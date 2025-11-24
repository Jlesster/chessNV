package com.jless.chess;

import javax.swing.*;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import static javax.imageio.ImageIO.read;

// public class UI extends JFrame {
//   public void body() {
//     StartMenu startMenu = new StartMenu(this);
//     Login login = new Login(startMenu);
//     login.setVisible(true);
//     if (login.loggedIn) {
//       startMenu.setVisible(true);
//     }
//     if (login.loggedIn && startMenu.gameDecided) {
//       this.setSize(800, 800);
//       add(new Board(this.getWidth(), this.getHeight()));
//       this.setLocationRelativeTo(null);
//       this.setResizable(false);
//       this.setVisible(true);
//       repaint();
//     }
//     this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//     this.addWindowListener(new java.awt.event.WindowAdapter() {
//
//       @Override
//       public void windowClosing(java.awt.event.WindowEvent wE) {
//         if (Board.layout != null) sl.saveGame;
//         System.exit(0);
//       }
//
//     });
//   }
//
//   public void runtime() {
//     body();
//   }
// }

// class Board extends JPanel {
//   private final java.util.Map<String, BufferedImage> pieceCache = new java.util.HashMap<>();
//   private String draggedPiece = null;
//   public static boolean whiteTurn = true;
//   private int dragStartCol = -1;
//   private int dragStartRow = -1;
//   private int squareW;
//   private int squareH;
//   private int pieceX;
//   private int pieceY;
//
//   private java.util.List<Point> avaliableMoves = new java.util.ArrayList<>();
//
//   private java.util.List<Point> getAvaliableMoves(int row, int col, String piece) {
//     java.util.List<Point> moves = new ArrayList<>();
//     char type = Character.toUpperCase(piece.charAt(0));
//     boolean isWhite = Character.isUpperCase(piece.charAt(0));
//
//     if (piece.equalsIgnoreCase("P")) {
//       int direction = Character.isUpperCase(piece.charAt(0)) ? -1 : 1;
//       int newRow = row + direction;
//       if (newRow >= 0 && newRow < 8) {
//         moves.add(new Point(col, newRow));
//       }
//     }
//     return switch (type) {
//       case 'P' -> getPawnMoves(row, col, isWhite);
//       case 'R' -> getRookMoves(row, col, isWhite);
//       case 'B' -> getBishopMoves(row, col, isWhite);
//       case 'K' -> getKnightMoves(row, col, isWhite);
//       case 'Q' -> getQueenMoves(row, col, isWhite);
//       case 'I' -> getKingMoves(row, col, isWhite);
//       default -> new ArrayList<>();
//     };
//   }
//
//   private boolean isEmpty(int row, int col) {
//     return isInsideBoard(row, col) && layout[row][col] == null;
//   }
//
//   private boolean isInsideBoard(int row, int col) {
//     return row >= 0 && row < 8 && col >= 0 && col < 8;
//   }
//
//   private boolean isOpponent(int row, int col, boolean isWhite) {
//     if (!isInsideBoard(row, col) || layout[row][col] == null)
//       return false;
//     return Character.isUpperCase(layout[row][col].charAt(0)) != isWhite;
//   }
//
//   private java.util.List<Point> getDiagMoves(int row, int, col, boolean isWhite, int[][] directions) {
//     java.util.List<Point> moves = new ArrayList<>();
//     for (int[] direction : directions) {
//       int newRow = row + direction[0];
//       int newCol = col + direction[1];
//       while (isInsideBoard(newRow, newCol)) {
//         if (isEmpty(newRow, newCol)) {
//           moves.add(new Point(newCol, newRow));
//     } else {
//             if (isOpponent(newRow, newCol, isWhite)) moves.add(new Point(newRow,newCol));
//             break;
//         }
//         newRow += direction[0];
//         newCow += direction[0];
//       }
//     }
//     return moves;
//     }
//
//   private java.util.List<Point> getPawnMoves(int row, int col, boolean isWhite) {
//     java.util.List<Point> moves = new ArrayList<>();
//     int direction = isWhite ? -1 : 1;
//     if (isEmpty(row + direction, col)) {
//       moves.add(new Point(col, row + 2 * direction));
//       if ((isWhite && row == 6) || (!isWhite && row == 1)) {
//         if (isEmpty(row + 2 * direction, col)) {
//           moves.add(new Point(col, row + 2 * direction));
//         }
//       }
//     }
//     if (isOpponent(row + direction, col - 1, isWhite))
//       moves.add(new Point(col - 1, row + direction));
//     if (isOpponent(row + direction, col + 1, isWhite))
//       moves.add(new Point(col + 1, row + direction));
//     return moves;
//   }
//
//   private java.util.List<Point> getRookMoves(int row, int col, boolean isWhite) {
//     int[][] dirs = { { 0, 1 }, { 1, 0 }, { 0, -1 }, { -1, 0 } };
//     return getDiagMoves(row, col, isWhite, dirs);
//   }
//
//   private java.util.List<Point> getKnightMoves(int row, int col, boolean isWhite) {
//     java.util.List<Point> moves = new ArrayList<>();
//     int[][] hops = { { -2, -1 }, { -2, 1 }, { -1, -2 }, { -1, 2 }, { 1, 2 }, { 2, -1 }, { 2, 1 } };
//     for (int[] j : hops) {
//       int newRow = row + j[0];
//       int newCol = col + j[1];
//       if (isInsideBoard(newRow, newCol) && (isEmpty(newRow, newCol) || isOpponent(newRow, newCol, isWhite)))
//         ;
//       moves.add(new Point(newCol, newRow));
//     }
//
//     return moves;
//   }
// }
