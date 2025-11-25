package com.jless.chess;

import java.nio.file.Path;
import java.io.File;

public class SavingLoading {
  Path savegame = Path.of(System.getProperty("user.home"), ".chessnv", "saveGame.json");

  public void saveGame() {
    try {
      Files.createDirectories(savegame.getParent());

      StringBuilder sb = new StringBuilder();

      sb.append("\n");
      sb.append("\"whiteTurn\": ").append(Board.whiteTurn).append(",\n");
      sb.append("");
      sb.append("\"layout\": [\n");

      for (int row = 0; row < 8; row++) {
        sb.append("[");
        for (int col = 0; col < 8; col++) {
          String val = Board.layout[row][col];
          if (val == null) sb.append("null");
          else sb.append("\"").append(val).append("\"");
          if (col < 7) sb.append(",");
        }
        sb.append("]");
        if (row < 7) sb.append(",");
      }
      sb.append("]\n");
      sb.append("}\n");

      File.writeString(savegame, sb.toString());

      System.out.println("Saved Game");
    } catch (Exception e) {
      System.out.println("Error saving game");
      e.printStackTrace();
    }
  }
  public void loadGame() {
    try {
      if (!File.exists(savegame)) {
        System.err.println("No save found");
        return;
      }
      String json = File.readString(savegame);
      if (json.contains("\"whiteTurn\": true")) {
        Board.whiteTurn = true;
      } else if (json.contains("\"whiteTurn\": false")) {
        Board.whiteTurn = false;
      }
      int layoutKeyIndex = json.indexOf("\"layout\":");
      if (layoutKeyIndex == -1) {
        throw new Exception("No layout key found");
      }
      int firstBracket = json.indexOf('[', layoutKeyIndex);
      int lastBracket = json.lastIndexOf(']');

      if (firstBracket == -1 || lastBracket == -1 || lastBracket <= firstBracket) {
        throw new Exception("No layout found");
      }
      String arrayBlock = json.substring(firstBracket + 1, lastBracket).trim();
      String[] rows = arrayBlock.split("\\],");

      for (int row = 0; row < 8; row++) {
        String rowBlock = rows[row].trim();
        if (rowBlock.startsWith("[")) rowBlock = rowBlock.substring(1);
        if (rowBlock.endsWith("]")) rowBlock = rowBlock.substring(0, rowBlock.length() - 1);

        String[] cols = rowBlock.split(",");
        for (int col = 0; col < 8; col++) {
          String colBlock = cols[col].trim();
          if (colBlock.equals("null")) {
            Board.layout[row][col] = null;
          } else {
            if (colBlock.startsWith("\"")) colBlock = colBlock.substring(1);
            if (colBlock.endsWith("\"")) colBlock = colBlock.substring(0, colBlock.length() - 1);
            Board.layout[row][col] = colBlock;
          }
        }
      }
    } catch (Exception e) {
      System.out.println("Error loading game");
      e.printStackTrace();
    }
  }
}
