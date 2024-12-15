package filter;

import models.Booking;

import java.util.List;

public class AndFilter implements Filter {

    private final Filter filter1;
    private final Filter filter2;

    public AndFilter(Filter filter1, Filter filter2) {
        this.filter1 = filter1;
        this.filter2 = filter2;
    }

    @Override
    public List<Booking> filter(List<Booking> bookings) {
        return filter2.filter(filter1.filter(bookings));
    }
}
