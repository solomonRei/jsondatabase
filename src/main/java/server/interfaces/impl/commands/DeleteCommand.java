package server.interfaces.impl.commands;

import com.google.gson.JsonObject;
import server.interfaces.Command;
import server.interfaces.impl.DatabaseCore;
import server.utils.ConsoleHandler;

/** Delete command. */
public class DeleteCommand implements Command {

  private final DatabaseCore databaseCore;
  private final String key;
  private JsonObject result = new JsonObject();

  public DeleteCommand(final String key) {
    this.databaseCore = DatabaseCore.getInstance();
    this.key = key;
  }

  @Override
  public void execute() {
    databaseCore.deleteData(key);
    result.addProperty("response", ConsoleHandler.SUCCESSFUL_COMMAND);
  }

  @Override
  public JsonObject getResponse() {
    return result;
  }
}
