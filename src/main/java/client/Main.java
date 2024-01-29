package client;

import client.core.ClientCore;
import client.utils.Config;
import client.utils.JsonStringConverter;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/** Main class. */
public class Main {

  @Parameter(names = "-t", description = "Type of the operation (get, set, delete)")
  private String operationType;

  @Parameter(names = "-k", description = "Key of the cell")
  private String key;

  @Parameter(
      names = "-v",
      description = "Value to store in the cell",
      converter = JsonStringConverter.class)
  private String value;

  @Parameter(names = "-in", description = "Input file with the request")
  private String inputFile;

  /**
   * Reads the request from a file.
   *
   * @param args The file to read the request from.
   */
  public static void main(String[] args) {
    Main clientApplication = new Main();

    var commander = JCommander.newBuilder().addObject(clientApplication).build();

    try {
      commander.parse(args);
      clientApplication.run();
    } catch (Exception e) {
      System.err.println("Error parsing arguments: " + e.getMessage());
      commander.usage();
    }
  }

  /** Starts the client application. */
  public void run() {
    String requestMessage = (inputFile != null) ? readFile(inputFile) : buildRequestString();

    try (ClientCore client = new ClientCore(Config.SERVER_ADDRESS, Config.SERVER_PORT)) {
      client.sendRequest(requestMessage);
    } catch (IOException e) {
      System.err.println("Client failed to start: " + e.getMessage());
    }
  }

  /**
   * Builds the request string based on the command line arguments.
   *
   * @return The request string.
   */
  private String buildRequestString() {
    JsonObject requestJson = new JsonObject();
    requestJson.addProperty("type", operationType);

    if (key != null && !key.isEmpty()) {
      requestJson.addProperty("key", key);
    }

    if (value != null && !value.isEmpty()) {
      requestJson.addProperty("value", value);
    }

    return requestJson.toString();
  }

  /**
   * Reads the request from a file.
   *
   * @param inputFile The file to read the request from.
   * @return The request as a string.
   */
  private String readFile(String inputFile) {
    try {
      var path = Paths.get(System.getProperty("user.dir"), "src/client/data", inputFile);
      return new String(Files.readAllBytes(Paths.get(path.toString())), StandardCharsets.UTF_8);
    } catch (IOException e) {
      System.err.println("Error reading the input file: " + e.getMessage());
      throw new UncheckedIOException(e);
    }
  }
}
