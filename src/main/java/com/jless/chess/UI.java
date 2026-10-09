package com.jless.chess;

import javax.swing.*;

/**
 * The UI class is the 'Orchestrator' of the application.
 *
 * It extends JFrame, meaning it is the main application window.
 * Its primary job is to manage the flow of the program:
 * Login Window -> Start Menu -> Game Board.
 */
public class UI extends JFrame {
  // Menu bar components for the top of the window.
  JMenuBar menu = new JMenuBar();
  JMenu session = new JMenu("Session");
  JMenuItem exit = new JMenuItem("Exit and Save");
  JMenuItem glow = new JMenuItem("Disable Glow");

  // The Board object handles the actual chess game and is used as the
  // main content of this window.
  Board board = new Board();

  /**
   * body() handles the sequence of windows that appear when the app starts.
   *
   * Educational Concept: Sequential Logic.
   * We create the windows, then show them one by one. The program 'waits'
   * for the modal dialogs to close before moving to the next 'if' statement.
   */
  public void body() {
    StartMenu startMenu = new StartMenu(this);
    Login login = new Login(startMenu);
    SavingLoading sl = new SavingLoading();

    // 1. Show the Login window.
    login.setVisible(true);

    // Once the Login window is closed, we check if the user actually logged in.
    if (login.loggedIn) {
      // 2. Show the Start Menu.
      startMenu.setVisible(true);

      // Once the Start Menu is closed, we check if they picked New Game or Continue.
      if (startMenu.gameDecided) {
        // 3. Finally, start the actual game.
        startGame();
      }
    }

    /**
     * WindowListener is used to detect when the user clicks the 'X' button
     * in the top corner of the window.
     *
     * We override 'windowClosing' to make sure the game is saved to the
     * hard drive before the program shuts down.
     */
    this.addWindowListener(new java.awt.event.WindowAdapter() {
      @Override
      public void windowClosing(java.awt.event.WindowEvent wE) {
        // We only save if the board has actually been initialized.
        if (Board.layout != null) sl.saveGame();
        System.exit(1);
      }
    });
  }

  /**
   * startGame() configures the main game window and makes it visible.
   */
  public void startGame() {
    // Add options to the 'Session' menu.
    session.add(glow);
    session.add(exit);

    setVisible(true);
    setSize(800, 820);
    menu.add(session);
    setResizable(false);
    setLocationRelativeTo(null);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setJMenuBar(menu);

    // setContentPane(board) tells Java: "Fill the entire window with the Board panel."
    setContentPane(board);

    // These three calls tell Swing to recalculate the layout and redraw the screen.
    pack();
    revalidate();
    repaint();
  }

  /**
   * menuLogic() defines what happens when the user clicks the items in the top menu.
   */
  public void menuLogic() {
    exit.addActionListener(e -> {
      System.exit(0);
    });

    glow.addActionListener(e -> {
      // Toggle the 'allowHint' variable on the board (True -> False or False -> True).
      board.allowHint = !board.allowHint;
      if (board.allowHint) {
        glow.setText("Disable Glow");
      } else {
        glow.setText("Enable Glow");
      }
    });
  }

  /**
   * runtime() is called by App.java to kick off the entire process.
   */
  public void runtime() {
    body();
    menuLogic();
  }
}
