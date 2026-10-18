package ua.lpnu.kzp;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a cinema screening record for CSV import and export.
 *
 * @param film film title
 * @param hall cinema hall
 * @param startAt screening start date and time
 * @param ticketPrice ticket price
 * @param soldTickets number of sold tickets
 */
public record ScreeningRecord(
        @CsvColumn("фільм") String film,
        @CsvColumn("зал") String hall,
        @CsvColumn("час") LocalDateTime startAt,
        @CsvColumn("ціна") double ticketPrice,
        @CsvColumn("продано") int soldTickets
) {

    public ScreeningRecord {
        Objects.requireNonNull(film, "Film must not be null");
        Objects.requireNonNull(hall, "Hall must not be null");
        Objects.requireNonNull(startAt, "Start time must not be null");

        if (film.isBlank()) {
            throw new IllegalArgumentException("Film must not be blank");
        }

        if (hall.isBlank()) {
            throw new IllegalArgumentException("Hall must not be blank");
        }

        if (!Double.isFinite(ticketPrice) || ticketPrice < 0) {
            throw new IllegalArgumentException(
                    "Ticket price must be finite and non-negative");
        }

        if (soldTickets < 0) {
            throw new IllegalArgumentException(
                    "Sold tickets must not be negative");
        }
    }
}