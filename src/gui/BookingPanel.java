package gui;

import data.AppointmentManager;
import data.DatabaseManager;
import filter.*;
import models.Booking;
import models.Customer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.List;

// TODO: fixa så kalendern inte ändrar storlek osv. när man väljer ett datum
public class BookingPanel extends JPanel {
    private final AppointmentManager appointmentManager;
    private final Customer customer;
    private LocalDate currentMonth;
    private JTable calendarTable;
    private DefaultTableModel calendarModel;
    private JLabel monthLabel;
    private JPanel timePanel;
    private JPanel bookingsPanel;

    public BookingPanel(String email, AppointmentManager appointmentManager, Customer customer) {
        this.customer = customer;
        this.appointmentManager = appointmentManager;
        currentMonth = LocalDate.now();

        setLayout(new BorderLayout());

        //Skapar en skalad bild
        ImageIcon scaledIcon = ImageFactory.createScaledImageIcon("src/resources/background.jpg", 400, 500);

        // Lägger den skalade bilden som bakgrunden
        JLabel backgroundLabel = new JLabel(scaledIcon);
        backgroundLabel.setLayout(new GridBagLayout()); // Gör så att komponenter kan placeras ovanpå
        add(backgroundLabel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;

        // Kalender
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        gbc.weighty = 0.5;
        setupCalendar(backgroundLabel, gbc);

        // Mina bokningar
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weighty = 0.4;
        setupBookingsPanel(backgroundLabel, gbc);

        // Tillgängliga tider
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        gbc.weightx = 0.7;
        gbc.weighty = 1.0;
        setupTimePanel(backgroundLabel, gbc);

    }

    private void setupCalendar(JLabel parentPanel, GridBagConstraints gbc) {
        JPanel calendarPanel = new JPanel(new BorderLayout());
        calendarPanel.setBorder(BorderFactory.createTitledBorder(""));
        calendarPanel.setOpaque(false);

        monthLabel = new JLabel("", SwingConstants.CENTER);
        monthLabel.setFont(new Font("Times New Roman", Font.BOLD, 14));

        JButton prevButton = new JButton("<");
        JButton nextButton = new JButton(">");
        prevButton.setPreferredSize(new Dimension(40, 25));
        nextButton.setPreferredSize(new Dimension(40, 25));
        Font buttonFont = new Font("Times New Roman", Font.BOLD, 11);
        prevButton.setFocusable(false);
        nextButton.setFocusable(false);
        prevButton.setBackground(Color.WHITE);
        nextButton.setBackground(Color.WHITE);
        prevButton.setFont(buttonFont);
        nextButton.setFont(buttonFont);

        prevButton.addActionListener(e -> navigateMonth(-1));
        nextButton.addActionListener(e -> navigateMonth(1));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.add(prevButton, BorderLayout.WEST);
        headerPanel.add(monthLabel, BorderLayout.CENTER);
        headerPanel.add(nextButton, BorderLayout.EAST);

        calendarModel = new DefaultTableModel(null, new String[]{"Mån", "Tis", "Ons", "Tors", "Fre", "Lör", "Sön"});
        calendarTable = new JTable(calendarModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        calendarTable.setRowHeight(30);
        calendarTable.setOpaque(false);
        calendarTable.setCellSelectionEnabled(true);
        calendarTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                updateTimePanel();
            }
        });

        JScrollPane calendarScrollPane = new JScrollPane(calendarTable);
        calendarScrollPane.setOpaque(false);
        calendarScrollPane.getViewport().setOpaque(false);
        calendarPanel.add(headerPanel, BorderLayout.NORTH);
        calendarPanel.add(calendarScrollPane, BorderLayout.CENTER);

        parentPanel.add(calendarPanel, gbc);

        updateCalendar(); // Fyller kalendern initialt
    }

    private void setupBookingsPanel(JLabel parentPanel, GridBagConstraints gbc) {
        JPanel bookingsWrapper = new JPanel(new BorderLayout());
        bookingsWrapper.setBorder(BorderFactory.createTitledBorder("Mina Bokningar"));
        bookingsWrapper.setOpaque(false);

        bookingsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        bookingsPanel.setOpaque(false);
        bookingsWrapper.add(bookingsPanel, BorderLayout.CENTER);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.setFont(new Font("Times New Roman", Font.BOLD, 12));
        refreshButton.setPreferredSize(new Dimension(80, 25));
        refreshButton.setBackground(Color.WHITE);
        refreshButton.setFocusable(false);
        refreshButton.addActionListener(e -> refreshBookings());
        bookingsWrapper.add(refreshButton, BorderLayout.SOUTH);

        parentPanel.add(bookingsWrapper, gbc);

        refreshBookings(); // Laddar bokningar initialt
    }

    private void setupTimePanel(JLabel parentPanel, GridBagConstraints gbc) {
        timePanel = new JPanel();
        timePanel.setBorder(BorderFactory.createTitledBorder("Tillgängliga tider"));
        timePanel.setOpaque(false);

        timePanel.setLayout(new BoxLayout(timePanel, BoxLayout.Y_AXIS));
        timePanel.setPreferredSize(new Dimension(400, 250));

        timePanel.revalidate();
        timePanel.repaint();
        parentPanel.add(timePanel, gbc);
    }

    private void navigateMonth(int direction) {
        currentMonth = currentMonth.plusMonths(direction);
        updateCalendar();
    }

    private void updateCalendar() {
        monthLabel.setText(currentMonth.getMonth().toString() + " " + currentMonth.getYear());
        calendarModel.setRowCount(0);

        LocalDate firstDay = currentMonth.withDayOfMonth(1);
        int startDayOfWeek = firstDay.getDayOfWeek().getValue();
        LocalDate currentDay = firstDay.minusDays(startDayOfWeek - 1);

        for (int row = 0; row < 6; row++) {
            Object[] week = new Object[7];
            for (int col = 0; col < 7; col++) {
                if (currentDay.getMonth() == currentMonth.getMonth()) {
                    week[col] = currentDay.getDayOfMonth();
                } else {
                    week[col] = ""; // Rensar dagar utanför månaden
                }
                currentDay = currentDay.plusDays(1);
            }
            calendarModel.addRow(week);
        }
    }

    private void updateTimePanel() {
        int selectedRow = calendarTable.getSelectedRow();
        int selectedCol = calendarTable.getSelectedColumn();
        if (selectedRow < 0 || selectedCol < 0) return;

        Object dayObj = calendarTable.getValueAt(selectedRow, selectedCol);
        if (dayObj == null || dayObj.toString().isEmpty()) return;

        LocalDate selectedDate = currentMonth.withDayOfMonth(Integer.parseInt(dayObj.toString()));
        timePanel.removeAll();

        Filter unbookedOnDate = new AndFilter(new FilterOnDate(selectedDate), new NotFilter(new FilterBooked()));
        List<Booking> filteredBookings = unbookedOnDate.filter(DatabaseManager.getInstance().getAllBookings());

        filteredBookings.forEach(booking -> {
            JButton timeButton = new JButton(booking.getTimeFrame().getStartTime() + " - " +
                    booking.getTimeFrame().getEndTime());
            timeButton.setAlignmentX(Component.LEFT_ALIGNMENT);
            timeButton.setPreferredSize(new Dimension(300, 30));
            timeButton.setBackground(Color.WHITE);
            timeButton.addActionListener(e -> handleBooking(selectedDate, booking));
            timePanel.add(timeButton);
            timePanel.add(Box.createRigidArea(new Dimension(0, 8))); // Lägger mellanrum mellan knapparna
        });

        timePanel.setPreferredSize(new Dimension(400, 250));
        timePanel.revalidate();
        timePanel.repaint();
    }

    private void handleBooking(LocalDate date, Booking booking) {
        int choice = JOptionPane.showConfirmDialog(this,
                "Vill du boka denna tid? " + date + " kl " + booking.getTimeFrame().getStartTime(),
                "Bekräfta bokning",
                JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            DatabaseManager dbManager = DatabaseManager.getInstance();
            dbManager.updateBookingStatus(booking.getTimeFrame(), customer); // Associerar bokningen med kunden
            JOptionPane.showMessageDialog(this, "Tiden har bokats: " + date + " kl " + booking.getTimeFrame().getStartTime());
            updateTimePanel(); // Uppdatera tillgängliga tider
            refreshBookings(); // Uppdatera "Mina bokningar"
        }
    }

    private void refreshBookings() {
        bookingsPanel.removeAll();

        List<Booking> userBookings = appointmentManager.getAppointmentsForUser(customer);
        for (Booking booking : userBookings) {
            JPanel bookingPanel = new JPanel();
            bookingPanel.setLayout(new BorderLayout());
            bookingPanel.setOpaque(false);

            JLabel bookingLabel = new JLabel(booking.getTimeFrame().getDate() + " | " +
                    booking.getTimeFrame().getStartTime() + " - " +
                    booking.getTimeFrame().getEndTime() + " | ");

            JButton cancelButton = new JButton("Avboka");
            cancelButton.setFont(new Font("Times New Roman", Font.BOLD, 12));
            cancelButton.setFocusable(false);
            cancelButton.setBackground(Color.WHITE);
            cancelButton.addActionListener(e -> {
                appointmentManager.cancelAppointment(booking);
                JOptionPane.showMessageDialog(this, "Bokning avbokad");
                refreshBookings(); // Uppdatera listan
            });

            bookingPanel.add(bookingLabel, BorderLayout.CENTER);
            bookingPanel.add(cancelButton, BorderLayout.EAST);
            bookingsPanel.add(bookingPanel);
        }

        bookingsPanel.revalidate();
        bookingsPanel.repaint();
    }
}