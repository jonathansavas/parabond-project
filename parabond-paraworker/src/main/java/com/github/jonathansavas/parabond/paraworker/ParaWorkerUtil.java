package com.github.jonathansavas.parabond.paraworker;

public class ParaWorkerUtil {

  public static int getPortOrElse(int defaultPort) {
    String port = System.getProperty("port");
    return port == null ? defaultPort : Integer.parseInt(port);
  }
}
