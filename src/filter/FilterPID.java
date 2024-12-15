package filter;

import models.Booking;

import java.util.List;
import java.util.stream.Collectors;

public class FilterPID implements Filter {

    private final String pID;

    public FilterPID(String pID) {
        this.pID = pID;
    }

    @Override
    public List<Booking> filter(List<Booking> bookings) {
        return bookings.stream()
                .filter(p -> p.getCustomer().getPID().equals(pID))
                .collect(Collectors.toList());
    }
}
