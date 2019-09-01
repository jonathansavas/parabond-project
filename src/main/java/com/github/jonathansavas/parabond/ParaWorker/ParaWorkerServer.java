// https://grpc.io/docs/tutorials/basic/java/
// https://developers.google.com/protocol-buffers/docs/proto3#simple

package com.github.jonathansavas.parabond.ParaWorker;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import com.github.jonathansavas.parabond.ParaWorker.ParaWorkerProto.Partition;
import com.github.jonathansavas.parabond.ParaWorker.ParaWorkerProto.Result;

import java.io.IOException;
import java.util.logging.Logger;
import java.util.logging.Level;

public class ParaWorkerServer {
  private static final Logger logger = Logger.getLogger(ParaWorkerServer.class.getName());

  private final int port;
  private final Server server;

  public ParaWorkerServer(int port) {
    this.port = port;
    this.server = ServerBuilder.forPort(port).addService(new ParaWorkerService()).build();
  }

  public void start() throws IOException {
    server.start();
    logger.info("Server started, listening on port " + port);
  }

  private static class ParaWorkerService extends ParaWorkerGrpc.ParaWorkerImplBase {

    @Override
    public void work(Partition partition, StreamObserver<Result> responseObserver) {

    }
  }
}
