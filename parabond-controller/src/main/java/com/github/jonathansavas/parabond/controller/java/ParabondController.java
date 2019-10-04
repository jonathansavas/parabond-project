package com.github.jonathansavas.parabond.controller.java;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.web.bind.annotation.*;
import com.github.jonathansavas.parabond.paradispatcher.java.ParaDispatcherUtil;
import com.github.jonathansavas.parabond.ParaDispatcher.ParaDispatcherProto.GrpcJobInfo;
import com.github.jonathansavas.parabond.paradispatcher.java.ParaDispatcherClient;

/**
 * REST controller for the parabond project.
 */
@RestController
@EnableAutoConfiguration
public class ParabondController {

  String HOST_ENV = "PARADISPATCHER_SVC_HOST";
  String PORT_ENV = "PARADISPATCHER_SVC_PORT";

  String DEFAULT_DISPATCHER_HOST = "localhost";
  String DEFAULT_DISPATCHER_PORT = "9898";

  String dHost = ParaDispatcherUtil.getStringEnvOrElse(HOST_ENV, DEFAULT_DISPATCHER_HOST);
  String dPort = ParaDispatcherUtil.getStringEnvOrElse(PORT_ENV, DEFAULT_DISPATCHER_PORT);

  ManagedChannel channelToDispatcher = ManagedChannelBuilder.forTarget(dHost + ":" + dPort).usePlaintext().build();

  /**
   * Endpoint to price a job and return timing information.
   * @param size Number of portfolios to analyze
   * @return Response with information about the request
   * @throws InterruptedException
   */
  @RequestMapping("/price")
  public Response timePricingRequest(@RequestParam(value="size", defaultValue="25000") int size) throws InterruptedException {
    if (size < 1) size = 1;
    if (size > 100000) size = 100000;
    ParaDispatcherClient client = new ParaDispatcherClient(channelToDispatcher);
    GrpcJobInfo info = client.processJob(size);
    return new Response(info, size);
  }

  /**
   * Test endpoint
   * @param msg
   * @return
   */
  @RequestMapping("/test")
  public String testResponse(@RequestParam(value="param", defaultValue="test_response") String msg) {
    return "test endpoint, response: " + msg;
  }


  /**
   * Main method to run the application
   * @param args
   */
  public static void main(String[] args) {
    SpringApplication.run(ParabondController.class, args);
  }
}
