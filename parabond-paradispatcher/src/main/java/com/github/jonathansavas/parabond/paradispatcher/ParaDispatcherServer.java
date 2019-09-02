// https://grpc.io/docs/tutorials/basic/java/
// https://developers.google.com/protocol-buffers/docs/proto3#simple

package com.github.jonathansavas.parabond.paradispatcher;

import com.github.jonathansavas.parabond.ParaDispatcher.ParaDispatcherGrpc;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import com.github.jonathansavas.parabond.ParaDispatcher.ParaDispatcherProto.JobInfo;
import com.github.jonathansavas.parabond.ParaDispatcher.ParaDispatcherProto.JobSize;

import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ParaDispatcherServer {
  private static final Logger logger = LogManager.getLogger(ParaDispatcherServer.class.getName());

  private final int port;
  private final Server server;

  public ParaDispatcherServer(int port) {
    this.port = port;
    this.server = ServerBuilder.forPort(port).addService(new ParaDispatcherService()).build();
  }

  public void start() throws IOException {
    server.start();
    logger.info("Server started, listening on port " + port);
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        System.err.println("*** shutting down gRPC server since JVM is shutting down");
        ParaDispatcherServer.this.stop();
        System.err.println("*** server shut down");
      }
    });
  }

  public void stop() {
    if (server != null) {
      server.shutdown();
    }
  }

  private void blockUntilShutdown() throws InterruptedException {
    if (server != null) {
      server.awaitTermination();
    }
  }

  private static class ParaDispatcherService extends ParaDispatcherGrpc.ParaDispatcherImplBase {

    @Override
    public void dispatch(JobSize jobSize, StreamObserver<JobInfo> jobInfo) {

    }
  }
}
