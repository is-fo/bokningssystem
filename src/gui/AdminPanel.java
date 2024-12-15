package gui;

import data.DatabaseManager;
import models.Booking;
import models.TimeFrame;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AdminPanel extends JPanel {

    private final JTextArea bookingDetails;

    public AdminPanel() {

        Font font = new Font("Times New Roman", Font.BOLD, 20);

        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(400, 500));

        //Skapar en skalad bild
        ImageIcon scaledIcon = ImageFactory.createScaledImageIcon("src/resources/background.jpg", 400, 500);

        // Lägger den skalade bilden som bakgrunden
        JLabel backgroundLabel = new JLabel(scaledIcon);
        backgroundLabel.setLayout(new BorderLayout()); // Gör så att komponenter kan placeras ovanpå
        add(backgroundLabel);

        JPanel overlayPanel = new JPanel(new BorderLayout());
        overlayPanel.setOpaque(false); // Gör panelen genomskinlig
        backgroundLabel.add(overlayPanel);


        // Rubrik
        JLabel headerLabel = new JLabel("Adminvy", SwingConstants.CENTER);
        headerLabel.setForeground(Color.BLACK);
        headerLabel.setFont(font);
        headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        overlayPanel.add(headerLabel, BorderLayout.NORTH);;

        // Textområdet för att visa bokningar
        bookingDetails = new JTextArea();
        bookingDetails.setFont(font.deriveFont(Font.PLAIN).deriveFont(14.0f));
        bookingDetails.setForeground(Color.BLACK);
        bookingDetails.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(bookingDetails);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        overlayPanel.add(scrollPane, BorderLayout.CENTER);

        // Knapp-panel för admin-funktioner
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton refreshButton = new JButton("Uppdatera Bokningar");
        JButton addTimeButton = new JButton("Lägg till Tid");

        refreshButton.setFont(font.deriveFont(14.0f));
        addTimeButton.setFont(font.deriveFont(14.0f));

        refreshButton.setBackground(Color.WHITE);
        addTimeButton.setBackground(Color.WHITE);

        refreshButton.addActionListener(e -> updateBookingDetails());
        addTimeButton.addActionListener(e -> addAvailableTime());

        buttonPanel.add(refreshButton);
        buttonPanel.add(addTimeButton);
        overlayPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Ladda bokningar vid start
        updateBookingDetails();
    }

    private void updateBookingDetails() {
        List<Booking> allBookings = DatabaseManager.getInstance().getAllBookings();

        StringBuilder details = new StringBuilder("Alla bokningar:\n");
        for (Booking booking : allBookings) {
            details.append(booking.getTimeFrame().getDate()).append(" | ")
                    .append(booking.getTimeFrame().getStartTime()).append(" - ")
                    .append(booking.getTimeFrame().getEndTime()).append(" | ");
            if (booking.isBooked()) {
                details.append("Bokad av: ").append(booking.getCustomer().getName());
            } else {
                details.append("Tillgänglig");
            }
            details.append("\n");
        }
        bookingDetails.setText(details.toString());
    }

    private void addAvailableTime() {
        // Få input från admin
        String date = JOptionPane.showInputDialog("Ange datum (YYYY-MM-DD):");
        String startTime = JOptionPane.showInputDialog("Ange starttid (HH:mm):");
        String endTime = JOptionPane.showInputDialog("Ange sluttid (HH:mm):");

        // Validera input
        if (date == null || startTime == null || endTime == null ||
                date.isEmpty() || startTime.isEmpty() || endTime.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Alla fält måste vara ifyllda.", "Felmeddelande", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            TimeFrame timeFrame = new TimeFrame(date, startTime, endTime);
            DatabaseManager.getInstance().createBooking(new Booking(timeFrame, "Available"));
            JOptionPane.showMessageDialog(this, "Tid tillagd: " + date + " " + startTime + " - " + endTime);
            updateBookingDetails(); // Uppdatera adminvyn
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Felaktigt format. Kontrollera datum och tider.", "Felmeddelande", JOptionPane.ERROR_MESSAGE);
        }
    }
}
