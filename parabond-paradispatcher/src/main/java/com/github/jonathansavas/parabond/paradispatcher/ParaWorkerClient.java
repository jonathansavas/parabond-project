package com.github.jonathansavas.parabond.paradispatcher;

import com.github.jonathansavas.parabond.ParaWorker.ParaWorkerGrpc;
import com.github.jonathansavas.parabond.ParaWorker.ParaWorkerGrpc.ParaWorkerBlockingStub;
import com.github.jonathansavas.parabond.ParaWorker.ParaWorkerGrpc.ParaWorkerStub;
import com.github.jonathansavas.parabond.ParaWorker.ParaWorkerProto.Result;
import com.github.jonathansavas.parabond.ParaWorker.ParaWorkerProto.Partition;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.TimeUnit;

public class ParaWorkerClient {
  private static final Logger logger = LogManager.getLogger(ParaWorkerClient.class);

  private final ManagedChannel channel;
  private final ParaWorkerBlockingStub blockingStub;
  private final ParaWorkerStub asyncStub;

  /**
   * Construct client to ParaWorkerServer at host:port
   * @param host Ip address of the host
   * @param port Port number the server is listening on
   */
  public ParaWorkerClient(String host, int port) {
    this(ManagedChannelBuilder.forAddress(host, port).usePlaintext());
  }

  /**
   * Construct client to ParaWorkerServer using existing channel.
   * @param channelBuilder
   */
  public ParaWorkerClient(ManagedChannelBuilder<?> channelBuilder) {
    this.channel = channelBuilder.build();
    this.blockingStub = ParaWorkerGrpc.newBlockingStub(channel);
    this.asyncStub = ParaWorkerGrpc.newStub(channel);
  }

  public void shutdown() throws InterruptedException {
    channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
  }

  public Result priceBonds(int n) {
    Partition partition = Partition.newBuilder().setN(n).build();
    Result result;
    try {
      result = blockingStub.work(partition);
    } catch (StatusRuntimeException e) {
      logger.warn("RPC failed: {}", e.getStatus());
      return null;
    }
    return result;
  }
}
