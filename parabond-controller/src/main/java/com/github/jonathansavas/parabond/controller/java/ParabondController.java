package com.github.jonathansavas.parabond.controller.java;

import com.github.jonathansavas.parabond.ParaDispatcher.ParaDispatcherProto;
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
   */
  @RequestMapping("/price")
  public BatchResponse timePricingRequest(@RequestParam(value="size", defaultValue="100") int size) {
    if (size < 1) size = 1;
    else if (size > 100000) size = 100000;

    ParaDispatcherClient client = new ParaDispatcherClient(channelToDispatcher);
    GrpcJobInfo info = client.processJob(size);

    return new BatchResponse(info, size);
  }

  @RequestMapping("/query/bond")
  public BondResponse queryBond(@RequestParam(value="id", defaultValue = "1") int id) {
    if (id < 1) id = 1;
    else if (id > 5000) id = 5000;

    ParaDispatcherClient client = new ParaDispatcherClient(channelToDispatcher);
    ParaDispatcherProto.GrpcBond bond = client.queryBond(id);

    return new BondResponse(bond);
  }

  @RequestMapping("/query/portfolio")
  public PortfResponse queryPortfolio(@RequestParam(value="id", defaultValue = "1") int id) {
    if (id < 1) id = 1;
    else if (id > 100000) id = 100000;

    ParaDispatcherClient client = new ParaDispatcherClient(channelToDispatcher);
    ParaDispatcherProto.GrpcPortf portfolio = client.queryPortfolio(id);
    
    return new PortfResponse(portfolio);
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
