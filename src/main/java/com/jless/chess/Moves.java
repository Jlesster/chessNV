package com.jless.chess;

public class Moves {
  public int fromRow, fromCol;
  public int toRow, toCol;
  public String pieceMoved;
  public String pieceCaptured;

  //allie did this magic below uwu
  public void moves(int fr, int fc, int tr, int tc, String moved, String captured) {
    this.toCol = tc;
    this.toRow = tr;
    this.fromRow = fr;
    this.fromCol = fc;
    this.pieceMoved = moved;
    this.pieceCaptured = captured;
  }
}
