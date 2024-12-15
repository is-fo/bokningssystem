package data;

import filter.Filter;
import filter.FilterTimeFrame;
import models.Booking;
import models.Customer;
import models.TimeFrame;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class AppointmentManager {

    private static AppointmentManager instance;
    private final DatabaseDao databaseDao;

    public AppointmentManager(DatabaseDao dataBaseDao) {
        this.databaseDao = dataBaseDao;
    }

    public static synchronized AppointmentManager getInstance(DatabaseDao databaseDao) {
        if (instance == null) {
            instance = new AppointmentManager(databaseDao);
        }
        return instance;
    }

    public boolean cancelAppointment(Booking b) {
        List<Booking> bookings = databaseDao.getAppointmentsForUser(b.getCustomer());
        Filter timeFrameFilter = new FilterTimeFrame(b.getTimeFrame());
        List<Booking> match = timeFrameFilter.filter(bookings);
        if (!match.isEmpty()) {
            Booking bookingToCancel = match.getFirst();
            bookingToCancel.setCustomer(null);
            bookingToCancel.setDescription("Available");
            databaseDao.updateBookingStatus(bookingToCancel.getTimeFrame(), null);
            return true;
        }

        return false;
    }

    private boolean timeFrameOverlaps(LocalDate date, TimeFrame newTimeFrame) {
        List<Booking> bookings = databaseDao.getAllBookings();
        for (Booking booking : bookings) {
            if (booking.getTimeFrame().getDate().equals(date)) {
                TimeFrame existingFrame = booking.getTimeFrame();
                if (existingFrame.getEndTime().isAfter(newTimeFrame.getStartTime()) &&
                        existingFrame.getStartTime().isBefore(newTimeFrame.getEndTime())) {
                    return true;
                }
            }
        }
        return false;
    }

    public List<Booking> getAppointmentsForUser(Customer customer) {
        return DatabaseManager.getInstance().getAppointmentsForUser(customer);
    }
}

