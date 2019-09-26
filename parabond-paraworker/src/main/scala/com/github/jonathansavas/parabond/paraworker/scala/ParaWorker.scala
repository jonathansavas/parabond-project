package com.github.jonathansavas.parabond.paraworker.scala

import com.github.jonathansavas.parabond.ParaWorker.ParaWorkerProto.{GrpcPartition, GrpcResult}
import org.apache.logging.log4j.LogManager
import parabond.cluster.{FineGrainedNode, Partition}

/**
  * Worker class to analyze a partition of bond portfolios. Queries a
  * Mongo DB for portfolio and bond information.
  * @author Jonathan Savas
  */
class ParaWorker {
  private val logger = LogManager.getLogger(classOf[ParaWorker])

  logger.info("ParaWorker created")

  val node = new FineGrainedNode

  /**
    * Analyzes a partition of portfolios
    * @param grpcPartition Partition information for this worker.
    * @return GrpcResult containing timing information about the parition analysis
    */
  def work(grpcPartition: GrpcPartition): GrpcResult = {
    val partition = Partition(grpcPartition.getN, grpcPartition.getBegin)

    val analysis = node.analyze(partition)

    val partialT1 = analysis.results.foldLeft(0L) { (sum, job) =>
      val time = job.result.t1 - job.result.t0

      sum + time
    }

    GrpcResult.newBuilder().setT1(partialT1).setT0(0).build();
  }
}
