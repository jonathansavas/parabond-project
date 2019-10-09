package com.github.jonathansavas.parabond.paradispatcher.scala

import com.github.jonathansavas.parabond.ParaDispatcher.ParaDispatcherProto.{GrpcJobInfo, GrpcJobSize}
import com.github.jonathansavas.parabond.paradispatcher.java.ParaDispatcherUtil
import com.github.jonathansavas.parabond.paraworker.java.ParaWorkerClient
import io.grpc.ManagedChannelBuilder
import org.apache.logging.log4j.LogManager
import parabond.cluster._
import parabond.util.Result

/**
  * Dispatcher class to partition the analysis work. Checks the
  * Mongo DB for any missed portfolio queries.
  * @author Jonathan Savas
  */
class ParaDispatcher {
  val logger = LogManager.getLogger(classOf[ParaDispatcher])


  val RANGE = ParaDispatcherUtil.getIntEnvOrElse("RANGE", 5000)

  // Env variable specified in client k8's deployment yaml
  // k8's will resolve the service name via DNS to the service proxy, which in turn forwards the
  // request to any of the worker (pod) instances
  /*
    spec:
      containers:
      - name: paradispatcher
        image: jonathansavas/paradispatcher
        env:
        - name: PARAWORKER_SVC_HOST
          value: paraworker-server
        - name: PARAWORKER_SVC_PORT
          value: "9999"
   */
  val HOST_ENV = "PARAWORKER_SVC_HOST"
  val PORT_ENV = "PARAWORKER_SVC_PORT"

  val DEFAULT_WORKER_HOST = "localhost"
  val DEFAULT_WORKER_PORT = "9999"

  val wHost = ParaDispatcherUtil.getStringEnvOrElse(HOST_ENV, DEFAULT_WORKER_HOST)
  val wPort = ParaDispatcherUtil.getStringEnvOrElse(PORT_ENV, DEFAULT_WORKER_PORT)

  val channelToWorker = ManagedChannelBuilder.forTarget(s"${wHost}:${wPort}").usePlaintext().build

  /**
    * Partitions the work, sends to workers, and analyzes results.
    * @param jobSize Number of portfolios to anaylze
    * @return Timing and misses info about the job
    */
  def dispatch(jobSize: GrpcJobSize): GrpcJobInfo = {
    val size = jobSize.getN

    logger.info("Available processors: {}", Runtime.getRuntime.availableProcessors)
    logger.info("creating and sending partitions for n = {}", size)

    val portfIds = checkReset(size, 0)

    val parT0 = System.nanoTime

    val numPartitions = (size.toDouble / RANGE).ceil.toInt

    // All subsequent operations on "partitions" will now be done in parallel threads
    // Any new collections based on "partitions" will be "par" as well
    val partitions = (0 until numPartitions).par

    val ranges = for (k <- partitions) yield {
      val begin = k * RANGE
      val n = RANGE min size - begin

      (n, begin)
    }

    // Make each unary blocking RPC in its own thread
    val results = ranges.map { bounds =>
      val (n, begin) = bounds

      val client = new ParaWorkerClient(ManagedChannelBuilder.forTarget(s"${wHost}:${wPort}").usePlaintext().build)

      logger.info("Sending partition n={}, begin={}", n, begin)

      val grpcResult = client.priceBonds(n, begin)

      logger.info("Received result: {}", grpcResult)

      client.shutdown()

      Result(grpcResult.getT0, grpcResult.getT1)
    }

    val parTN = System.nanoTime - parT0

    logger.info("received results for size = {}", size)

    val serialT1 = results.foldLeft(0L) { (sum, result) =>
      val partialTime = result.t1 - result.t0

      sum + partialTime
    }

    // Check for database misses, should be none
    val misses = check(portfIds)

    GrpcJobInfo.newBuilder().setT1(serialT1).setTN(parTN).setMisses(misses.length).build()
  }
}
