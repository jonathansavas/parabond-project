import com.github.jonathansavas.parabond.ParaDispatcher.ParaDispatcherProto.GrpcJobInfo;
import com.github.jonathansavas.parabond.paradispatcher.java.ParaDispatcherClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DispatcherDriver {
  private static final Logger logger = LogManager.getLogger(DispatcherDriver.class);

  public static void main(String[] args) throws InterruptedException {
    ParaDispatcherClient client = new ParaDispatcherClient();

    GrpcJobInfo jobInfo = client.processJob(150);

    System.out.println(String.format("Job Info: T1 = %f, TN = %f, Misses = %d", jobInfo.getT1() / 1000000000.0, jobInfo.getTN() / 1000000000.0, jobInfo.getMisses()));
    client.shutdown();
  }
}
