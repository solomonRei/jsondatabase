package server.interfaces.impl.commands;

import com.google.gson.JsonObject;
import server.interfaces.Command;
import server.interfaces.impl.DatabaseCore;
import server.utils.ConsoleHandler;

/** Set command. */
public class SetCommand implements Command {

  private final DatabaseCore databaseCore;
  private final String key;
  private final String value;
  private JsonObject result = new JsonObject();

  /** Set command. */
  public SetCommand(final String key, final String value) {
    this.databaseCore = DatabaseCore.getInstance();
    this.key = key;
    this.value = value;
  }

  @Override
  public void execute() {
    databaseCore.setData(key, value);
    result.addProperty("response", ConsoleHandler.SUCCESSFUL_COMMAND);
  }

  @Override
  public JsonObject getResponse() {
    return result;
  }
}
