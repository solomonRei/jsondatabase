package server.interfaces;

import com.google.gson.JsonObject;

/** Command interface, used for command pattern. */
public interface Command {

  /** Execute method. */
  void execute();

  /** Get result method. */
  JsonObject getResponse();
}
