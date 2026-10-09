package com.jless.chess;

import java.nio.file.Path;
import java.nio.file.Files;
import java.io.File;

/**
 * The SavingLoading class handles 'Game State Persistence'.
 *
 * Persistence means saving data to a file so that it is still there
 * after the program is closed. In this case, we save the entire board layout
 * and whose turn it is.
 */
public class SavingLoading {
  // We store the save file in a hidden folder (.chessnv) in the user's home directory.
  Path savegame = Path.of(System.getProperty("user.home"), ".chessnv", "saveGame.json");

  /**
   * saveGame converts the current board state into a JSON-formatted string
   * and writes it to the hard drive.
   */
  public void saveGame() {
    try {
      // Ensure the folder exists before we try to write a file into it.
      Files.createDirectories(savegame.getParent());

      /**
       * StringBuilder is used here instead of a normal String.
       * In Java, Strings are 'immutable' (they cannot be changed). Every time you
       * add text to a String, Java actually creates a brand new copy.
       * StringBuilder is like a 'draft' that we can keep adding to efficiently.
       */
      StringBuilder sb = new StringBuilder();

      sb.append("\n");
      // Save the turn state (true for white, false for black).
      sb.append("\"whiteTurn\": ").append(Board.whiteTurn).append(",\n");
      sb.append("");
      sb.append("\"layout\": [\n");

      /**
       * Nested Loops:
       * We use a loop for rows (0-7) and inside that, a loop for columns (0-7).
       * This allows us to visit every single square of the 8x8 board.
       */
      for (int row = 0; row < 8; row++) {
        sb.append("[");
        for (int col = 0; col < 8; col++) {
          String val = Board.layout[row][col];
          // If a square is empty, we write 'null' in the JSON file.
          if (val == null) sb.append("null");
          else sb.append("\"").append(val).append("\"");

          // We add a comma between values, but NOT after the last value in the row.
          if (col < 7) sb.append(",");
        }
        sb.append("]");
        // We add a comma between rows, but NOT after the last row.
        if (row < 7) sb.append(",");
      }
      sb.append("]\n");
      sb.append("}\n");

      // writeString is a convenient way to save the entire StringBuilder result to disk.
      Files.writeString(savegame, sb.toString());

      System.out.println("Saved Game");
    } catch (Exception e) {
      System.out.println("Error saving game");
      e.printStackTrace();
    }
  }

  /**
   * loadGame reads the save file and 'reconstructs' the board state.
   *
   * This is a manual implementation of a JSON parser. Instead of using a library,
   * it looks for keywords (like "layout") and slices the text to find the data.
   */
  public void loadGame() {
    try {
      // If there is no save file, there's nothing to load.
      if (!Files.exists(savegame)) {
        System.err.println("No save found");
        return;
      }

      // Read the entire file into one large string.
      String json = Files.readString(savegame);

      // Determine whose turn it is by checking if the text contains "true" or "false".
      if (json.contains("\"whiteTurn\": true")) {
        Board.whiteTurn = true;
      } else if (json.contains("\"whiteTurn\": false")) {
        Board.whiteTurn = false;
      }

      /**
       * Manual Parsing Logic:
       * We find the position of the "layout" key and then find the first '[' and last ']'
       * to isolate just the part of the file that contains the board data.
       */
      int layoutKeyIndex = json.indexOf("\"layout\":");
      if (layoutKeyIndex == -1) {
        throw new Exception("No layout key found");
      }
      int firstBracket = json.indexOf('[', layoutKeyIndex);
      int lastBracket = json.lastIndexOf(']');

      if (firstBracket == -1 || lastBracket == -1 || lastBracket <= firstBracket) {
        throw new Exception("No layout found");
      }

      // .substring() extracts a piece of the string.
      String arrayBlock = json.substring(firstBracket + 1, lastBracket).trim();

      // Split the big block of text into 8 individual row strings.
      String[] rows = arrayBlock.split("\\],");

      for (int row = 0; row < 8; row++) {
        String rowBlock = rows[row].trim();
        // Clean up the brackets from the start and end of the row.
        if (rowBlock.startsWith("[")) rowBlock = rowBlock.substring(1);
        if (rowBlock.endsWith("]")) rowBlock = rowBlock.substring(0, rowBlock.length() - 1);

        // Split the row into 8 individual square values.
        String[] cols = rowBlock.split(",");
        for (int col = 0; col < 8; col++) {
          String colBlock = cols[col].trim();
          if (colBlock.equals("null")) {
            Board.layout[row][col] = null;
          } else {
            // Remove the quotation marks from the piece identifier (e.g., "\"P\"" -> "P").
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
