import com.github.jonathansavas.parabond.ParaDispatcher.ParaDispatcherProto;

public class Response {
  private int numPortfolios;
  private String t1;
  private String tN;
  private int numMisses;

  public Response(ParaDispatcherProto.GrpcJobInfo info, int numPortfolios) {
    this.numPortfolios = numPortfolios;
    this.t1 = info.getT1() / 1000000000.0 + "s";
    this.tN = info.getTN() / 1000000000.0 + "s";
    this.numMisses = info.getMisses();
  }
}
