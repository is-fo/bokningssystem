package filter;

import models.Booking;

import java.util.List;
import java.util.stream.Collectors;

public class FilterBooked implements Filter {

    @Override
    public List<Booking> filter(List<Booking> bookings) {
        return bookings.stream()
                .filter(p -> p.isBooked())
                .collect(Collectors.toList());
    }
}
