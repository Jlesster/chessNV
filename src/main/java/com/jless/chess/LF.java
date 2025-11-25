package com.jless.chess;

import javax.swing.*;

public class LF {
  public void lf() throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
    if (System.getProperty("os.name").contains("Windows")) {
      UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
    } else if (System.getProperty("os.name").contains("Mac")) {
      UIManager.setLookAndFeel("com.sun.java.swing.plaf.mac.MacLookAndFeel");
    } else if (System.getProperty("os.name").contains("Linux")) {
      UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
    } else {
      UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
    }
  }
}
