package com.jless.chess;

import javax.swing.*;
import java.awt.*;

public class StartMenu extends JDialog {
  public boolean gameDecided = false;
  SavingLoading sl = new SavingLoading();

  public StartMenu(JFrame parent) {
    super(parent, "Start", true);
    JPanel contentPane = new JPanel();
    JButton exitButton = new JButton("Exit");
    JButton newGame = new JButton("New Game");
  }

}
