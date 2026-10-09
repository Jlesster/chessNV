package com.jless.chess;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.HashMap;
import java.util.*;
import java.util.List;
import java.nio.file.*;

/**
 * The Login class creates a 'Dialog' window that handles user authentication.
 *
 * It inherits from JDialog, which means it is a pop-up window that can be
 * 'modal' (blocking the main window until it is closed).
 *
 * Educational Concept: Inheritance. By extending JDialog, this class
 * gains all the properties of a window (title, size, close button) without
 * us having to write that code from scratch.
 */
public class Login extends JDialog {
  // This variable tracks whether the user successfully logged in.
  public boolean loggedIn = false;
  private String loggedAcc = null;

  /**
   * We use a HashMap to store users in memory while the program is running.
   * Key: Username (String) -> Value: Password (String)
   */
  private HashMap<String, String> users = new HashMap<>();

  /**
   * The 'Path' object defines exactly where the accounts file is stored on the hard drive.
   * System.getProperty("user.home") ensures this works on any computer, regardless
   * of whether the user is named 'Alice' or 'Bob'.
   */
  private Path accountsFile = Paths.get(System.getProperty("user.home"), ".chessnv", "accounts.txt");

  /**
   * Registers a new user by adding them to the HashMap and saving that map to a file.
   */
  public void register(String username, String password) {
    // .isBlank() checks if the string is empty or just contains spaces.
    if (!username.isBlank() && !password.isBlank()) {
      users.put(username, password);
      saveUsers();
      dispose(); // dispose() closes the window.
    } else {
      // JOptionPane is a quick way to show a simple alert message to the user.
      JOptionPane.showMessageDialog(this, "Invalid username or password");
    }
  }

  /**
   * Checks if the provided password matches the one stored for that username.
   */
  public boolean authenticate(String username, String password) {
    loadUsers(); // We reload from disk to make sure we have the latest accounts.
    return password.equals(users.get(username));
  }

  /**
   * Ensures there is always a 'Guest' account available so people can play without registering.
   */
  private void guestAccount() {
    if(!users.containsKey("Guest")) {
      users.put("Guest", "Guest");
      saveUsers();
    }
  }

  /**
   * loadUsers reads the 'accounts.txt' file and fills the 'users' HashMap.
   *
   * This is called 'Parsing'. We take a line of text like "Alice : 12345"
   * and split it into two separate pieces of data.
   */
  private void loadUsers() {
    try {
      // Create the .chessnv folder if it doesn't exist yet.
      Files.createDirectories(accountsFile.getParent());
      if (!Files.exists(accountsFile)) {
        Files.createFile(accountsFile);
      }
      users.clear();
      // Files.readAllLines reads the entire text file into a List of strings.
      for (String line : Files.readAllLines(accountsFile)) {
        // We split the line by the delimiter " : " to separate user from pass.
        String[] parts = line.split(" : ");
        if (parts.length == 2) users.put(parts[0], parts[1]);
      }
    } catch (IOException e) {
        // If the hard drive fails or the file is locked, we throw a RuntimeException.
        throw new RuntimeException(e);
      }
  }

  /**
   * saveUsers writes the current 'users' HashMap back to the hard drive.
   */
  private void saveUsers() {
    try {
      List<String> lines = new ArrayList<>();
      for (Map.Entry<String, String> entry : users.entrySet()) {
        // We convert the map entry back into a single string for the text file.
        lines.add(entry.getKey() + " : " + entry.getValue());
      }
      Files.write(accountsFile, lines);
    } catch (IOException e) {
        throw new RuntimeException(e);
      }
  }

  /**
   * The Constructor. This is where we build the visual layout of the login window.
   */
  public Login(JDialog parent) {
    super(parent, "Login", true);

    // UI Components: These are the 'widgets' the user interacts with.
    JPasswordField password = new JPasswordField(10);
    JButton registerButton = new JButton("Register");
    JButton cancelButton = new JButton("Cancel");
    JButton loginButton = new JButton("Login");
    JTextField username = new JTextField(10);

    /**
     * GridBagLayout is one of the most powerful but complex layouts in Swing.
     * It treats the window like a grid (rows and columns).
     *
     * We use GridBagConstraints to tell Java exactly where each component goes.
     */
    JPanel panel = new JPanel(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();
    c.insets = new Insets(5, 5, 5, 5); // Adds 5 pixels of padding around every element.

    panel.setBackground(Colours.getColor("mantle"));

    // Column 1, Row 0: Header text
    c.gridx = 1;
    c.gridy = 0;
    panel.add(new JLabel("Please log in to Play!"), c);

    // Column 0, Row 1: Username Label
    c.gridx = 0;
    c.gridy = 1;
    c.anchor = GridBagConstraints.WEST;
    panel.add(new JLabel("Username"), c);

    // Column 0, Row 2: Password Label
    c.gridx = 0;
    c.gridy = 2;
    c.anchor = GridBagConstraints.WEST;
    panel.add(new JLabel("Password"), c);

    // Column 1, Row 1: Username Input Field
    c.gridx = 1;
    c.gridy = 1;
    c.weightx = 1;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.anchor = GridBagConstraints.WEST;
    panel.add(username, c);

    // Column 1, Row 2: Password Input Field
    c.gridx = 1;
    c.gridy = 2;
    c.anchor = GridBagConstraints.WEST;
    panel.add(password, c);

    // Column 2, Rows 0-3: Login Button (spans multiple rows)
    c.gridx = 2;
    c.gridy = 0;
    c.gridheight = 4;
    c.weightx = 1;
    c.weighty = 1;
    c.fill = GridBagConstraints.BOTH;
    panel.add(loginButton, c);

    // Column 0, Row 3: Register Button
    c.gridx = 0;
    c.gridy = 3;
    c.fill = GridBagConstraints.BOTH;
    panel.add(registerButton, c);

    // Column 1, Row 3: Cancel Button
    c.gridx = 1;
    c.gridy = 3;
    panel.add(cancelButton, c);

    cancelButton.setBackground(Colours.getColor("subtext1"));
    loginButton.setBackground(Colours.getColor("subtext1"));

    getContentPane().add(panel, BorderLayout.CENTER);

    registerButton.setFocusable(false);
    cancelButton.setFocusable(false);
    username.requestFocusInWindow();
    loginButton.setFocusable(false);

    this.setLocationRelativeTo(null); // Centers the window on the screen.
    this.setResizable(false);
    this.setSize(400, 140);

    loadUsers();
    guestAccount();

    /**
     * ACTION LISTENERS:
     *
     * These are the 'triggers'. Instead of the program constantly asking "Did the user click?",
     * we tell Java: "When this button is clicked, run this specific piece of code."
     * This is called 'Asynchronous' or 'Event-Driven' programming.
     */
    loginButton.addActionListener(e -> {
      if (authenticate(username.getText(), String.valueOf(password.getPassword()))) {
        loggedIn = true;
        dispose();
      }
    });

    registerButton.addActionListener(e -> {
      register(username.getText(), String.valueOf(password.getPassword()));
      loggedIn = true;
      dispose();
    });

    cancelButton.addActionListener(e -> {
      loggedIn = false;
      System.exit(0);
      dispose();
    });

    // Also allow pressing 'Enter' while in the password field to log in.
    password.addActionListener(e -> {
      if (authenticate(username.getText(), String.valueOf(password.getPassword()))) {
        loggedIn = true;
        dispose();
      }
    });
  }
}
