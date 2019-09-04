// https://grpc.io/docs/tutorials/basic/java/
// https://developers.google.com/protocol-buffers/docs/proto3#simple

package com.github.jonathansavas.parabond.paraworker.java;

import com.github.jonathansavas.parabond.ParaWorker.ParaWorkerGrpc;
import com.github.jonathansavas.parabond.paraworker.scala.ParaWorker;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import com.github.jonathansavas.parabond.ParaWorker.ParaWorkerProto.GrpcPartition;
import com.github.jonathansavas.parabond.ParaWorker.ParaWorkerProto.GrpcResult;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ParaWorkerServer {
  private static final Logger logger = LogManager.getLogger(ParaWorkerServer.class);
  private static final int DEFAULT_PORT = 9999;

  private final int port;
  private final Server server;

  public ParaWorkerServer() {
    this(DEFAULT_PORT);
  }

  public ParaWorkerServer(int port) {
    this.port = port;
    this.server = ServerBuilder.forPort(port).addService(new ParaWorkerService()).build();
  }

  public void start() throws IOException {
    server.start();
    logger.info("Server started, listening on port " + port);
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        System.err.println("*** shutting down gRPC server since JVM is shutting down");
        ParaWorkerServer.this.stop();
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

  public static void main(String[] args) throws InterruptedException {
    int port = ParaWorkerUtil.getPortOrElse(DEFAULT_PORT);

    ParaWorkerServer server = new ParaWorkerServer(port);

    try {
      server.start();
    } catch (IOException e) {
      logger.warn("ParaWorkerServer startup failed: {}", e.getMessage());
    }

    server.blockUntilShutdown();
  }

  private static class ParaWorkerService extends ParaWorkerGrpc.ParaWorkerImplBase {

    @Override
    public void work(GrpcPartition partition, StreamObserver<GrpcResult> responseObserver) {
      responseObserver.onNext(new ParaWorker().work(partition));
      responseObserver.onCompleted();
    }
  }
}
