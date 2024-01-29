package server.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.interfaces.Command;
import server.interfaces.impl.CommandFactory;

/**
 * This class handles the console input and output.
 */
public class ConsoleHandler {

  /** Logger object to log messages to the console. */
  private static final Logger LOGGER = Logger.getLogger(ConsoleHandler.class.getName());

  /** The error message for invalid command. */
  public static final String SUCCESSFUL_COMMAND = "OK";

  /** The message to print when the server is started. */
  public static final String SERVER_STARTED = "Server started!";

  /**
   * This method prints a message to the console.
   *
   * @param message The message to print.
   */
  public static void printMessage(final String message) {
    System.out.println(message);
  }

  /**
   * This method parses the input from the client side and returns a Command object.
   *
   * @param commandObj The user input.
   * @return A Command object.
   * @throws IllegalArgumentException If the input is invalid.
   */
  public static Command parseCommand(final JsonObject commandObj) {
    try {
      String type = commandObj.get("type").getAsString();
      JsonElement valueElement = commandObj.has("value") ? commandObj.get("value") : null;

      String value = null;
      if (valueElement != null) {
        if (valueElement.isJsonPrimitive() && valueElement.getAsJsonPrimitive().isString()) {
          value = valueElement.getAsString();
        } else if (valueElement.isJsonObject()) {
          value = valueElement.toString();
        } else {
          throw new IllegalArgumentException("Value is of an unsupported type");
        }
      }

      String key = "";
      JsonElement keyElement = commandObj.get("key");
      if (keyElement != null) {
        if (keyElement.isJsonArray()) {
          var keyArray = keyElement.getAsJsonArray();
          key = keyArray.toString();
        } else {
          key = keyElement.getAsString();
        }
      }
      return CommandFactory.createCommand(type, key, value);
    } catch (NumberFormatException e) {
      LOGGER.log(Level.SEVERE, "Invalid number format: {0}", e.getMessage());
      throw new IllegalArgumentException(e.getMessage());
    } catch (IllegalArgumentException e) {
      LOGGER.log(Level.SEVERE, "Invalid command: {0}", e.getMessage());
      throw e;
    }
  }
}
