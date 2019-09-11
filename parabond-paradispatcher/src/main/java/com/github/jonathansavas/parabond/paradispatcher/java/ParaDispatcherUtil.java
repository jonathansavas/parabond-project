package com.github.jonathansavas.parabond.paradispatcher.java;

public class ParaDispatcherUtil {

  public static int getIntPropOrElse(String property, int defaultProp) {
    String prop = System.getProperty(property);
    return prop == null ? defaultProp : Integer.parseInt(prop);
  }

  public static String getStringPropOrElse(String property, String defaultProp) {
    String prop = System.getProperty(property);
    return prop == null ? defaultProp : prop;
  }

  public static int getPortOrElse(int defaultPort) {
    String port = System.getProperty("port");
    return port == null ? defaultPort : Integer.parseInt(port);
  }

  public static String getStringEnvOrElse(String env, String defaultEnv) {
    String var = System.getenv(env);
    return var == null || var.isEmpty() ? defaultEnv : var;
  }
}
