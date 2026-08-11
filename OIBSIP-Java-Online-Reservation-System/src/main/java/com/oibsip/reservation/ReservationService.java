package com.oibsip.reservation;

import java.sql.*;
import java.util.UUID;

public class ReservationService {

    public String findTrainName(int trainNumber) {
        String sql = "SELECT train_name FROM trains WHERE train_number=?";
        try (Connection c = Database.connect();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setInt(1, trainNumber);
            try (ResultSet rs = p.executeQuery()) {
                return rs.next() ? rs.getString("train_name") : null;
            }
        } catch (SQLException e) {
            return null;
        }
    }

    public String book(String passenger, int trainNumber, String trainName,
                       String classType, String date, String source, String destination) {
        String pnr = "OIB" + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        String sql = "INSERT INTO reservations VALUES(?,?,?,?,?,?,?,?)";

        try (Connection c = Database.connect();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, pnr);
            p.setString(2, passenger);
            p.setInt(3, trainNumber);
            p.setString(4, trainName);
            p.setString(5, classType);
            p.setString(6, date);
            p.setString(7, source);
            p.setString(8, destination);
            p.executeUpdate();
            return pnr;
        } catch (SQLException e) {
            return null;
        }
    }

    public String getBooking(String pnr) {
        String sql = "SELECT * FROM reservations WHERE pnr=?";
        try (Connection c = Database.connect();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, pnr);
            try (ResultSet rs = p.executeQuery()) {
                if (!rs.next()) return null;
                return "PNR: " + rs.getString("pnr") +
                        "\nPassenger: " + rs.getString("passenger_name") +
                        "\nTrain: " + rs.getInt("train_number") + " - " + rs.getString("train_name") +
                        "\nClass: " + rs.getString("class_type") +
                        "\nJourney Date: " + rs.getString("journey_date") +
                        "\nFrom: " + rs.getString("source") +
                        "\nTo: " + rs.getString("destination");
            }
        } catch (SQLException e) {
            return null;
        }
    }

    public boolean cancel(String pnr) {
        String sql = "DELETE FROM reservations WHERE pnr=?";
        try (Connection c = Database.connect();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, pnr);
            return p.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }
}
