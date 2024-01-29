package server.interfaces.impl;

import server.interfaces.Command;
import server.interfaces.impl.commands.DeleteCommand;
import server.interfaces.impl.commands.ExitCommand;
import server.interfaces.impl.commands.GetCommand;
import server.interfaces.impl.commands.SetCommand;

/** Command factory, used for command pattern. */
public class CommandFactory {

  /**
   * Creates a command based on the command type.
   *
   * @param type The type of the command.
   * @param key The key of the cell.
   * @param value The value of the cell.
   * @return The command.
   */
  public static Command createCommand(final String type, final String key, final String value) {
    switch (type) {
      case "exit":
        return new ExitCommand();
      case "get":
        return new GetCommand(key);
      case "set":
        return new SetCommand(key, value);
      case "delete":
        return new DeleteCommand(key);
      default:
        throw new IllegalArgumentException("Unsupported command type: " + type);
    }
  }
}
