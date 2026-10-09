package com.jless.chess;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

/**
 * The Pieces class is the 'Rulebook' of the game.
 *
 * Its main responsibility is to calculate which moves are legal for a given piece
 * based on the current state of the board. It also handles loading and caching
 * the images (sprites) for every piece.
 */
public class Pieces {
  private Board board;

  /**
   * pieceCache is a Map that stores images of the pieces.
   * Instead of loading an image from the hard drive every time we draw a piece
   * (which would be very slow), we load it once and store it in memory (Cache).
   */
  public final java.util.Map<String, BufferedImage> pieceCache = new java.util.HashMap<>();
  public java.util.List<Point> avaliableMoves = new java.util.ArrayList<>();

  /**
   * This is the primary method for move validation.
   *
   * @param row The current row of the piece.
   * @param col The current column of the piece.
   * @param piece The identifier for the piece (e.g., "P" for white pawn, "p" for black).
   * @return A list of Point objects representing every legal square the piece can move to.
   */
  public java.util.List<Point> getAvaliableMoves(int row, int col, String piece) {
    java.util.List<Point> moves = new ArrayList<>();

    // We convert the piece to uppercase to identify its 'type' regardless of color.
    char type = Character.toUpperCase(piece.charAt(0));

    /**
     * Educational Concept: Case-based Color identification.
     * In this project, Uppercase letters = White, Lowercase letters = Black.
     * Character.isUpperCase() allows us to determine the color of the piece instantly.
     */
    boolean isWhite = Character.isUpperCase(piece.charAt(0));

    // This is a 'switch expression' (introduced in modern Java).
    // It acts like a cleaner version of a long if/else chain.
    return switch (type) {
      case 'P' -> getPawnMoves(row, col, isWhite);
      case 'R' -> getRookMoves(row, col, isWhite);
      case 'B' -> getBishopMoves(row, col, isWhite);
      case 'K' -> getKnightMoves(row, col, isWhite);
      case 'Q' -> getQueenMoves(row, col, isWhite);
      case 'I' -> getKingMoves(row, col, isWhite);
      default -> new ArrayList<>();
    };
  }

  /**
   * Helper method to check if a square is empty.
   */
  private boolean isEmpty(int row, int col) {
    return isInsideBoard(row, col) && Board.layout[row][col] == null;
  }

  /**
   * Helper method to ensure we aren't checking squares outside the 8x8 grid.
   * This prevents 'ArrayIndexOutOfBoundsException' crashes.
   */
  private boolean isInsideBoard(int row, int col) {
    return row >= 0 && row < 8 && col >= 0 && col < 8;
  }

  /**
   * Checks if a square contains a piece belonging to the other player.
   */
  private boolean isOpponent(int row, int col, boolean isWhite) {
    if (!isInsideBoard(row, col) || Board.layout[row][col] == null)
      return false;
    // If the current piece is white, the opponent is anyone who is NOT white (lowercase).
    return Character.isUpperCase(Board.layout[row][col].charAt(0)) != isWhite;
  }

  /**
   * getDiagMoves is a generic method used by Rooks, Bishops, and Queens.
   *
   * Instead of writing the sliding logic three times, we write it once here.
   * The 'directions' array tells the method which way to slide (e.g., [1, 1] for down-right).
   */
  private java.util.List<Point> getDiagMoves(int row, int col, boolean isWhite, int[][] directions) {
    java.util.List<Point> moves = new ArrayList<>();
    for (int[] direction : directions) {
      int newRow = row + direction[0];
      int newCol = col + direction[1];

      // We keep moving in the same direction until we hit the edge of the board or another piece.
      while (isInsideBoard(newRow, newCol)) {
        if (isEmpty(newRow, newCol)) {
          moves.add(new Point(newCol, newRow));
        } else {
            // If we hit a piece, we can move there ONLY if it's an opponent.
            if (isOpponent(newRow, newCol, isWhite)) moves.add(new Point(newCol, newRow));
            break; // Stop sliding because the path is now blocked.
          }
        newRow += direction[0];
        newCol += direction[1];
      }
    }
    return moves;
  }

  private java.util.List<Point> getPawnMoves(int row, int col, boolean isWhite) {
    java.util.List<Point> moves = new ArrayList<>();
    // White pawns move up (-1 row), Black pawns move down (+1 row).
    int direction = isWhite ? -1 : 1;

    // 1. Basic forward move.
    if (isEmpty(row + direction, col)) {
      moves.add(new Point(col, row + direction));

      // 2. Initial double-move (from the 2nd or 7th rank).
      if ((isWhite && row == 6) || (!isWhite && row == 1)) {
        if (isEmpty(row + 2 * direction, col)) {
          moves.add(new Point(col, row + 2 * direction));
        }
      }
    }

    // 3. Capturing diagonally.
    if (isOpponent(row + direction, col - 1, isWhite))
      moves.add(new Point(col - 1, row + direction));
    if (isOpponent(row + direction, col + 1, isWhite))
      moves.add(new Point(col + 1, row + direction));

    return moves;
  }

  private java.util.List<Point> getRookMoves(int row, int col, boolean isWhite) {
    // Rooks move in straight lines (Up, Down, Left, Right).
    int[][] dirs = { { 0, 1 }, { 1, 0 }, { 0, -1 }, { -1, 0 } };
    return getDiagMoves(row, col, isWhite, dirs);
  }

  private java.util.List<Point> getKnightMoves(int row, int col, boolean isWhite) {
    java.util.List<Point> moves = new ArrayList<>();
    // Knights move in an 'L' shape.
    int[][] hops = { { -2, -1 }, { -2, 1 }, { -1, -2 }, { -1, 2 }, {1, -2},{ 1, 2 }, { 2, -1 }, { 2, 1 } };
    for (int[] j : hops) {
      int newRow = row + j[0];
      int newCol = col + j[1];
      // Knights can jump over other pieces. They only care if the destination is empty or an opponent.
      if (isInsideBoard(newRow, newCol) && (isEmpty(newRow, newCol) || isOpponent(newRow, newCol, isWhite))) {
        moves.add(new Point(newCol, newRow));
      }
    }
    return moves;
  }

  private java.util.List<Point> getBishopMoves(int row, int col, boolean isWhite) {
    // Bishops move diagonally.
    int[][] dirs = { { 1, 1 }, { 1, -1 }, { -1, 1 }, { -1, -1 } };
    return getDiagMoves(row, col, isWhite, dirs);
  }

  private java.util.List<Point> getKingMoves(int row, int col, boolean isWhite) {
    java.util.List<Point> moves = new ArrayList<>();
    // The King moves exactly one square in any direction.
    for (int drow = -1; drow < 2; drow++) {
      for (int dcol = -1; dcol < 2; dcol++) {
        if (drow == 0 && dcol == 0) continue; // Don't count the square the King is already on.
        int newRow = row + drow;
        int newCol = col + dcol;
        if (isInsideBoard(newRow, newCol) && (isEmpty(newRow, newCol) || isOpponent(newRow, newCol, isWhite))) {
          moves.add(new Point(newCol, newRow));
        }
      }
    }
    return moves;
  }

  private java.util.List<Point> getQueenMoves(int row, int col, boolean isWhite) {
    // The Queen combines Rook and Bishop movements.
    int[][] dirs = { { 1, 1 }, { 1, -1 }, { -1, 1 }, { -1, -1 }, { 0, 1 }, { 1, 0 }, { 0, -1 }, { -1, 0 } };
    return getDiagMoves(row, col, isWhite, dirs);
  }

  String[] pieces = {"Pawn", "Rook", "Bishop", "Queen", "King", "Knight"};
  String[] colors = {"BLK", "WHT"};

  /**
   * The Constructor loads all piece images from the resources folder.
   */
  public Pieces(Board board) {
    this.board = board;
    for (String color : colors) {
      for (String piece : pieces) {
        // We build a string like "sprites/PawnWHT.png".
        String resourceName = "sprites/" + piece + color + ".png";
        try (java.io.InputStream in = UI.class.getClassLoader().getResourceAsStream(resourceName)){
          if (in != null) {
            BufferedImage img = ImageIO.read(in);
            pieceCache.put(piece + color, img);
          }
        } catch (IOException e) {
          System.err.println("Error loading image " + resourceName);
        }
      }
    }
  }

  public String getPieceName(char type) {
    return switch (type) {
      case 'B' -> "Bishop";
      case 'K' -> "Knight";
      case 'Q' -> "Queen";
      case 'R' -> "Rook";
      case 'P' -> "Pawn";
      case 'I' -> "King";
      default -> "Unknown";
    };
  }

  public int getPieceValue(char type){
    return switch (type) {
      case 'P' -> 10;
      case 'B' -> 30;
      case 'K' -> 30;
      case 'Q' -> 90;
      case 'R' -> 50;
      case 'I' -> 900;
      default -> 0;
    };
  }
}
