package com.taursys.debug;

public class Debug {
  private static Debug singleton = null;
  private LoggerAdapter loggerAdapter = null;
  public static final int DEBUG   = 4;
  public static final int INFO    = 3;
  public static final int WARN    = 2;
  public static final int ERROR   = 1;
  public static final int FATAL   = 0;
  public static final String[] LEVEL_NAMES = {
      "FATAL",
      "ERROR",
      "WARN",
      "INFO",
      "DEBUG",
      "UNKNOWN",
      };

  private Debug() {
    loggerAdapter = new SimpleLogger();
  }

  public static Debug getInstance() {
    if (singleton == null)
      singleton = new Debug();
    return singleton;
  }

  public static void setLoggerAdapter(LoggerAdapter loggerAdapter) {
    if (loggerAdapter == null)
      throw new IllegalArgumentException("LoggerAdapter cannot be null");
    getInstance().loggerAdapter = loggerAdapter;
  }

  public static LoggerAdapter getLoggerAdapter() {
    return getInstance().loggerAdapter;
  }

  public static void debug(Object message) {
    getInstance().loggerAdapter.debug(message);
  }

  public static void debug(Object message, Throwable t) {
    getInstance().loggerAdapter.debug(message, t);
  }

  public static void info(Object message) {
    getInstance().loggerAdapter.info(message);
  }

  public static void info(Object message, Throwable t) {
    getInstance().loggerAdapter.info(message, t);
  }

  public static void warn(Object message) {
    getInstance().loggerAdapter.warn(message);
  }

  public static void warn(Object message, Throwable t) {
    getInstance().loggerAdapter.warn(message, t);
  }

  public static void error(Object message) {
    getInstance().loggerAdapter.error(message);
  }

  public static void error(Object message, Throwable t) {
    getInstance().loggerAdapter.error(message, t);
  }

  public static void fatal(Object message) {
    getInstance().loggerAdapter.fatal(message);
  }

  public static void fatal(Object message, Throwable t) {
    getInstance().loggerAdapter.fatal(message, t);
  }

  public static boolean isDebugEnabled() {
    return getInstance().loggerAdapter.isDebugEnabled();
  }

  public static boolean isInfoEnabled() {
    return getInstance().loggerAdapter.isInfoEnabled();
  }

  public boolean isEnabledFor(int level) {
    return getInstance().loggerAdapter.isEnabledFor(level);
  }

  public static void log(int level, String message) {
    getInstance().loggerAdapter.log(level, message);
  }

  public static void log(int level, String message, Throwable t) {
    getInstance().loggerAdapter.log(level, message, t);
  }
}
