package filter;

import models.Booking;

import java.util.List;

public interface Filter {
    List<Booking> filter(List<Booking> bookings);
}
