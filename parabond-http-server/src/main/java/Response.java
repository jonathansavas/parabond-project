import com.github.jonathansavas.parabond.ParaDispatcher.ParaDispatcherProto;

/**
 * Class to respond with information about the user request.
 */
public class Response {
  private int numPortfolios;
  private String t1;
  private String tN;
  private int numMisses;

  /**
   * Constructs a Response object
   * @param info GrpcJobInfo with timing and misses information.
   * @param numPortfolios Number of porfolios analyzed
   */
  public Response(ParaDispatcherProto.GrpcJobInfo info, int numPortfolios) {
    this.numPortfolios = numPortfolios;
    this.t1 = info.getT1() / 1000000000.0 + "s";
    this.tN = info.getTN() / 1000000000.0 + "s";
    this.numMisses = info.getMisses();
  }

  public int getNumPortfolios() {
    return numPortfolios;
  }

  public String getT1() {
    return t1 != null ? t1 : "null";
  }

  public String gettN() {
    return tN != null ? tN : "null;";
  }

  public int getNumMisses() {
    return numMisses;
  }
}
