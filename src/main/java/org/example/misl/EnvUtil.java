package org.example.misl;

import org.springframework.util.StringUtils;

public class EnvUtil {
  public static class ConnectionParams {
    public String host;
    public int port;
  }

  public static ConnectionParams defaultConnectionParams = new ConnectionParams();
  static {
    defaultConnectionParams.port = 10000;
    defaultConnectionParams.host = "localhost";
  }

  public static ConnectionParams getParams(String connURL) {
    if (StringUtils.isEmpty(connURL)) {
      return defaultConnectionParams;
    }
    String[] params = connURL.split(":");
    if (params == null || params.length != 2) {
      return defaultConnectionParams;
    }

    ConnectionParams ret = new EnvUtil.ConnectionParams();
    ret.host = params[0];
    try {
      ret.port = Integer.parseInt(params[1]);
    } catch (Throwable e) {
      ret.port = defaultConnectionParams.port;
    }
    return ret;

  }
}
