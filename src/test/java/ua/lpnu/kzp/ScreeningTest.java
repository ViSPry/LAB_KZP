package ua.lpnu.kzp;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ScreeningTest {

    @Test
    void constructorCreatesValidScreening() {
        Screening screening = new RegularScreening(
                "Дюна 2",
                1,
                200.0,
                100,
                166
        );

        assertEquals("Дюна 2", screening.getFilm());
        assertEquals(1, screening.getHall());
        assertEquals(200.0, screening.getTicketPrice());
        assertEquals(100, screening.getSold());
        assertEquals(166, screening.getDurationMin());
    }

    @Test
    void constructorAcceptsBoundaryValues() {
        Screening screening = new RegularScreening(
                "Фільм",
                1,
                0.0,
                0,
                1
        );

        assertEquals(0.0, screening.getTicketPrice());
        assertEquals(0, screening.getSold());
    }

    @Test
    void constructorRejectsBlankFilm() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new RegularScreening(
                        "   ",
                        1,
                        100.0,
                        10,
                        120
                )
        );
    }

    @Test
    void constructorRejectsInvalidHall() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new RegularScreening(
                        "Фільм",
                        0,
                        100.0,
                        10,
                        120
                )
        );
    }

    @Test
    void constructorRejectsNegativeTicketPrice() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new RegularScreening(
                        "Фільм",
                        1,
                        -1.0,
                        10,
                        120
                )
        );
    }

    @Test
    void constructorRejectsNonFiniteTicketPrice() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new RegularScreening(
                        "Фільм",
                        1,
                        Double.NaN,
                        10,
                        120
                )
        );
    }

    @Test
    void constructorRejectsNegativeSold() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new RegularScreening(
                        "Фільм",
                        1,
                        100.0,
                        -1,
                        120
                )
        );
    }

    @Test
    void constructorRejectsInvalidDuration() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new RegularScreening(
                        "Фільм",
                        1,
                        100.0,
                        10,
                        0
                )
        );
    }

    @Test
    void fromCsvCreatesScreening() {
        Screening screening = Screening.fromCsv(
                "Дюна 2;1;200.0;100;166"
        );

        assertEquals("Дюна 2", screening.getFilm());
        assertEquals(1, screening.getHall());
        assertEquals(200.0, screening.getTicketPrice());
        assertEquals(100, screening.getSold());
        assertEquals(166, screening.getDurationMin());
        assertInstanceOf(PremiumScreening.class, screening);
    }

    @Test
    void fromCsvRejectsWrongFieldCount() {
        assertThrows(
                IllegalArgumentException.class,
                () -> Screening.fromCsv(
                        "Дюна 2;1;200.0"
                )
        );
    }

    @Test
    void fromCsvRejectsInvalidNumber() {
        assertThrows(
                IllegalArgumentException.class,
                () -> Screening.fromCsv(
                        "Дюна 2;abc;200.0;100;166"
                )
        );
    }

    @Test
    void regularScreeningPreservesBuilderValidScenario() {
        Screening screening = new RegularScreening(
                "Інтерстеллар",
                2,
                180.0,
                150,
                169
        );

        assertEquals("Інтерстеллар", screening.getFilm());
        assertEquals(2, screening.getHall());
        assertEquals(180.0, screening.getTicketPrice());
        assertEquals(150, screening.getSold());
        assertEquals(169, screening.getDurationMin());
    }

    @Test
    void constructorCannotBypassValidation() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new RegularScreening(
                        "Матриця",
                        -1,
                        150.0,
                        50,
                        136
                )
        );
    }
}