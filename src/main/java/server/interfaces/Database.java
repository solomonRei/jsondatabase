package server.interfaces;

import com.google.gson.JsonElement;

/** Database interface. */
public interface Database {

  /**
   * Set data to database.
   *
   * @param key key
   * @param value value
   */
  void setData(String key, String value);

  /**
   * Get data from database.
   *
   * @param key key
   * @return data
   */
  JsonElement getData(String key);

  /**
   * Delete data from database.
   *
   * @param key key
   */
  void deleteData(String key);
}
