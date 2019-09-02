import com.github.jonathansavas.parabond.ParaWorker.ParaWorkerProto.Result;
import com.github.jonathansavas.parabond.paradispatcher.ParaWorkerClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DispatcherDriver {
  private static final Logger logger = LogManager.getLogger(DispatcherDriver.class);

  public static void main(String[] args) throws InterruptedException {
    ParaWorkerClient client = new ParaWorkerClient("localhost", 9999);
    int n = 555;
    Result a = client.priceBonds(n);
    System.out.println(String.format("Input n = %d should equal result.portfId = %d", n, a.getPortfId()));
    n = 0;
    a = client.priceBonds(n);
    System.out.println(String.format("Input n = %d should equal result.portfId = %d", n, a.getPortfId()));
    client.shutdown();
  }
}
