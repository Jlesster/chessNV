package com.jless.chess;

import javax.swing.*;

/**
 * The App class serves as the 'Entry Point' for the entire application.
 *
 * In Java, the 'main' method is the very first thing the computer looks for
 * when you start the program. Think of it as the 'Front Door' to the code.
 */
public class App {
  public static void main(String[] args) {
    // We create an instance of the UI class to handle the windows and screens.
    UI ui = new UI();

    // We create an instance of the LF (Look and Feel) class to handle how
    // the windows look on different operating systems (Windows, Mac, Linux).
    LF lf = new LF();

    /**
     * SwingUtilities.invokeLater is a very important concept in GUI programming.
     *
     * Imagine the computer has one 'main thread' for logic and one special 'UI thread'
     * for drawing things on the screen. If we try to change the screen from the
     * main thread, the program might crash or flicker.
     *
     * 'invokeLater' tells Java: "Wait until the UI thread is free, then run this code."
     * This ensures our windows open smoothly and safely.
     */
    SwingUtilities.invokeLater(() -> {
      try {
          // This method sets the visual style (the 'Look and Feel') of the app.
          lf.lf();
      }   catch (UnsupportedLookAndFeelException | InstantiationException | ClassNotFoundException | IllegalAccessException e) {
          // If the computer can't find the style we asked for, it 'throws' an error.
          // We wrap it in a RuntimeException to stop the program and tell us what went wrong.
          throw new RuntimeException(e);
      }

      // Finally, we start the UI logic, which usually opens the first screen (like the Login).
      ui.runtime();
    });
  }
}
