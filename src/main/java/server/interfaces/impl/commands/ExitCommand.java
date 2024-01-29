package server.interfaces.impl.commands;

import com.google.gson.JsonObject;
import server.core.ServerCore;
import server.interfaces.Command;
import server.utils.ConsoleHandler;

/** Exit command. */
public class ExitCommand implements Command {
  private JsonObject result = new JsonObject();

  /** Exit command execution. */
  @Override
  public void execute() {
    ServerCore.requestExit();
    result.addProperty("response", ConsoleHandler.SUCCESSFUL_COMMAND);
  }

  @Override
  public JsonObject getResponse() {
    return result;
  }
}
