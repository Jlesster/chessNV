package com.jless.chess;

import java.awt.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import com.fasterxml.jackson.databind.*;

/**
 * The Colours class is a 'Utility Class'.
 *
 * Instead of hard-coding colors like 'Color.RED' throughout the project,
 * we use this class to load a theme from an external JSON file.
 * This allows us to change the entire look of the game just by editing a
 * text file, without having to rewrite and recompile the Java code.
 */
public class Colours {
  /**
   * 'static' means this Map belongs to the class itself, not to any specific object.
   * 'Map' is like a dictionary: it pairs a 'Key' (the name of the color, e.g., "BoardLight")
   * with a 'Value' (the actual Color object).
   *
   * A HashMap allows us to find a color almost instantly if we know its name.
   */
  private static final Map<String, Color> colors = new HashMap<>();

  /**
   * This is a 'static block'. It runs exactly once when the program first loads
   * the Colours class into memory. We use it to load our theme file immediately.
   */
  static {
    loadFromJSON("colours/Colors.json");
  }

  /**
   * loadFromJSON reads a text file (JSON format) and converts it into Java Color objects.
   */
  public static void loadFromJSON(String path) {
    try {
      /**
       * ClassLoader.getResourceAsStream is used to find files that are bundled
       * inside the program's JAR file. It treats the file as a 'Stream' of data.
       */
      InputStream in = Colours.class.getClassLoader().getResourceAsStream(path);
      if (in == null) {
        System.err.println("[Colours] failed to load resource" + path);
        return;
      }

      /**
       * 'ObjectMapper' comes from the Jackson library. JSON (JavaScript Object Notation)
       * is a very common way to store data. Jackson 'maps' the JSON text into
       * Java objects (in this case, a Map of Strings).
       */
      ObjectMapper mapper = new ObjectMapper();
      Map<String, String> map = mapper.readValue(
        in,
        mapper.getTypeFactory().constructMapType(Map.class, String.class, String.class)
      );

      // We loop through every entry in the JSON file.
      // 'entry.getKey()' is the color name, 'entry.getValue()' is the Hex code (e.g., "#FFFFFF").
      for (Map.Entry<String, String> entry : map.entrySet()) {
        colors.put(entry.getKey(), parseHex(entry.getValue()));
      }
      System.out.println("Loaded " + colors.size() + " colors");
    } catch (Exception e) {
      // If the file is missing or corrupted, we catch the error here so the
      // game doesn't crash; it will just use default colors.
      System.err.println("[Colours] failed" + e.getMessage());
    }
  }

  /**
   * parseHex converts a string like "#FF0000" into a Java Color object.
   *
   * Hexadecimal (Base-16) is used for colors. It uses digits 0-9 and letters A-F.
   * #RRGGBB: Red, Green, Blue.
   */
  private static Color parseHex(String hex) {
    // Remove the '#' character if it's there.
    hex = hex.replace("#", "");

    if (hex.length() == 6) {
      // Integer.parseInt(..., 16) tells Java to treat the string as a Hexadecimal number.
      // We use .substring to grab 2 characters at a time for R, G, and B.
      int r = Integer.parseInt(hex.substring(0, 2), 16);
      int g = Integer.parseInt(hex.substring(2, 4), 16);
      int b = Integer.parseInt(hex.substring(4, 6), 16);
      return new Color(r, g, b);
    } else if (hex.length() == 8) {
      // If the hex string is 8 characters, the last two represent 'Alpha' (Transparency).
      int r = Integer.parseInt(hex.substring(0, 2), 16);
      int g = Integer.parseInt(hex.substring(2, 4), 16);
      int b = Integer.parseInt(hex.substring(4, 6), 16);
      int a = Integer.parseInt(hex.substring(6, 8), 16);
      return new Color(r, g, b, a);
    } else {
      // If the string is not 6 or 8 characters, it's not a valid color.
      throw new IllegalArgumentException("Invalid hex string");
    }
  }

  /**
   * This is the main way other classes get a color.
   * 'getOrDefault' means: "Give me the color named 'X', but if you can't find it,
   * just give me gray so the program doesn't crash."
   */
  public static Color getColor(String name) {
    return colors.getOrDefault(name, Color.gray);
  }

  public static void add(String name, Color color) {
    colors.put(name, color);
  }
}
