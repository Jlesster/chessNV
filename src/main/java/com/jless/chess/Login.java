package com.jless.chess;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Login extends JDialog {
  public boolean loggedIn = false;
  private String loggedAcc = null;
  private int minCharCreds = 3;

  private HashMap<String, String> users = new HashMap<>();
  private String file = "resources/accounts/accounts.txt";

  public void register(String username, String password) {
    if (!username.isBlank() && password.isBlank()) {
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
    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
      String line;
      while ((line = br.readLine()) != null) {
        String[] user = line.split(" : ");
        if (user.length == 2) users.put(user[0], user[1]);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
  private void saveUsers() {
    try (PrintWriter out = new PrintWriter(file)) {
      for (Map.Entry<String, String> user : users.entrySet()) {
        out.println(user.getKey() + " : " + user.getValue());
      }
    } catch (FileNotFoundException e) {
        throw new RuntimeException(e);
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
    c.insets = new Insets(2, 2, 2, 2);

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
    c.anchor = GridBagConstraints.BOTH;
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

    username.requestFocusInWindow();
    loginButton.setFocusable(false);
    registerButton.setFocusable(false);
    cancelButton.setFocusable(false);

    this.setSize(400, 120);
    this.setLocationRelativeTo(null);
    this.setResizable(false);

    loadUsers();
    guestAccount();

    loginButton.addActionListener(e -> {
      if (authenticate(username.getText(), String.valueOf(password.getPassword()))) {
        loggedIn = true;
        dispose();
      }
    });
    registerButton.addActionListener(e -> {
      if (authenticate(username.getText(), String.valueOf(password.getPassword()))) {
        loggedIn = true;
        dispose();
      }
    });
    cancelButton.addActionListener(e -> {
      System.exit(0);
      dispose();
      loggedIn = false;
    });
  }
}
