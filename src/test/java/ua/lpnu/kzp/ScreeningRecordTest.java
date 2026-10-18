package ua.lpnu.kzp;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class ScreeningRecordTest {

    private static final LocalDateTime START =
            LocalDateTime.of(2026, 10, 18, 19, 30);

    @Test
    void shouldCreateValidScreeningRecord() {
        ScreeningRecord record = new ScreeningRecord(
                "Dune", "Hall 1", START, 180.50, 75);

        assertEquals("Dune", record.film());
        assertEquals("Hall 1", record.hall());
        assertEquals(START, record.startAt());
        assertEquals(180.50, record.ticketPrice());
        assertEquals(75, record.soldTickets());
    }

    @Test
    void shouldCompareRecordsByAllFields() {
        ScreeningRecord first = new ScreeningRecord(
                "Dune", "Hall 1", START, 180.50, 75);

        ScreeningRecord same = new ScreeningRecord(
                "Dune", "Hall 1", START, 180.50, 75);

        ScreeningRecord different = new ScreeningRecord(
                "Dune", "Hall 2", START, 180.50, 75);

        assertEquals(first, same);
        assertEquals(first.hashCode(), same.hashCode());
        assertNotEquals(first, different);
    }

    @Test
    void shouldRejectInvalidFilmAndHall() {
        assertThrows(NullPointerException.class,
                () -> new ScreeningRecord(null, "Hall 1", START, 100, 10));

        assertThrows(IllegalArgumentException.class,
                () -> new ScreeningRecord(" ", "Hall 1", START, 100, 10));

        assertThrows(IllegalArgumentException.class,
                () -> new ScreeningRecord("Dune", " ", START, 100, 10));
    }

    @Test
    void shouldRejectNullStartTime() {
        assertThrows(NullPointerException.class,
                () -> new ScreeningRecord("Dune", "Hall 1", null, 100, 10));
    }

    @Test
    void shouldRejectInvalidTicketPrice() {
        assertThrows(IllegalArgumentException.class,
                () -> new ScreeningRecord("Dune", "Hall 1", START, -1, 10));

        assertThrows(IllegalArgumentException.class,
                () -> new ScreeningRecord(
                        "Dune", "Hall 1", START, Double.NaN, 10));

        assertThrows(IllegalArgumentException.class,
                () -> new ScreeningRecord(
                        "Dune", "Hall 1", START,
                        Double.POSITIVE_INFINITY, 10));
    }

    @Test
    void shouldRejectNegativeSoldTickets() {
        assertThrows(IllegalArgumentException.class,
                () -> new ScreeningRecord("Dune", "Hall 1", START, 100, -1));
    }

    @Test
    void shouldAllowZeroPriceAndZeroTickets() {
        ScreeningRecord record = new ScreeningRecord(
                "Dune", "Hall 1", START, 0, 0);

        assertEquals(0, record.ticketPrice());
        assertEquals(0, record.soldTickets());
    }

    @Test
    void shouldExposeCorrectCsvColumnNames() {
        Map<String, String> columns = Arrays.stream(
                        ScreeningRecord.class.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(CsvColumn.class))
                .collect(Collectors.toMap(
                        Field::getName,
                        field -> field.getAnnotation(CsvColumn.class).value()
                ));

        assertEquals(Map.of(
                "film", "фільм",
                "hall", "зал",
                "startAt", "час",
                "ticketPrice", "ціна",
                "soldTickets", "продано"
        ), columns);
    }
}