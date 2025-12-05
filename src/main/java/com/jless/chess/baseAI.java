package com.jless.chess;

import java.util.*;
import java.util.List;
import java.util.ArrayList;

public class baseAI {
  public double whiteScore = 0;
  public double blackScore = 0;
  List<String[][]> copies = new ArrayList<>();
  public int fromRow, fromCol;
  public int toRow, toCol;
  public String pieceMoved;
  public String pieceCaptured;

  public List<Move> generateMoves(boolean whiteTurn) {
    List<Move> moves = new ArrayList<>();

    for (int row = 0; row < 8; row++){
      for (int col = 0; col < 8; col++){
        String piece = Board.layout[row][col];
      }
    }
  }

  int stepsAhead = 3;
  Board board = new Board();

  private String[][] deepCopy(String[][] original) {
    String[][] result = new String[original.length][];
    for (int i = 0; i < original.length; i++) {
      result[i] = original[i].clone();
    }
    return result;
  }
  private void createCopies(int stepsAhead) {
    for (int i = 0; i < stepsAhead; i++) {
      copies.add(deepCopy(Board.layout));
    }
  }
  private void evaluateFinal(int whiteScore, int blackScore) {
    int finalScore = whiteScore - blackScore;
  }
}
