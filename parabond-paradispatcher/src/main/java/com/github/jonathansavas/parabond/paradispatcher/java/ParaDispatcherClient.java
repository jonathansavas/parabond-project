package com.github.jonathansavas.parabond.paradispatcher.java;

import com.github.jonathansavas.parabond.ParaDispatcher.ParaDispatcherGrpc;
import com.github.jonathansavas.parabond.ParaDispatcher.ParaDispatcherGrpc.ParaDispatcherBlockingStub;
import com.github.jonathansavas.parabond.ParaDispatcher.ParaDispatcherGrpc.ParaDispatcherStub;
import com.github.jonathansavas.parabond.ParaDispatcher.ParaDispatcherProto.GrpcJobInfo;
import com.github.jonathansavas.parabond.ParaDispatcher.ParaDispatcherProto.GrpcJobSize;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.concurrent.TimeUnit;

public class ParaDispatcherClient {
  private static final Logger logger = LogManager.getLogger(ParaDispatcherClient.class);
  private static final int DEFAULT_PORT = 9898;

  private final ManagedChannel channel;
  private final ParaDispatcherBlockingStub blockingStub;
  private final ParaDispatcherStub asyncStub;

  public ParaDispatcherClient() {
    this("localhost", DEFAULT_PORT);
  }

  public ParaDispatcherClient(String host) {
    this(host, DEFAULT_PORT);
  }

  /**
   * Construct client to ParaDispatcherServer at host:port
   * @param host Ip address of the host
   * @param port Port number the server is listening on
   */
  public ParaDispatcherClient(String host, int port) {
    this(ManagedChannelBuilder.forAddress(host, port).usePlaintext().build());
  }

  /**
   * Construct client to ParaDispatcherServer using existing channel.
   * @param channel
   */
  public ParaDispatcherClient(ManagedChannel channel) {
    this.channel = channel;
    this.blockingStub = ParaDispatcherGrpc.newBlockingStub(channel);
    this.asyncStub = ParaDispatcherGrpc.newStub(channel);
  }

  public void shutdown() throws InterruptedException {
    channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
  }

  public GrpcJobInfo processJob(int size) {
    GrpcJobSize request = GrpcJobSize.newBuilder().setN(size).build();
    GrpcJobInfo info;
    try {
      info = blockingStub.dispatch(request);
    } catch (StatusRuntimeException e) {
      logger.warn("RPC failed: {}", e.getStatus());
      return null;
    }
    return info;
  }
}
