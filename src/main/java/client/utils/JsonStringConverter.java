package client.utils;

import com.beust.jcommander.IStringConverter;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

/** This class converts a string to a JSON string. */
public class JsonStringConverter implements IStringConverter<String> {

  @Override
  public String convert(final String value) {
    try {
      JsonElement jsonElement = JsonParser.parseString(value);
      return jsonElement.toString();
    } catch (JsonSyntaxException e) {
      System.err.println("Invalid JSON input66: " + e.getMessage());
      return value;
    }
  }
}
