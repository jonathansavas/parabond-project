package com.github.jonathansavas.parabond.paradispatcher.java;

public class ParaDispatcherUtil {

  public static int getPortOrElse(int defaultPort) {
    String port = System.getProperty("port");
    return port == null ? defaultPort : Integer.parseInt(port);
  }
}
