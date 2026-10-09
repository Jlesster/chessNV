package com.jless.chess;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

/**
 * The Board class is the heart of the visual game.
 *
 * It extends JPanel, which is a container that can be drawn on.
 * This class handles three main things:
 * 1. The 'State' of the board (where pieces are).
 * 2. User Interaction (dragging pieces with the mouse).
 * 3. Rendering (painting the squares and pieces on the screen).
 */
public class Board extends JPanel {
  // State variables for the 'Drag and Drop' system.
  private String draggedPiece = null;
  public static boolean whiteTurn = true;
  private int dragStartCol = -1;
  private int dragStartRow = -1;

  // Visual settings for the 'Move Hint' (glow) system.
  public boolean allowHint = true;
  public boolean allowGlow = true;
  private double glowPhase = 1;
  private Timer glowTimer;

  // Dimensions of a single square on the 8x8 board.
  private int squareW;
  private int squareH;
  private int pieceX;
  private int pieceY;

  // Reference to the rulebook and UI classes.
  Pieces pieces;
  UI ui;

  public Board() {
    // The window is 800x800, so each square is 100x100 pixels.
    squareW = 800 / 8;
    squareH = 800 / 8;
    this.setBackground(Colours.getColor("subtext2"));

    pieces = new Pieces(this);

    /**
     * EDUCATIONAL CONCEPT: The Animation Timer.
     * A Timer allows us to run a piece of code every X milliseconds.
     * Here, we update 'glowPhase' every 30ms and call repaint().
     * This creates the smooth 'pulsing' effect for move hints.
     */
    if (allowHint) {
      glowTimer = new Timer(30, e -> {
        glowPhase += 0.15;   // controls speed of pulsing
        if (glowPhase > Math.PI * 2) glowPhase = 0;
        repaint();
      });
      glowTimer.start();
    }

    /**
     * MOUSE LISTENER:
     * Handles the start (mousePressed) and end (mouseReleased) of a click.
     */
    addMouseListener(new MouseAdapter() {
      public void mousePressed(MouseEvent e) {
        allowGlow = true;
        // Convert pixel coordinates (e.g., 250px) into grid coordinates (e.g., col 2).
        int col = e.getX() / squareW;
        int row = e.getY() / squareH;

        String selected = layout[row][col];
        if (selected == null)
          return;

        // Check if the player is trying to move a piece that isn't theirs.
        boolean isWhite = Character.isUpperCase(selected.charAt(0));
        if ((whiteTurn && !isWhite) || (!whiteTurn && isWhite)) {
          return;
        }

        // Start the drag process.
        draggedPiece = selected;
        layout[row][col] = null; // Remove piece from the board while it's being dragged.
        dragStartCol = col;
        dragStartRow = row;
        pieceX = e.getX();
        pieceY = e.getY();

        // Ask the rulebook (Pieces class) where this piece is allowed to go.
        pieces.avaliableMoves = pieces.getAvaliableMoves(row, col, draggedPiece);
      }

      @Override
      public void mouseReleased(MouseEvent e) {
        allowGlow = false;
        if (draggedPiece != null) {
          int col = e.getX() / squareW;
          int row = e.getY() / squareH;
          boolean validMove = false;

          // Check if the square the user dropped the piece on is in the 'availableMoves' list.
          for (Point move : pieces.avaliableMoves) {
            if (move.x == col && move.y == row) {
              validMove = true;
               break;
            }
          }

          if (validMove) {
            // Valid move! Update the board and switch turns.
            layout[row][col] = draggedPiece;
            whiteTurn = !whiteTurn;
          } else {
            // Invalid move! Put the piece back where it started.
            layout[dragStartRow][dragStartCol] = draggedPiece;
          }
          pieces.avaliableMoves.clear();
          draggedPiece = null;
          repaint();
        }
      }
    });

    /**
     * MOUSE MOTION LISTENER:
     * Handles the mouse movement while the button is held down.
     */
    addMouseMotionListener(new MouseAdapter() {
      @Override
      public void mouseDragged(MouseEvent e) {
        if (draggedPiece != null) {
          // Update the image position to follow the mouse cursor.
          pieceX = e.getX();
          pieceY = e.getY();
          repaint();
        }
      }
    });
  }

  @Override
  public Dimension getPreferredSize() {
    return new Dimension(800, 800);
  }

  /**
   * THE BOARD STATE:
   * A 2D String array representing the 8x8 grid.
   * null = Empty square.
   * Uppercase = White piece.
   * Lowercase = Black piece.
   */
  public final static String[][] layout = {
    { "r",  "k",  "b",  "q",  "i",  "b",  "k",  "r"   },
    { "p",  "p",  "p",  "p",  "p",  "p",  "p",  "p"   },
    { null, null, null, null, null, null, null, null, },
    { null, null, null, null, null, null, null, null, },
    { null, null, null, null, null, null, null, null, },
    { null, null, null, null, null, null, null, null, },
    { "P",  "P",  "P",  "P",  "P",  "P",  "P",  "P"   },
    { "R",  "K",  "B",  "I",  "Q",  "B",  "K",  "R"   },
  };

  /**
   * Rendering: Drawing the pieces on the screen.
   */
  public void paintPieces(Graphics g) throws IOException {
    int rows = 8;
    int cols = 8;
    double scale = 0.9; // Make pieces slightly smaller than the square.
    int pieceW = (int) (squareW * scale);
    int pieceH = (int) (squareH * scale);
    int offsetX = (squareW - pieceW) / 2;
    int offsetY = (squareH - pieceW) / 2;

    for (int row = 0; row < rows; row++) {
      for (int col = 0; col < cols; col++) {
        String piece = layout[row][col];
        if (piece == null)
          continue;

        // Determine the correct image to fetch from the cache.
        String color = Character.isUpperCase(piece.charAt(0)) ? "WHT" : "BLK";
        char type = Character.toUpperCase(piece.charAt(0));
        BufferedImage pieceImg = pieces.pieceCache.get(pieces.getPieceName(type) + color);

        if(pieceImg == null) {
          System.err.println("Missing from cache " + pieces.getPieceName(type));
          continue;
        }
        // Calculate position based on grid cell.
        int x = col * squareW + offsetX;
        int y = row * squareH + offsetY;
        g.drawImage(pieceImg, x, y, pieceW, pieceH, this);
      }
    }
    // Draw the piece that is currently being dragged by the user.
    if (draggedPiece != null) {
      String color = Character.isUpperCase(draggedPiece.charAt(0)) ? "WHT" : "BLK";
      char type = Character.toUpperCase(draggedPiece.charAt(0));
      BufferedImage pieceImg = pieces.pieceCache.get(pieces.getPieceName(type) + color);

      if (pieceImg != null) {
        pieceW = (int) (squareW * scale);
        pieceH = (int) (squareH * scale);
        // Center the image on the mouse cursor.
        g.drawImage(pieceImg, pieceX - pieceW / 2, pieceY - pieceH / 2, pieceW, pieceH, this);
      }
    }
  }

  /**
   * Rendering: Drawing the checkered board background.
   */
  public void paintSquare(Graphics g) {
    int rows = 8;
    int cols = 8;

    for (int row = 0; row < rows; row++) {
      for (int col = 0; col < cols; col++) {
        int x = (col * squareW);
        int y = (row * squareH);

        /**
         * Checkerboard Pattern Logic:
         * If (row + col) is even, it's one color; if odd, it's another.
         * This is a classic trick for creating grids.
         */
        if ((row + col) % 2 == 0) {
          g.setColor(Colours.getColor("mantle"));
          g.fillRect(x, y, squareW, squareH);
        } else {
          g.setColor(Colours.getColor("lavender"));
          g.fillRect(x, y, squareW, squareH);
        }
      }
    }
  }

  /**
   * Rendering: Drawing the pulsing move hints.
   */
  private void paintGlow(Graphics g) {
    Graphics2D g2d = (Graphics2D) g;
    // Use a Sine wave to create a smooth pulsing value between 0 and 1.
    double glow = (Math.sin(glowPhase) + 1) / 2;
    double eased = 0.3 + (0.7 * glow);

    int glowSize = (int) (squareW * 0.8 + 10 * glow);
    Color glowColor = Colours.getColor("green");

    // AlphaComposite allows us to draw shapes with transparency.
    g2d.setComposite(AlphaComposite.SrcOver);

    for (Point move : pieces.avaliableMoves) {
      // Change the transparency based on the pulsing glow value.
      int alphaValue = (int)(10 + glow * 100);
      int x = move.x * squareW + (squareW - glowSize) / 2;
      int y = move.y * squareH + (squareH - glowSize) / 2;
      Color guide = new Color(glowColor.getRed(), glowColor.getGreen(), glowColor.getBlue(), alphaValue);
      g2d.setColor(guide);
      g2d.fillRect(x, y, glowSize, glowSize);
    }
  }

  /**
   * paintComponent is the 'Master Drawing Method'.
   *
   * In Swing, you don't just draw whenever you want. You override paintComponent,
   * and Java calls it whenever the window needs to be redrawn (e.g., when you move the mouse).
   */
  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    paintSquare(g);
    if (allowHint && allowGlow) {
      paintGlow(g);
    }
    try {
      paintPieces(g);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
