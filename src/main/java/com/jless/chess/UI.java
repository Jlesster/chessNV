package com.jless.chess;

import javax.swing.*;

public class UI extends JFrame {
  JMenuBar menu = new JMenuBar();
  JMenu session = new JMenu("Session");
  JMenuItem exit = new JMenuItem("Exit and Save");
  JMenuItem glow = new JMenuItem("Disable Glow");
  Board board = new Board();

  public void body() {
    StartMenu startMenu = new StartMenu(this);
    Login login = new Login(startMenu);
    SavingLoading sl = new SavingLoading();

    login.setVisible(true);
    if (login.loggedIn) {
      startMenu.setVisible(true);
      if (startMenu.gameDecided) {
        startGame();
      }
    }
    //Save Game stuff
    this.addWindowListener(new java.awt.event.WindowAdapter() {
      @Override
      public void windowClosing(java.awt.event.WindowEvent wE) {
        if (Board.layout != null) sl.saveGame();
        System.exit(1);
      }
    });
  }
  public void startGame() {
    session.add(glow);
    session.add(exit);

    setVisible(true);
    setSize(800, 820);
    menu.add(session);
    setResizable(false);
    setLocationRelativeTo(null);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setJMenuBar(menu);
    setContentPane(board);
    pack();
    revalidate();
    repaint();
  }
  public void menuLogic() {
    exit.addActionListener(e -> {
      System.exit(0);
    });
    glow.addActionListener(e -> {
      board.allowHint = !board.allowHint;
      if (board.allowHint) {
        glow.setText("Disable Glow");
      } else {
        glow.setText("Enable Glow");
      }
    });
  }
  public void runtime() {
    body();
    menuLogic();
  }
}

