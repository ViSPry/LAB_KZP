package ua.lpnu.kzp;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TicketSalesTest {

    @Test
    void recordStoresValidStatistics() {
        TicketSales sales = new TicketSales(
                5,
                5,
                98700.0,
                182.0,
                150
        );

        assertEquals(5, sales.validCount());
        assertEquals(5, sales.invalidCount());
        assertEquals(98700.0, sales.totalRevenue());
        assertEquals(182.0, sales.averageTicketPrice());
        assertEquals(150, sales.maxSold());
    }

    @Test
    void recordAcceptsBoundaryValues() {
        TicketSales sales = new TicketSales(
                0,
                0,
                0.0,
                0.0,
                0
        );

        assertEquals(0, sales.validCount());
        assertEquals(0, sales.invalidCount());
        assertEquals(0.0, sales.totalRevenue());
        assertEquals(0.0, sales.averageTicketPrice());
        assertEquals(0, sales.maxSold());
    }

    @Test
    void recordRejectsNegativeValidCount() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new TicketSales(
                        -1,
                        0,
                        0.0,
                        0.0,
                        0
                )
        );
    }

    @Test
    void recordRejectsNegativeInvalidCount() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new TicketSales(
                        0,
                        -1,
                        0.0,
                        0.0,
                        0
                )
        );
    }

    @Test
    void recordRejectsNegativeRevenue() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new TicketSales(
                        1,
                        0,
                        -1.0,
                        100.0,
                        10
                )
        );
    }

    @Test
    void recordRejectsNonFiniteAveragePrice() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new TicketSales(
                        1,
                        0,
                        100.0,
                        Double.NaN,
                        10
                )
        );
    }

    @Test
    void recordRejectsNegativeMaxSold() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new TicketSales(
                        1,
                        0,
                        100.0,
                        100.0,
                        -1
                )
        );
    }
}