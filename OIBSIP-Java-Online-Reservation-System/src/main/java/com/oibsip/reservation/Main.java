package com.oibsip.reservation;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        Database.initialize();
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
