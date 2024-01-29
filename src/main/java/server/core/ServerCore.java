package server.core;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import server.interfaces.Command;
import server.interfaces.impl.commands.ExitCommand;
import server.utils.Config;
import server.utils.ConsoleHandler;

/** Server core class which starts the server and processes requests. */
public class ServerCore {
  /** The port to listen on. */
  private final int port;

  /** Flag to indicate if the application is running. */
  private static boolean isRunning = true;

  private final ExecutorService executor =
      Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 3);

  public ServerCore(final int port) {
    this.port = port;
  }

  /**
   * Starts the server.
   *
   * @throws IOException if an I/O error occurs
   */
  public void startServer() throws IOException {
    try (ServerSocket serverSocket =
        new ServerSocket(this.port, 50, InetAddress.getByName(Config.SERVER_ADDRESS))) {
      ConsoleHandler.printMessage(ConsoleHandler.SERVER_STARTED);
      while (isRunning) {
        executor.submit(
            () -> {
              try (Socket clientSocket = serverSocket.accept();
                  var input = new DataInputStream(clientSocket.getInputStream());
                  var output = new DataOutputStream(clientSocket.getOutputStream())) {
                var command = input.readUTF();
                var response = processCommand(command);
                output.writeUTF(response);
              } catch (IOException ignored) {
                System.out.println("Client disconnected");
              }
            });
      }
    }
  }

  /**
   * Processes a command.
   *
   * @param commandJson the command to process
   * @return the response to the command
   */
  private String processCommand(final String commandJson) {
    try {
      var commandObj = JsonParser.parseString(commandJson).getAsJsonObject();
      var type = commandObj.get("type").getAsString();
      Command command;

      if ("exit".equals(type)) {
        isRunning = false;
        command = new ExitCommand();
        stopServer();
      } else {
        command = ConsoleHandler.parseCommand(commandObj);
      }

      command.execute();
      return command.getResponse().toString();
    } catch (Exception e) {
      var errorJson = new JsonObject();
      errorJson.addProperty("response", "ERROR");
      errorJson.addProperty("reason", e.getMessage());
      return errorJson.toString();
    }
  }

  /** Requests the application to exit. */
  public static void requestExit() {
    isRunning = false;
  }

  /** Stops the server. */
  public void stopServer() {
    executor.shutdown();
    try {
      if (!executor.awaitTermination(800, TimeUnit.MILLISECONDS)) {
        executor.shutdownNow();
        requestExit();
      }
    } catch (InterruptedException e) {
      executor.shutdownNow();
    }
  }
}
