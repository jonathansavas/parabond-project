import com.github.jonathansavas.parabond.paradispatcher.java.ParaDispatcherUtil;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.web.bind.annotation.*;

import com.github.jonathansavas.parabond.ParaDispatcher.ParaDispatcherProto.GrpcJobInfo;
import com.github.jonathansavas.parabond.paradispatcher.java.ParaDispatcherClient;

@RestController
@EnableAutoConfiguration
public class HttpServer {

  String HOST_ENV = "PARADISPATCHER_SVC_HOST";
  String PORT_ENV = "PARADISPATCHER_SVC_PORT";

  String DEFAULT_WORKER_HOST = "localhost";
  String DEFAULT_WORKER_PORT = "9999";

  String dHost = ParaDispatcherUtil.getStringEnvOrElse(HOST_ENV, DEFAULT_WORKER_HOST);
  String dPort = ParaDispatcherUtil.getStringEnvOrElse(PORT_ENV, DEFAULT_WORKER_PORT);

  @RequestMapping("/price")
  public Response timePricingRequest(@RequestParam(value="size", defaultValue="25000") int size) {
    ParaDispatcherClient client = new ParaDispatcherClient(dHost, dPort);
    GrpcJobInfo info = client.processJob(size);
    return new Response(info, size);
  }


  public static void main(String[] args) {
    SpringApplication.run(HttpServer.class, args);
  }
}
