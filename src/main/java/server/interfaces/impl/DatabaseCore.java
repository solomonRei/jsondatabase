package server.interfaces.impl;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import server.interfaces.Database;

/** Database core using JSON for storage. */
public class DatabaseCore implements Database {

  /** Database core instance. */
  private static volatile DatabaseCore instance;

  /** Path to the server folder. */
  private static final String serverFolderPath =
      System.getProperty("user.dir")
          + File.separator
          + "src"
          + File.separator
          + "server"
          + File.separator
          + "data";

  /** Path to the database file. */
  private static final String filename = "db.json";

  /** Path to the database file. */
  private static final String databaseFilePath = serverFolderPath + File.separator + filename;

  /** Lock for the database. */
  private final ReadWriteLock lock = new ReentrantReadWriteLock();

  /** Gson instance. */
  private Gson gson = new Gson();

  private DatabaseCore() {
    loadDatabase();
  }

  /**
   * Get the singleton instance of DatabaseCore.
   *
   * @return database core instance
   */
  public static DatabaseCore getInstance() {
    if (instance == null) {
      synchronized (DatabaseCore.class) {
        if (instance == null) {
          instance = new DatabaseCore();
        }
      }
    }
    return instance;
  }

  /**
   * Load the database from the JSON storage.
   *
   * @return database as JsonObject
   */
  private JsonObject loadDatabase() {
    try {
      Reader reader = Files.newBufferedReader(Paths.get(databaseFilePath));
      JsonReader jsonReader = new JsonReader(reader);
      jsonReader.setLenient(true);
      JsonObject jsonObject = gson.fromJson(jsonReader, JsonObject.class);
      reader.close();

      return jsonObject == null ? new JsonObject() : jsonObject;

    } catch (IOException e) {
      return new JsonObject();
    }
  }

  /**
   * Save the database to the JSON storage.
   *
   * @param database database as JsonObject
   */
  private void saveDatabase(final JsonObject database) {
    try (Writer writer = new FileWriter(String.valueOf(Paths.get(databaseFilePath)))) {
      gson.toJson(database, writer);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  /**
   * Set data in the JSON storage.
   *
   * @param key key
   * @param valueJson value
   */
  public void setData(final String key, final String valueJson) {
    Lock writeLock = lock.writeLock();
    writeLock.lock();
    try {
      var database = loadDatabase();
      var path = parseJsonPath(key);
      JsonElement valueElement;

      try {
        valueElement = gson.fromJson(valueJson, JsonElement.class);
        if (valueElement.isJsonPrimitive() && valueElement.getAsJsonPrimitive().isNumber()) {
          valueElement = new JsonPrimitive(valueElement.getAsString());
        }
      } catch (com.google.gson.JsonSyntaxException e) {
        valueElement = new JsonPrimitive(valueJson);
      }

      setJsonValue(database, path, valueElement);
      System.out.println("Saving database" + database);
      saveDatabase(database);
    } catch (Exception e) {
      System.out.println(e.getMessage());
    } finally {
      writeLock.unlock();
    }
  }

  /**
   * Set data in the JSON storage.
   *
   * @param database key
   * @param path path
   * @param value value
   */
  private void setJsonValue(final JsonObject database, final JsonArray path, final JsonElement value) {
    JsonObject currentObject = database;
    for (int i = 0; i < path.size() - 1; i++) {
      var key = path.get(i).getAsString();
      var nextElement = currentObject.get(key);
      if (nextElement == null || !nextElement.isJsonObject()) {
        nextElement = new JsonObject();
        currentObject.add(key, nextElement);
      }
      currentObject = (JsonObject) nextElement;
    }
    var finalKey = path.get(path.size() - 1).getAsString();
    currentObject.add(finalKey, value);
  }

  /**
   * Get data from the JSON storage.
   *
   * @param key key
   * @return value as String
   */
  public JsonElement getData(final String key) {
    Lock readLock = lock.readLock();
    readLock.lock();
    try {
      var database = loadDatabase();
      var path = parseJsonPath(key);
      var resultElement = getJsonValue(database, path);
      if (resultElement == null) {
        throw new IllegalArgumentException("No such key");
      }
      return resultElement;
    } finally {
      readLock.unlock();
    }
  }

  /**
   * Get data from the JSON storage.
   *
   * @param database the JSON object representing the database
   * @param path the path as a JsonArray
   * @return value as String
   */
  private JsonElement getJsonValue(final JsonObject database, final JsonArray path) {
    var currentObject = database;
    for (int i = 0; i < path.size(); i++) {
      var currentKey = path.get(i).getAsString();
      if (!currentObject.has(currentKey)) {
        return null;
      }
      if (i == path.size() - 1) {
        return currentObject.get(currentKey);
      }
      currentObject = currentObject.getAsJsonObject(currentKey);
    }
    return null;
  }

  /**
   * Delete data from the JSON storage.
   *
   * @param key key
   */
  public void deleteData(final String key) {
    Lock writeLock = lock.writeLock();
    writeLock.lock();
    try {
      var database = loadDatabase();
      var path = parseJsonPath(key);

      if (!deleteJsonValue(database, path)) {
        throw new IllegalArgumentException("No such key");
      }

      saveDatabase(database);
    } finally {
      writeLock.unlock();
    }
  }

  /**
   * Delete data from the JSON storage.
   *
   * @param database the JSON object representing the database
   * @param path the path as a JsonArray
   * @return true if deletion was successful, false otherwise
   */
  private boolean deleteJsonValue(final JsonObject database, final JsonArray path) {
    if (path.size() == 0) {
      return false;
    }

    JsonObject currentObject = database;
    for (int i = 0; i < path.size() - 1; i++) {
      var currentKey = path.get(i).getAsString();
      var nextElement = currentObject.get(currentKey);

      if (nextElement == null || !nextElement.isJsonObject()) {
        return false;
      }
      currentObject = nextElement.getAsJsonObject();
    }

    var finalKey = path.get(path.size() - 1).getAsString();
    return currentObject.remove(finalKey) != null;
  }

  /**
   * Parse the JSON path.
   *
   * @param jsonPath the JSON path as a string
   * @return the JSON path as a JsonArray
   */
  private JsonArray parseJsonPath(final String jsonPath) {
    try {
      return gson.fromJson(jsonPath, JsonArray.class);
    } catch (com.google.gson.JsonSyntaxException e) {
      try {
        var primitive = gson.fromJson(jsonPath, JsonPrimitive.class);
        if (primitive.isString()
            && primitive.getAsString().startsWith("[")
            && primitive.getAsString().endsWith("]")) {
          return gson.fromJson(primitive.getAsString(), JsonArray.class);
        } else {
          var singlePathArray = new JsonArray();
          singlePathArray.add(primitive);
          return singlePathArray;
        }
      } catch (com.google.gson.JsonSyntaxException innerException) {
        System.err.println("Invalid JSON input: " + innerException.getMessage());
        return new JsonArray();
      }
    }
  }
}
