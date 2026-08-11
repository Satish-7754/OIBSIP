package com.oibsip.reservation;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private final JTextField username = new JTextField();
    private final JPasswordField password = new JPasswordField();

    public LoginFrame() {
        setTitle("OIBSIP Reservation System - Login");
        setSize(420, 260);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        panel.add(new JLabel("Username"));
        panel.add(username);
        panel.add(new JLabel("Password"));
        panel.add(password);

        JButton login = new JButton("Login");
        panel.add(new JLabel());
        panel.add(login);

        JLabel hint = new JLabel("Demo: admin / admin123");
        panel.add(hint);

        login.addActionListener(e -> authenticate());
        add(panel);
    }

    private void authenticate() {
        String user = username.getText().trim();
        String pass = new String(password.getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter username and password.");
            return;
        }

        if (new AuthService().login(user, pass)) {
            dispose();
            new ReservationFrame().setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password.");
        }
    }
}
