package filter;

import models.Booking;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class FilterOnDate implements Filter {

    private final LocalDate selectedDate;

    public FilterOnDate(LocalDate selectedDate) {
        this.selectedDate = selectedDate;
    }

    @Override
    public List<Booking> filter(List<Booking> bookings) {
        return bookings.stream()
                .filter(booking -> booking.getTimeFrame().getDate().equals(selectedDate))
                .collect(Collectors.toList());
    }
}
