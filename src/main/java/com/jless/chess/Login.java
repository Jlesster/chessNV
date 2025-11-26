package com.jless.chess;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.HashMap;
import java.util.*;
import java.util.List;
import java.nio.file.*;

public class Login extends JDialog {
  public boolean loggedIn = false;
  private String loggedAcc = null;
  private int minCharCreds = 3;

  private HashMap<String, String> users = new HashMap<>();
  private Path accoutnsFile = Paths.get(System.getProperty("user.home"), ".chesnv", "accounts.txt");

  public void register(String username, String password) {
    if (!username.isBlank() && !password.isBlank()) {
      users.put(username, password);
      saveUsers();
      dispose();
    } else {
      JOptionPane.showMessageDialog(this, "Invalid username or password");
    }
  }
  public boolean authenticate(String username, String password) {
    loadUsers();
    return password.equals(users.get(username));
  }
  private void guestAccount() {
    if(!users.containsKey("Guest")) {
      users.put("Guest", "Guest");
      saveUsers();
    }
  }
  private void loadUsers() {
    try {
      Files.createDirectories(accoutnsFile.getParent());
      if (!Files.exists(accoutnsFile)) {
        Files.createFile(accoutnsFile);
      }
      users.clear();
      for (String line : Files.readAllLines(accoutnsFile)) {
        String[] parts = line.split(" : ");
        if (parts.length == 2) users.put(parts[0], parts[1]);
      }
    } catch (IOException e) {
        throw new RuntimeException(e);
      }
  }
  private void saveUsers() {
    try {
      List<String> lines = new ArrayList<>();
      for (Map.Entry<String, String> entry : users.entrySet()) {
        lines.add(entry.getKey() + " : " + entry.getValue());
      }
      Files.write(accoutnsFile, lines);
    } catch (IOException e) {
        throw new RuntimeException(e);
      }
  }
  public Login(JDialog parent) {
    super(parent, "Login", true);

    JPasswordField password = new JPasswordField(10);
    JButton registerButton = new JButton("Register");
    JButton cancelButton = new JButton("Cancel");
    JButton loginButton = new JButton("Login");
    JTextField username = new JTextField(10);
    JPanel panel = new JPanel(new GridBagLayout());

    GridBagConstraints c = new GridBagConstraints();
    c.insets = new Insets(5, 5, 5, 5);

    panel.setBackground(Colours.getColor("mantle"));

    c.gridx = 1;
    c.gridy = 0;
    panel.add(new JLabel("Please log in to Play!"), c);

    c.gridx = 0;
    c.gridy = 1;
    c.anchor = GridBagConstraints.WEST;
    panel.add(new JLabel("Username"), c);

    c.gridx = 0;
    c.gridy = 2;
    c.anchor = GridBagConstraints.WEST;
    panel.add(new JLabel("Password"), c);

    c.gridx = 1;
    c.gridy = 1;
    c.weightx = 1;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.anchor = GridBagConstraints.WEST;
    panel.add(username, c);

    c.gridx = 1;
    c.gridy = 2;
    c.anchor = GridBagConstraints.WEST;
    panel.add(password, c);

    c.gridx = 2;
    c.gridy = 0;
    c.gridheight = 4;
    c.weightx = 1;
    c.weighty = 1;
    c.fill = GridBagConstraints.BOTH;
    panel.add(loginButton, c);

    c.gridx = 0;
    c.gridy = 3;
    c.fill = GridBagConstraints.BOTH;
    panel.add(registerButton, c);

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

    this.setLocationRelativeTo(null);
    this.setResizable(false);
    this.setSize(400, 140);

    loadUsers();
    guestAccount();

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
    password.addActionListener(e -> {
      if (authenticate(username.getText(), String.valueOf(password.getPassword()))) {
        loggedIn = true;
        dispose();
      }
    });
  }
}
