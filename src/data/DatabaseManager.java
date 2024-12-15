package data;

import filter.*;
import models.Booking;
import models.Customer;
import models.TimeFrame;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager implements DatabaseDao {

    private static DatabaseManager instance;
    private final List<Booking> bookings; // Cachar bokningar i minnet
    private static final String FILE_PATH = "src/resources/bookings.csv";


    private DatabaseManager() {
        bookings = new ArrayList<>();
        loadBookingsFromFile();
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    // Ladda bokningar från fil
    private void loadBookingsFromFile() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return; // Om filen inte finns, lämna listan tom
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    TimeFrame timeFrame = new TimeFrame(parts[0], parts[1], parts[2]);
                    String description = parts[3];
                    Customer customer = null;
                    if (parts.length > 4) {
                        customer = new Customer(parts[4], parts[5], parts[6], parts[7], parts[8]);
                    }
                    bookings.add(new Booking(timeFrame, description, customer));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error loading bookings from file", e);
        }
    }

    // Skriv bokningar till fil
    private void saveBookingsToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Booking booking : bookings) {
                StringBuilder line = new StringBuilder();
                line.append(booking.getTimeFrame().getDate()).append(",")
                        .append(booking.getTimeFrame().getStartTime()).append(",")
                        .append(booking.getTimeFrame().getEndTime()).append(",")
                        .append(booking.getDescription());
                if (booking.isBooked()) {
                    Customer customer = booking.getCustomer();
                    line.append(",").append(customer.getPID()).append(",")
                            .append(customer.getName()).append(",")
                            .append(customer.getEmail()).append(",")
                            .append(customer.getPhoneNumber()).append(",")
                            .append(customer.getPassword());
                }
                writer.write(line.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error saving bookings to file", e);
        }
    }


    @Override
    public void createBooking(Booking booking) {
        bookings.add(booking); // Lägg till bokning i listan
        saveBookingsToFile();
    }

    @Override
    public void deleteBooking(Booking booking) {
        bookings.removeIf(b -> b.getTimeFrame().equals(booking.getTimeFrame())); // Ta bort matchande bokning
        saveBookingsToFile();
    }

    @Override
    public List<Booking> getAllBookings() {
        return new ArrayList<>(bookings); // Returnerar en kopia av listan
    }
    // Filtrerar bokningar baserat på användaren
    @Override
    public List<Booking> getAppointmentsForUser(Customer customer) {
        Filter bookedByThisCustomer = new AndFilter(new FilterBooked(), new FilterPID(customer.getPID()));
        return bookedByThisCustomer.filter(new ArrayList<>(bookings));
    }

    public void updateBookingStatus(TimeFrame timeFrame, Customer customer) {
        Filter timeFrameFilter = new FilterTimeFrame(timeFrame);
        List<Booking> matchingBookings = timeFrameFilter.filter(bookings);

        matchingBookings.forEach(booking -> {
            if (customer == null) {
                // Gör bokningen tillgänglig
                booking.setCustomer(null);
                booking.setDescription("Available");
            } else {
                // Uppdatera kund
                booking.setCustomer(customer);
                booking.setDescription("Booked");
            }
        });

        saveBookingsToFile(); // Uppdatera filen
    }
}

