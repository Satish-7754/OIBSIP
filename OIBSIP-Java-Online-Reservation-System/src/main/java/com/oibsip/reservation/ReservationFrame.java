package com.oibsip.reservation;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class ReservationFrame extends JFrame {
    private final JTextField passenger = new JTextField();
    private final JTextField trainNumber = new JTextField();
    private final JTextField trainName = new JTextField();
    private final JComboBox<String> classType =
            new JComboBox<>(new String[]{"Sleeper", "AC 3 Tier", "AC 2 Tier", "AC First Class"});
    private final JTextField journeyDate = new JTextField(LocalDate.now().plusDays(1).toString());
    private final JTextField source = new JTextField();
    private final JTextField destination = new JTextField();

    private final JTextField pnrSearch = new JTextField();
    private final ReservationService service = new ReservationService();

    public ReservationFrame() {
        setTitle("OIBSIP - Online Reservation System");
        setSize(720, 560);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel form = new JPanel(new GridLayout(7, 2, 8, 8));
        form.setBorder(BorderFactory.createTitledBorder("Reservation Form"));

        trainName.setEditable(false);

        addField(form, "Passenger Name", passenger);
        addField(form, "Train Number", trainNumber);
        addField(form, "Train Name", trainName);
        addField(form, "Class Type", classType);
        addField(form, "Journey Date (YYYY-MM-DD)", journeyDate);
        addField(form, "Source Station", source);
        addField(form, "Destination Station", destination);

        JButton lookupTrain = new JButton("Find Train");
        JButton book = new JButton("Book Ticket");
        JButton clear = new JButton("Clear");

        JPanel actions = new JPanel();
        actions.add(lookupTrain);
        actions.add(book);
        actions.add(clear);

        JPanel cancellation = new JPanel(new GridLayout(2, 2, 8, 8));
        cancellation.setBorder(BorderFactory.createTitledBorder("Cancellation"));
        cancellation.add(new JLabel("PNR Number"));
        cancellation.add(pnrSearch);
        JButton fetch = new JButton("Fetch Booking");
        JButton cancel = new JButton("Confirm Cancellation");
        cancellation.add(fetch);
        cancellation.add(cancel);

        main.add(form, BorderLayout.NORTH);
        main.add(actions, BorderLayout.CENTER);
        main.add(cancellation, BorderLayout.SOUTH);
        add(main);

        lookupTrain.addActionListener(e -> lookupTrain());
        book.addActionListener(e -> bookTicket());
        clear.addActionListener(e -> clearForm());
        fetch.addActionListener(e -> fetchBooking());
        cancel.addActionListener(e -> cancelBooking());
    }

    private void addField(JPanel p, String label, JComponent field) {
        p.add(new JLabel(label));
        p.add(field);
    }

    private void lookupTrain() {
        try {
            int number = Integer.parseInt(trainNumber.getText().trim());
            String name = service.findTrainName(number);
            if (name == null) {
                trainName.setText("");
                JOptionPane.showMessageDialog(this, "Train number not found.");
            } else {
                trainName.setText(name);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Train number must be numeric.");
        }
    }

    private void bookTicket() {
        if (passenger.getText().trim().isEmpty() ||
                trainNumber.getText().trim().isEmpty() ||
                journeyDate.getText().trim().isEmpty() ||
                source.getText().trim().isEmpty() ||
                destination.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all required fields.");
            return;
        }

        try {
            int number = Integer.parseInt(trainNumber.getText().trim());
            LocalDate.parse(journeyDate.getText().trim());

            String name = service.findTrainName(number);
            if (name == null) {
                JOptionPane.showMessageDialog(this, "Invalid train number.");
                return;
            }

            trainName.setText(name);

            if (source.getText().trim().equalsIgnoreCase(destination.getText().trim())) {
                JOptionPane.showMessageDialog(this, "Source and destination must be different.");
                return;
            }

            String pnr = service.book(
                    passenger.getText().trim(),
                    number,
                    name,
                    classType.getSelectedItem().toString(),
                    journeyDate.getText().trim(),
                    source.getText().trim(),
                    destination.getText().trim()
            );

            if (pnr == null) {
                JOptionPane.showMessageDialog(this, "Booking failed.");
            } else {
                JOptionPane.showMessageDialog(this,
                        "Booking successful!\n\nPNR: " + pnr,
                        "Confirmation",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Train number must be numeric.");
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Use date format YYYY-MM-DD.");
        }
    }

    private void fetchBooking() {
        String pnr = pnrSearch.getText().trim();
        if (pnr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter a PNR number.");
            return;
        }

        String booking = service.getBooking(pnr);
        if (booking == null) {
            JOptionPane.showMessageDialog(this, "Booking not found.");
        } else {
            JOptionPane.showMessageDialog(this, booking, "Booking Details",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void cancelBooking() {
        String pnr = pnrSearch.getText().trim();
        if (pnr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter a PNR number.");
            return;
        }

        String booking = service.getBooking(pnr);
        if (booking == null) {
            JOptionPane.showMessageDialog(this, "Booking not found.");
            return;
        }

        int choice = JOptionPane.showConfirmDialog(
                this,
                booking + "\n\nAre you sure you want to cancel this booking?",
                "Confirm Cancellation",
                JOptionPane.YES_NO_OPTION
        );

        if (choice == JOptionPane.YES_OPTION) {
            if (service.cancel(pnr)) {
                JOptionPane.showMessageDialog(this, "Booking cancelled successfully.");
                pnrSearch.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Cancellation failed.");
            }
        }
    }

    private void clearForm() {
        passenger.setText("");
        trainNumber.setText("");
        trainName.setText("");
        journeyDate.setText(LocalDate.now().plusDays(1).toString());
        source.setText("");
        destination.setText("");
        classType.setSelectedIndex(0);
    }
}
