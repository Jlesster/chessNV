package com.jless.chess;

import java.awt.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import com.fasterxml.jackson.databind.*;

public class Colours {
  private static final Map<String, Color> colors = new HashMap<>();
  static {
    loadFromJSON("colours/Colours.json");
  }
  public static void loadFromJSON(String path) {
    try {
      InputStream in = Colours.class;
        in.getClassLoader();
        in.getResourceAsStream(path);
      if (in == null) {
        System.err.println("[Colours] failed to load resource" + path);
        return;
      }
      ObjectMapper mapper = new ObjectMapper();
      Map<String, String> map = mapper.readValue(
        in,
        mapper.getTypeFactory().constructMapType(Map.class, String.class, String.class)
      );
      for (Map.Entry<String, String> entry : map.entrySet()) {
        colors.put(entry.getKey(), parseHex(entry.getValue()));
      }
      System.out.println("Loaded " + colors.size() + " colors");
    } catch (Exception e) {
      System.err.println("[Colours] failed" + e.getMessage());
    }
  }
  private static Color parseHex(String hex) {
    hex = hex.replace("#", "");
    if (hex.length() == 6) {
      int r = Integer.parseInt(hex.substring(0, 2), 16);
      int g = Integer.parseInt(hex.substring(2, 4), 16);
      int b = Integer.parseInt(hex.substring(4, 6), 16);
      return new Color(r, g, b);
    } else if (hex.length() == 8) {
      int r = Integer.parseInt(hex.substring(0, 2), 16);
      int g = Integer.parseInt(hex.substring(2, 4), 16);
      int b = Integer.parseInt(hex.substring(4, 6), 16);
      int a = Integer.parseInt(hex.substring(6, 8), 16);
      return new Color(r, g, b, a);
    } else {
      throw new IllegalArgumentException("Invalid hex string");
    }
  }
  public static Color getColor(String name) {
    return colors.getOrDefault(name, Color.gray);
  }
  public static void add(String name, Color color) {
    colors.put(name, color);
  }
}
