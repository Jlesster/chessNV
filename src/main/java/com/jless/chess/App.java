package com.jless.chess;

import javax.swing.*;

public class App {
    public static void main(String[] args) {
        UI ui = new UI();
        LF lf = new LF();
        SwingUtilities.invokeLater(() -> {
            try {
                lf.lf();
            }   catch (UnsupportedLookAndFeelException | InstantiationException | ClassNotFoundException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            ui.runtime();
        });
    }
}
