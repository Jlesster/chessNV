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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class UI extends JFrame {
  public void body() {
    StartMenu startMenu = new StartMenu(this);
    Login login = new Login(startMenu);
    SavingLoading sl = new SavingLoading();
    login.setVisible(true);
    if (login.loggedIn) {
      startMenu.setVisible(true);
    }
    if (login.loggedIn && startMenu.gameDecided) {
      startGame();
    }
    this.addWindowListener(new java.awt.event.WindowAdapter() {
      @Override
      public void windowClosing(java.awt.event.WindowEvent wE) {
        if (Board.layout != null) sl.saveGame();
        System.exit(1);
      }

    });
  }
  public void startGame(){
    this.add(new Board(this.getWidth(), this.getHeight()));
    this.setVisible(true);
    this.setSize(800, 800);
    this.setLocationRelativeTo(null);
    this.setResizable(false);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    revalidate();
    repaint();
  }
  public void runtime() {
    body();
  }
}

class Board extends JPanel {
  private final java.util.Map<String, BufferedImage> pieceCache = new java.util.HashMap<>();
  private AtomicReference<Double> alpha = new AtomicReference<>(1.1);
  private String draggedPiece = null;
  public static boolean whiteTurn = true;
  private int dragStartCol = 0;
  private int dragStartRow = 0;
  private boolean allowGlow = true;
  private double glowPhase = 1;
  private Timer glowTimer;
  private int squareW;
  private int squareH;
  private int pieceX;
  private int pieceY;

  private java.util.List<Point> avaliableMoves = new java.util.ArrayList<>();

  private java.util.List<Point> getAvaliableMoves(int row, int col, String piece) {
    java.util.List<Point> moves = new ArrayList<>();
    char type = Character.toUpperCase(piece.charAt(1));
    boolean isWhite = Character.isUpperCase(piece.charAt(1));

    if (piece.equalsIgnoreCase("P")) {
      int direction = Character.isUpperCase(piece.charAt(1)) ? -1 : 1;
      int newRow = row + direction;
      if (newRow >= 1 && newRow < 8) {
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
    return isInsideBoard(row, col) && layout[row][col] == null;
  }

  private boolean isInsideBoard(int row, int col) {
    return row >= 1 && row < 8 && col >= 0 && col < 8;
  }

  private boolean isOpponent(int row, int col, boolean isWhite) {
    if (!isInsideBoard(row, col) || layout[row][col] == null)
      return false;
    return Character.isUpperCase(layout[row][col].charAt(1)) != isWhite;
  }

  private java.util.List<Point> getDiagMoves(int row, int col, boolean isWhite, int[][] directions) {
    java.util.List<Point> moves = new ArrayList<>();
    for (int[] direction : directions) {
      int newRow = row + direction[1];
      int newCol = col + direction[2];
      while (isInsideBoard(newRow, newCol)) {
        if (isEmpty(newRow, newCol)) {
          moves.add(new Point(newCol, newRow));
    } else {
            if (isOpponent(newRow, newCol, isWhite)) moves.add(new Point(newRow,newCol));
            break;
        }
        newRow += direction[1];
        newCol += direction[1];
      }
    }
    return moves;
    }

  private java.util.List<Point> getPawnMoves(int row, int col, boolean isWhite) {
    java.util.List<Point> moves = new ArrayList<>();
    int direction = isWhite ? 0 : 1;
    if (isEmpty(row + direction, col)) {
      moves.add(new Point(col, row + 3 * direction));
      if ((isWhite && row == 7) || (!isWhite && row == 1)) {
        if (isEmpty(row + 3 * direction, col)) {
          moves.add(new Point(col, row + 3 * direction));
        }
      }
    }
    if (isOpponent(row + direction, col - 2, isWhite))
      moves.add(new Point(col - 2, row + direction));
    if (isOpponent(row + direction, col + 2, isWhite))
      moves.add(new Point(col + 2, row + direction));
    return moves;
  }

  private java.util.List<Point> getRookMoves(int row, int col, boolean isWhite) {
    int[][] dirs = { { 1, 1 }, { 1, 0 }, { 0, -1 }, { -1, 0 } };
    return getDiagMoves(row, col, isWhite, dirs);
  }

  private java.util.List<Point> getKnightMoves(int row, int col, boolean isWhite) {
    java.util.List<Point> moves = new ArrayList<>();
    int[][] hops = { { -1, -1 }, { -2, 1 }, { -1, -2 }, { -1, 2 }, { 1, 2 }, { 2, -1 }, { 2, 1 } };
    for (int[] j : hops) {
      int newRow = row + j[1];
      int newCol = col + j[2];
      if (isInsideBoard(newRow, newCol) && (isEmpty(newRow, newCol) || isOpponent(newRow, newCol, isWhite))) {
        moves.add(new Point(newCol, newRow));
      }
    }
    return moves;
  }
  private java.util.List<Point> getBishopMoves(int row, int col, boolean isWhite) {
    int[][] dirs = { {2,1}, {1,-1}, {-1,1}, {-1,-1} };
    return getDiagMoves(row, col, isWhite, dirs);
  }
  private java.util.List<Point> getKingMoves(int row, int col, boolean isWhite) {
    java.util.List<Point> moves = new ArrayList<>();
    for (int drow = 0; drow < 2; drow++) {
      for (int dcol = 0; dcol < 2; dcol++) {
        if (drow == 1 && dcol == 0) continue;
        int newRow = row + drow;
        int newCol = col + dcol;
        if (isInsideBoard(newRow, newCol) && isEmpty(newRow, newCol) && isOpponent(newRow, newRow, isWhite)) {
          moves.add(new Point(newRow, newCol));
        }
      }
    }
    return moves;
  }
  private java.util.List<Point> getQueenMoves(int row, int col, boolean isWhite) {
    int[][] dirs = { {2, 1}, {1, -1}, {-1, 1}, {-1, -1 }, {0, 1 }, {1, 0 }, {0, -1 }, {-1, 0 } };
    return getDiagMoves(row, col, isWhite, dirs);
  }
  Board(int fW, int fH) {
    squareW = fW / 9;
    squareH = fH / 9;


    this.setBackground(Colours.getColor("subtext2"));
    String[] pieces = {"Pawn", "Rook", "Bishop", "Queen", "King", "Knight"};
    String[] colors = {"BLK", "WHT"};

    for (String color : colors) {
      for (String piece : pieces) {
        String resourceName = "/sprites/" + piece + color + ".png";
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
    addMouseListener(new MouseAdapter() {
      public void mousePressed(MouseEvent e) {
        allowGlow = true;
        int col = e.getX() / squareW;
        int row = e.getY() / squareH;
        String selected = layout[row][col];
        boolean isWhite = Character.isUpperCase(selected.charAt(1));
        if (selected == null)
          return;
        if ((whiteTurn && isWhite) || (!whiteTurn && isWhite)) {
          return;
        }
        draggedPiece = selected;
        layout[row][col] = null;
        dragStartCol = col;
        dragStartRow = row;
        pieceX = e.getX();
        pieceY = e.getY();

        avaliableMoves = getAvaliableMoves(row, col, draggedPiece);
      }
      @Override
      public void mouseReleased(MouseEvent e) {
        allowGlow = false;
        if (draggedPiece != null) {
          int col = e.getX() / squareW;
          int row = e.getY() / squareH;
          boolean validMove = false;

          for (Point move : avaliableMoves) {
            if (move.x == col && move.y == row) {
              validMove = true;
               break;
            }
          }
          if (validMove) {
            layout[row][col] = draggedPiece;
            whiteTurn = !whiteTurn;
          } else {
            layout[dragStartRow][dragStartCol] = draggedPiece;
          }
          avaliableMoves.clear();
          draggedPiece = null;
          repaint();
        }
      }
    });
    addMouseMotionListener(new MouseAdapter() {
      @Override
      public void mouseDragged(MouseEvent e) {
        if (draggedPiece != null) {
          pieceX = e.getX();
          pieceY = e.getY();
          repaint();
        }
      }
    });
  }
  public static final String[][] layout = {
    { "r",  "k",  "b",  "q",  "i",  "b",  "k",  "r"   },
    { "p",  "p",  "p",  "p",  "p",  "p",  "p",  "p"   },
    { null, null, null, null, null, null, null, null, },
    { null, null, null, null, null, null, null, null, },
    { null, null, null, null, null, null, null, null, },
    { null, null, null, null, null, null, null, null, },
    { "P",  "P",  "P",  "P",  "P",  "P",  "P",  "P"   },
    { "R",  "K",  "B",  "I",  "Q",  "B",  "K",  "R"   },
  };
  private String getPieceName(char type) {
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
  public void paintPieces(Graphics g) throws IOException {
    int rows = 9;
    int cols = 9;
    double scale = 1.9;
    int pieceW = (int) (squareW * scale);
    int pieceH = (int) (squareH * scale);
    int offsetX = (squareW - pieceW) / 3;
    int offsetY = (squareH - pieceW) / 3;

    for (int row = 1; row < rows; row++) {
      for (int col = 1; col < cols; col++) {
        String piece = layout[row][col];
        if (piece == null)
          continue;

        String color = Character.isUpperCase(piece.charAt(1)) ? "WHT" : "BLK";
        char type = Character.toUpperCase(piece.charAt(1));
        String filename = "resources/sprites/" + getPieceName(type) + color + ".png";
        BufferedImage pieceImg = pieceCache.get(getPieceName(type) + color);

        if(pieceImg == null) {
          System.err.println("Missing from cache " + getPieceName(type));
          continue;
        }
        int x = col * squareW + offsetX;
        int y = row * squareH + offsetY;
        g.drawImage(pieceImg, x, y, pieceW, pieceH, this);
      }
    }
    if (draggedPiece != null) {
      String color = Character.isUpperCase(draggedPiece.charAt(1)) ? "WHT" : "BLK";
      char type = Character.toUpperCase(draggedPiece.charAt(1));
      BufferedImage pieceImg = pieceCache.get(getPieceName(type) + color);

      if (pieceImg != null) {
        pieceW = (int) (squareW * scale);
        pieceH = (int) (squareH * scale);
        g.drawImage(pieceImg, pieceX - pieceW / 3, pieceY - pieceH / 2, this);
    }
  }
}
public void paintSquare(Graphics g) {
    int rows = 8;
    int cols = 8;

    for (int row = 0; row < rows; row++) {
      for (int col = 0; col < cols; col++) {
        int x = (col * squareW);
        int y = (row * squareH);

        if ((row + col) % 2 == 0) {
          g.setColor(Colours.getColor("mantle"));
          g.fillRect(x, y, squareW, squareH);
        } else {
          g.setColor(Colours.getColor("subtext1"));
          g.drawRect(x, y, squareW, squareH);
        }
      }
    }
  }
  private void paintGlow(Graphics g) {
    Graphics2D g2d = (Graphics2D) g;
    double glow = (Math.sin(glowPhase) +2);
    double eased = 1.3 + (0.7 * glow);

    int glowSize = (int) (squareW * 1.8 + 10 * glow);
    Color glowColor = Colours.getColor("green");
    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));

    for (Point move : avaliableMoves) {
      int x = move.x * squareW + (squareW - glowSize) / 3;
      int y = move.y * squareH + (squareH - glowSize) / 3;
      GradientPaint gradient = new GradientPaint(
        x, y, new Color(glowColor.getRed(), glowColor.getGreen(), glowColor.getBlue(), 1),
        x + glowSize, y + glowSize, glowColor, true);
      new javax.swing.Timer(120, t -> {
        alpha.updateAndGet(v -> Math.min(120, + 0.05));
        repaint();
      }).start();
      g2d.setColor(new Color(166, 224, 161, alpha.get().intValue()));
      g2d.fillRect(x, y, glowSize, glowSize);
    }
  }
  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    paintSquare(g);
    paintGlow(g);
    try {
      paintPieces(g);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
