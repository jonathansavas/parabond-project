package com.github.jonathansavas.parabond.paraworker.java;

import java.util.Properties;

public class ParaWorkerUtil {

  public static int getIntPropOrElse(String property, int defaultProp) {
    String prop = System.getProperty(property);
    return prop == null ? defaultProp : Integer.parseInt(prop);
  }

  public static int getIntPropOrElse(Properties props, String property, int defaultProp) {
    String prop = props.getProperty(property);
    return prop == null ? defaultProp : Integer.parseInt(prop);
  }

  public static String getStringPropOrElse(String property, String defaultProp) {
    String prop = System.getProperty(property);
    return prop == null ? defaultProp : prop;
  }
}
