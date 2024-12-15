package filter;

import models.Booking;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Inte implementerat eftersom beskrivningen på bokningar inte används riktigt.
 * Tanken är att beskrivningen t.ex ska vara "Spinning" eller "Yoga"
 * och användaren ska kunna filtrera genom t.ex en dropdown meny där de väljer förinställda val.
 */
public class FilterType implements Filter {

    String description;

    public FilterType(String description) {
        this.description = description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public List<Booking> filter(List<Booking> bookings) {
        return bookings.stream()
                .filter(p -> p.getDescription().equals(description))
                .collect(Collectors.toList());
    }
}
