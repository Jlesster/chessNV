package com.jless.chess;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class Pieces {
  private Board board;
  public final java.util.Map<String, BufferedImage> pieceCache = new java.util.HashMap<>();
  public java.util.List<Point> avaliableMoves = new java.util.ArrayList<>();
  public java.util.List<Point> getAvaliableMoves(int row, int col, String piece) {
    java.util.List<Point> moves = new ArrayList<>();
    char type = Character.toUpperCase(piece.charAt(0));
    boolean isWhite = Character.isUpperCase(piece.charAt(0));

    if (piece.equalsIgnoreCase("P")) {
      int direction = Character.isUpperCase(piece.charAt(0)) ? -1 : 1;
      int newRow = row + direction;
      if (newRow >= 0 && newRow < 8) {
        moves.add(new Point(col, newRow));
      }
    }
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

  private boolean isEmpty(int row, int col) {
    return isInsideBoard(row, col) && board.layout[row][col] == null;
  }

  private boolean isInsideBoard(int row, int col) {
    return row >= 0 && row < 8 && col >= 0 && col < 8;
  }

  private boolean isOpponent(int row, int col, boolean isWhite) {
    if (!isInsideBoard(row, col) || board.layout[row][col] == null)
      return false;
    return Character.isUpperCase(board.layout[row][col].charAt(0)) != isWhite;
  }

  private java.util.List<Point> getDiagMoves(int row, int col, boolean isWhite, int[][] directions) {
    java.util.List<Point> moves = new ArrayList<>();
    for (int[] direction : directions) {
      int newRow = row + direction[0];
      int newCol = col + direction[1];
      while (isInsideBoard(newRow, newCol)) {
        if (isEmpty(newRow, newCol)) {
          moves.add(new Point(newCol, newRow));
        } else {
            if (isOpponent(newRow, newCol, isWhite)) moves.add(new Point(newCol, newRow));
            break;
          }
        newRow += direction[0];
        newCol += direction[1];
      }
    }
    return moves;
  }
  private java.util.List<Point> getPawnMoves(int row, int col, boolean isWhite) {
    java.util.List<Point> moves = new ArrayList<>();
    int direction = isWhite ? -1 : 1;
    if (isEmpty(row + direction, col)) {
      moves.add(new Point(col, row + direction));
      if ((isWhite && row == 6) || (!isWhite && row == 1)) {
        if (isEmpty(row + 2 * direction, col)) {
          moves.add(new Point(col, row + 2 * direction));
        }
      }
    }
    if (isOpponent(row + direction, col - 1, isWhite))
      moves.add(new Point(col - 1, row + direction));
    if (isOpponent(row + direction, col + 1, isWhite))
      moves.add(new Point(col + 1, row + direction));
    return moves;
  }
  private java.util.List<Point> getRookMoves(int row, int col, boolean isWhite) {
    int[][] dirs = { { 0, 1 }, { 1, 0 }, { 0, -1 }, { -1, 0 } };
    return getDiagMoves(row, col, isWhite, dirs);
  }
  private java.util.List<Point> getKnightMoves(int row, int col, boolean isWhite) {
    java.util.List<Point> moves = new ArrayList<>();
    int[][] hops = { { -2, -1 }, { -2, 1 }, { -1, -2 }, { -1, 2 }, {1, -2},{ 1, 2 }, { 2, -1 }, { 2, 1 } };
    for (int[] j : hops) {
      int newRow = row + j[0];
      int newCol = col + j[1];
      if (isInsideBoard(newRow, newCol) && (isEmpty(newRow, newCol) || isOpponent(newRow, newCol, isWhite))) {
        moves.add(new Point(newCol, newRow));
      }
    }
    return moves;
  }
  private java.util.List<Point> getBishopMoves(int row, int col, boolean isWhite) {
    int[][] dirs = { { 1, 1 }, { 1, -1 }, { -1, 1 }, { -1, -1 } };
    return getDiagMoves(row, col, isWhite, dirs);
  }
  private java.util.List<Point> getKingMoves(int row, int col, boolean isWhite) {
    java.util.List<Point> moves = new ArrayList<>();
    for (int drow = -1; drow < 2; drow++) {
      for (int dcol = -1; dcol < 2; dcol++) {
        if (drow == 0 && dcol == 0) continue;
        int newRow = row + drow;
        int newCol = col + dcol;
        if (isInsideBoard(newRow, newCol) && isEmpty(newRow, newCol) && isOpponent(newRow, newCol, isWhite)) {
          moves.add(new Point(newCol, newRow));
        }
      }
    }
    return moves;
  }
  private java.util.List<Point> getQueenMoves(int row, int col, boolean isWhite) {
    int[][] dirs = { { 1, 1 }, { 1, -1 }, { -1, 1 }, { -1, -1 }, { 0, 1 }, { 1, 0 }, { 0, -1 }, { -1, 0 } };
    return getDiagMoves(row, col, isWhite, dirs);
  }

  String[] pieces = {"Pawn", "Rook", "Bishop", "Queen", "King", "Knight"};
  String[] colors = {"BLK", "WHT"};

  public Pieces(Board board) {
    this.board = board;
    for (String color : colors) {
      for (String piece : pieces) {
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


