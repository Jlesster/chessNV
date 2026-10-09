package com.jless.chess;

import javax.swing.*;
import java.awt.*;

/**
 * The StartMenu class creates the menu window a user sees after logging in.
 *
 * This class acts as a 'router'—it asks the user what they want to do next
 * (Start a fresh game or continue an old one) and sets a flag to tell
 * the rest of the program which state to load.
 */
public class StartMenu extends JDialog {
  /**
   * 'gameDecided' is a boolean flag.
   * We use this to tell the main UI class: "The user has made a choice,
   * you can now proceed to open the game board."
   */
  public boolean gameDecided = false;

  // We create an instance of SavingLoading to handle bringing back a saved game.
  SavingLoading sl = new SavingLoading();

  public StartMenu(JFrame parent) {
    super(parent, "Start", true);
    JPanel contentPane = new JPanel();

    // Define the buttons for our menu.
    JButton continueButton = new JButton("Continue Last Game");
    JButton exitButton = new JButton("Exit");
    JButton newGame = new JButton("New Game");

    // We use GridBagLayout here as well to center our buttons.
    GridBagConstraints c = new GridBagConstraints();
    c.insets = new Insets(5, 5, 5, 5);

    contentPane.setLayout(new GridBagLayout());

    // Position 'New Game' at the top.
    c.gridx = 0;
    c.gridy = 0;
    c.fill = GridBagConstraints.BOTH;
    c.weightx = 1;
    c.weighty = 1;
    contentPane.add(newGame, c);

    // Position 'Continue' in the middle.
    c.gridx = 0;
    c.gridy = 1;
    contentPane.add(continueButton, c);

    // Position 'Exit' at the bottom.
    c.gridx = 0;
    c.gridy = 3;
    contentPane.add(exitButton, c);

    setSize(500, 500);
    setResizable(false);
    setLocationRelativeTo(null);
    setContentPane(contentPane);

    /**
     * ACTION LISTENERS:
     *
     * These define what happens when each button is clicked.
     */
    continueButton.addActionListener(e -> {
      try {
        // Try to load the state of the board from the saved file on disk.
        sl.loadGame();
        gameDecided = true;
        // We use invokeLater to ensure the window closes safely on the UI thread.
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
      // For a new game, we don't need to load anything; we just mark that
      // the user is ready to start.
      gameDecided = true;
      SwingUtilities.invokeLater(this::dispose);
    });
  }
}
