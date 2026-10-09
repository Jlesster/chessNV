package com.jless.chess;

import javax.swing.*;

/**
 * The LF class stands for 'Look and Feel'.
 *
 * In software development, 'Look and Feel' refers to the visual appearance
 * (Look) and the interactive behavior (Feel) of a user interface.
 *
 * Different operating systems (Windows, Mac, Linux) have different standards
 * for how buttons and windows should look. This class detects which system
 * the user is running and tells Java to use the matching style.
 */
public class LF {
  /**
   * This method determines the OS and applies the corresponding look and feel.
   *
   * It 'throws' several exceptions (like UnsupportedLookAndFeelException).
   * An 'Exception' is Java's way of saying: "Something might go wrong here that
   * I can't fix on my own, so the person calling this method needs to decide
   * how to handle the error."
   */
  public void lf() throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
    // System.getProperty("os.name") asks the computer: "What operating system are you?"
    // .contains("Windows") checks if the answer includes the word "Windows".
    if (System.getProperty("os.name").contains("Windows")) {
      // UIManager is a built-in Java tool that controls the global style of the app.
      UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
    } else if (System.getProperty("os.name").contains("Mac")) {
      UIManager.setLookAndFeel("com.sun.java.swing.plaf.mac.MacLookAndFeel");
    } else if (System.getProperty("os.name").contains("Linux")) {
      UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
    } else {
      // This is a 'fallback'. If the OS isn't Windows, Mac, or Linux,
      // we use a basic style called 'Motif'.
      UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
    }
  }
}
