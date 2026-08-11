package com.oibsip.reservation;

import java.sql.*;

public final class Database {
    private static final String URL = "jdbc:sqlite:reservation.db";

    private Database() {}

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static void initialize() {
        String users = "CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username TEXT UNIQUE NOT NULL," +
                "password TEXT NOT NULL)";

        String trains = "CREATE TABLE IF NOT EXISTS trains (" +
                "train_number INTEGER PRIMARY KEY," +
                "train_name TEXT NOT NULL)";

        String reservations = "CREATE TABLE IF NOT EXISTS reservations (" +
                "pnr TEXT PRIMARY KEY," +
                "passenger_name TEXT NOT NULL," +
                "train_number INTEGER NOT NULL," +
                "train_name TEXT NOT NULL," +
                "class_type TEXT NOT NULL," +
                "journey_date TEXT NOT NULL," +
                "source TEXT NOT NULL," +
                "destination TEXT NOT NULL)";

        try (Connection c = connect(); Statement s = c.createStatement()) {
            s.execute(users);
            s.execute(trains);
            s.execute(reservations);

            try (PreparedStatement p = c.prepareStatement(
                    "INSERT OR IGNORE INTO users(username,password) VALUES(?,?)")) {
                p.setString(1, "admin");
                p.setString(2, "admin123");
                p.executeUpdate();
            }

            try (PreparedStatement p = c.prepareStatement(
                    "INSERT OR IGNORE INTO trains(train_number,train_name) VALUES(?,?)")) {
                addTrain(p, 12001, "Godavari Express");
                addTrain(p, 12737, "Goutami Express");
                addTrain(p, 12727, "Godavari Superfast");
                addTrain(p, 17015, "Visakha Express");
                addTrain(p, 17487, "Tirumala Express");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database initialization failed", e);
        }
    }

    private static void addTrain(PreparedStatement p, int number, String name) throws SQLException {
        p.setInt(1, number);
        p.setString(2, name);
        p.executeUpdate();
    }
}
