package com.jless.chess;

import javax.swing.*;
import java.awt.*;

public class StartMenu extends JDialog {
  public boolean gameDecided = false;
  SavingLoading sl = new SavingLoading();

  public StartMenu(JFrame parent) {
    super(parent, "Start", true);
    JPanel contentPane = new JPanel();
    JButton continueButton = new JButton("Continue Last Game");
    JButton exitButton = new JButton("Exit");
    JButton newGame = new JButton("New Game");
    GridBagConstraints c = new GridBagConstraints();
    c.insets = new Insets(5, 5, 5, 5);

    contentPane.setLayout(new GridBagLayout());

    c.gridx = 0;
    c.gridy = 0;
    c.fill =GridBagConstraints.BOTH;
    c.weightx = 1;
    c.weighty = 1;
    contentPane.add(newGame, c);

    c.gridx = 0;
    c.gridy = 1;
    contentPane.add(continueButton, c);

    c.gridx = 0;
    c.gridy = 3;
    contentPane.add(exitButton, c);

    setSize(500, 500);
    setResizable(false);
    setLocationRelativeTo(null);
    setContentPane(contentPane);

    continueButton.addActionListener(e -> {
      try {
        sl.loadGame();
        gameDecided = true;
        SwingUtilities.invokeLater(this::dispose);
      } catch (Exception q) {
          System.err.println("Error loading game");
      }
    });
    exitButton.addActionListener(e -> {
      dispose();
      System.exit(0);
      SwingUtilities.invokeLater(this::dispose);
    });
    newGame.addActionListener(e -> {
      gameDecided = true;
      SwingUtilities.invokeLater(this::dispose);
    });
  }

}
