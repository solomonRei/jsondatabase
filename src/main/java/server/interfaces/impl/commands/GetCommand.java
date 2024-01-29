package server.interfaces.impl.commands;

import com.google.gson.JsonObject;
import server.interfaces.Command;
import server.interfaces.impl.DatabaseCore;
import server.utils.ConsoleHandler;

/** Get command. */
public class GetCommand implements Command {
  private final DatabaseCore databaseCore;
  private final String key;
  private JsonObject result = new JsonObject();

  public GetCommand(final String key) {
    this.databaseCore = DatabaseCore.getInstance();
    this.key = key;
  }

  @Override
  public void execute() {
    var valueElement = databaseCore.getData(key);
    result.addProperty("response", ConsoleHandler.SUCCESSFUL_COMMAND);

    if (valueElement.isJsonPrimitive() && valueElement.getAsJsonPrimitive().isString()) {
      result.add("value", valueElement.getAsJsonPrimitive());
    } else {
      result.add("value", valueElement);
    }
  }

  @Override
  public JsonObject getResponse() {
    return result;
  }
}
