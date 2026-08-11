package com.oibsip.reservation;

import java.sql.*;

public class AuthService {
    public boolean login(String username, String password) {
        String sql = "SELECT 1 FROM users WHERE username=? AND password=?";
        try (Connection c = Database.connect();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, username);
            p.setString(2, password);
            try (ResultSet rs = p.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            return false;
        }
    }
}
