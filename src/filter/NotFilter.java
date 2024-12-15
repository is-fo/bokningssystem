package filter;

import models.Booking;

import java.util.List;
import java.util.stream.Collectors;

public class NotFilter implements Filter {
    private final Filter filter;

    public NotFilter(Filter filter) {
        this.filter = filter;
    }

    @Override
    public List<Booking> filter(List<Booking> bookings) {
        return bookings.stream()
                .filter(p -> !filter.filter(List.of(p)).contains(p))
                .collect(Collectors.toList());
    }
}
