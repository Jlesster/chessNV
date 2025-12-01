package com.jless.chess;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

public class Board extends JPanel {
  private String draggedPiece = null;
  public static boolean whiteTurn = true;
  private int dragStartCol = -1;
  private int dragStartRow = -1;
  public boolean allowHint = true;
  public boolean allowGlow = true;
  private double glowPhase = 1;
  private Timer glowTimer;
  private int squareW;
  private int squareH;
  private int pieceX;
  private int pieceY;
  Pieces pieces;
  UI ui;

  public Board() {
    squareW = 800 / 8;
    squareH = 800 / 8;
    this.setBackground(Colours.getColor("subtext2"));

    pieces = new Pieces(this);
    if (allowHint) {
      glowTimer = new Timer(30, e -> {
        glowPhase += 0.15;   // controls speed of pulsing
        if (glowPhase > Math.PI * 2) glowPhase = 0;
        repaint();
      });
      glowTimer.start();
    }

    addMouseListener(new MouseAdapter() {
      public void mousePressed(MouseEvent e) {
        allowGlow = true;
        int col = e.getX() / squareW;
        int row = e.getY() / squareH;
        String selected = layout[row][col];
        boolean isWhite = Character.isUpperCase(selected.charAt(0));
        if (selected == null)
          return;
        if ((whiteTurn && !isWhite) || (!whiteTurn && isWhite)) {
          return;
        }
        draggedPiece = selected;
        layout[row][col] = null;
        dragStartCol = col;
        dragStartRow = row;
        pieceX = e.getX();
        pieceY = e.getY();

        pieces.avaliableMoves = pieces.getAvaliableMoves(row, col, draggedPiece);
      }
      @Override
      public void mouseReleased(MouseEvent e) {
        allowGlow = false;
        if (draggedPiece != null) {
          int col = e.getX() / squareW;
          int row = e.getY() / squareH;
          boolean validMove = false;

          for (Point move : pieces.avaliableMoves) {
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
          pieces.avaliableMoves.clear();
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
  @Override
  public Dimension getPreferredSize() {
    return new Dimension(800, 800);
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
  public void paintPieces(Graphics g) throws IOException {
    int rows = 8;
    int cols = 8;
    double scale = 0.9;
    int pieceW = (int) (squareW * scale);
    int pieceH = (int) (squareH * scale);
    int offsetX = (squareW - pieceW) / 2;
    int offsetY = (squareH - pieceW) / 2;

    for (int row = 0; row < rows; row++) {
      for (int col = 0; col < cols; col++) {
        String piece = layout[row][col];
        if (piece == null)
          continue;

        String color = Character.isUpperCase(piece.charAt(0)) ? "WHT" : "BLK";
        char type = Character.toUpperCase(piece.charAt(0));
        String filename = "sprites/" + pieces.getPieceName(type) + color + ".png";
        BufferedImage pieceImg = pieces.pieceCache.get(pieces.getPieceName(type) + color);

        if(pieceImg == null) {
          System.err.println("Missing from cache " + pieces.getPieceName(type));
          continue;
        }
        int x = col * squareW + offsetX;
        int y = row * squareH + offsetY;
        g.drawImage(pieceImg, x, y, pieceW, pieceH, this);
      }
    }
    if (draggedPiece != null) {
      String color = Character.isUpperCase(draggedPiece.charAt(0)) ? "WHT" : "BLK";
      char type = Character.toUpperCase(draggedPiece.charAt(0));
      BufferedImage pieceImg = pieces.pieceCache.get(pieces.getPieceName(type) + color);

      if (pieceImg != null) {
        pieceW = (int) (squareW * scale);
        pieceH = (int) (squareH * scale);
        g.drawImage(pieceImg, pieceX - pieceW / 2, pieceY - pieceH / 2, pieceW, pieceH, this);
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
          g.setColor(Colours.getColor("lavender"));
          g.fillRect(x, y, squareW, squareH);
        }
      }
    }
  }
  private void paintGlow(Graphics g) {
    Graphics2D g2d = (Graphics2D) g;
    double glow = (Math.sin(glowPhase) + 1) / 2;
    double eased = 0.3 + (0.7 * glow);

    int glowSize = (int) (squareW * 0.8 + 10 * glow);
    Color glowColor = Colours.getColor("green");
    g2d.setComposite(AlphaComposite.SrcOver);

    for (Point move : pieces.avaliableMoves) {
      int alphaValue = (int)(10 + glow * 100);
      int x = move.x * squareW + (squareW - glowSize) / 2;
      int y = move.y * squareH + (squareH - glowSize) / 2;
      Color guide = new Color(glowColor.getRed(), glowColor.getGreen(), glowColor.getBlue(), alphaValue);
      g2d.setColor(guide);
      g2d.fillRect(x, y, glowSize, glowSize);
    }
  }
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

