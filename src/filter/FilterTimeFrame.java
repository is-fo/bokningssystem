package filter;

import models.Booking;
import models.TimeFrame;

import java.util.List;
import java.util.stream.Collectors;

public class FilterTimeFrame implements Filter {

    private final TimeFrame timeFrame;

    public FilterTimeFrame(TimeFrame timeFrame) {
        this.timeFrame = timeFrame;
    }

    @Override
    public List<Booking> filter(List<Booking> bookings) {
        return bookings.stream()
                .filter(p -> p.getTimeFrame().equals(timeFrame))
                .collect(Collectors.toList());
    }
}
