package server;

import java.io.IOException;
import server.core.ServerCore;
import server.utils.Config;

/** Main class. */
public class Main {
  /** Main method. */
  public static void main(String[] args) {
    var server = new ServerCore(Config.SERVER_PORT);
    try {
      server.startServer();
    } catch (IOException e) {
      System.err.println("Server failed to start: " + e.getMessage());
    }
  }
}
