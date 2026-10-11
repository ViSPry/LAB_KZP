
package ua.lpnu.kzp;

import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тести Stream API-запитів для варіанта №19.
 */
class ScreeningQueriesTest {

    private static final LocalTime TIME_10 = LocalTime.of(10, 0);
    private static final LocalTime TIME_12 = LocalTime.of(12, 0);
    private static final LocalTime TIME_14 = LocalTime.of(14, 0);
    private static final LocalTime TIME_16 = LocalTime.of(16, 0);
    private static final LocalTime TIME_18 = LocalTime.of(18, 0);
    private static final LocalTime TIME_20 = LocalTime.of(20, 0);

    /**
     * Шість тестових сеансів, включно з однаковою кількістю
     * продажів, повторною назвою фільму та різними типами.
     */
    private List<Screening> sampleScreenings() {
        return List.of(
                new RegularScreening(
                        "Avatar", 2, 150.0, 100, 180, TIME_18
                ),
                new PremiumScreening(
                        "Dune", 1, 200.0, 150, 160, 20.0, TIME_20
                ),
                new RegularScreening(
                        "Interstellar", 3, 180.0, 150, 169, TIME_12
                ),
                new RegularScreening(
                        "Avatar", 2, 150.0, 50, 180, TIME_10
                ),
                new PremiumScreening(
                        "Oppenheimer", 1, 250.0, 80, 180, 30.0, TIME_16
                ),
                new RegularScreening(
                        "Matrix", 4, 120.0, 0, 136, TIME_14
                )
        );
    }

    @Test
    void filterBySoldUsesStrictThreshold() {
        List<Screening> result =
                ScreeningQueries.filterBySold(sampleScreenings(), 100);

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(s -> s.getSold() > 100));
        assertFalse(result.stream().anyMatch(s -> s.getSold() == 100));
    }

    @Test
    void filterBySoldReturnsEmptyWhenNothingMatches() {
        assertTrue(
                ScreeningQueries.filterBySold(sampleScreenings(), 200)
                        .isEmpty()
        );
    }

    @Test
    void filmNamesPreservesOrderAndDuplicates() {
        assertEquals(
                List.of(
                        "Avatar",
                        "Dune",
                        "Interstellar",
                        "Avatar",
                        "Oppenheimer",
                        "Matrix"
                ),
                ScreeningQueries.filmNames(sampleScreenings())
        );
    }

    @Test
    void revenueByHallUsesPolymorphicRevenue() {
        Map<Integer, Double> result =
                ScreeningQueries.revenueByHall(sampleScreenings());

        assertEquals(4, result.size());
        assertEquals(22500.0, result.get(2), 0.001);
        assertEquals(55400.0, result.get(1), 0.001);
        assertEquals(27000.0, result.get(3), 0.001);
        assertEquals(0.0, result.get(4), 0.001);
    }

    @Test
    void ticketPriceStatisticsAreCorrect() {
        var stats = ScreeningQueries.ticketPriceStatistics(
                sampleScreenings()
        );

        assertEquals(6, stats.getCount());
        assertEquals(1050.0, stats.getSum(), 0.001);
        assertEquals(120.0, stats.getMin(), 0.001);
        assertEquals(250.0, stats.getMax(), 0.001);
        assertEquals(175.0, stats.getAverage(), 0.001);
    }

    @Test
    void topFiveSortsBySoldThenStartTime() {
        List<Screening> result =
                ScreeningQueries.topBySoldAndTime(sampleScreenings(), 5);

        assertEquals(5, result.size());
        assertEquals("Interstellar", result.get(0).getFilm());
        assertEquals("Dune", result.get(1).getFilm());
        assertEquals("Avatar", result.get(2).getFilm());
        assertEquals("Oppenheimer", result.get(3).getFilm());
        assertEquals("Avatar", result.get(4).getFilm());
    }

    @Test
    void topNWithZeroReturnsEmptyList() {
        assertTrue(
                ScreeningQueries.topBySoldAndTime(sampleScreenings(), 0)
                        .isEmpty()
        );
    }

    @Test
    void topNGreaterThanSizeReturnsAll() {
        assertEquals(
                6,
                ScreeningQueries.topBySoldAndTime(sampleScreenings(), 100)
                        .size()
        );
    }

    @Test
    void topNRejectsNegativeN() {
        assertThrows(
                IllegalArgumentException.class,
                () -> ScreeningQueries.topBySoldAndTime(
                        sampleScreenings(), -1
                )
        );
    }

    @Test
    void findByFilmAndTimeReturnsMatchingScreening() {
        Optional<Screening> result =
                ScreeningQueries.findByFilmAndTime(
                        sampleScreenings(), "Avatar", TIME_10
                );

        assertTrue(result.isPresent());
        assertEquals(50, result.orElseThrow().getSold());
    }

    @Test
    void findByFilmAndTimeReturnsEmptyForMissingMatch() {
        assertTrue(
                ScreeningQueries.findByFilmAndTime(
                        sampleScreenings(), "Avatar", TIME_12
                ).isEmpty()
        );
    }

    @Test
    void findByFilmAndTimeReturnsFirstDuplicateMatch() {
        Screening first = new RegularScreening(
                "Test", 2, 100.0, 10, 90, TIME_10
        );
        Screening second = new RegularScreening(
                "Test", 3, 100.0, 20, 90, TIME_10
        );

        Optional<Screening> result =
                ScreeningQueries.findByFilmAndTime(
                        List.of(first, second), "Test", TIME_10
                );

        assertSame(first, result.orElseThrow());
    }

    @Test
    void queriesHandleEmptyCollections() {
        List<Screening> empty = List.of();

        assertTrue(ScreeningQueries.filterBySold(empty, 10).isEmpty());
        assertTrue(ScreeningQueries.filmNames(empty).isEmpty());
        assertTrue(ScreeningQueries.revenueByHall(empty).isEmpty());
        assertEquals(
                0,
                ScreeningQueries.ticketPriceStatistics(empty).getCount()
        );
        assertTrue(ScreeningQueries.topBySoldAndTime(empty, 5).isEmpty());
        assertTrue(
                ScreeningQueries.findByFilmAndTime(
                        empty, "Missing", TIME_10
                ).isEmpty()
        );
        assertTrue(ScreeningQueries.countByHallAndKind(empty).isEmpty());
    }

    @Test
    void queriesDoNotModifyOriginalCollection() {
        List<Screening> original =
                new ArrayList<>(sampleScreenings());
        List<Screening> snapshot =
                new ArrayList<>(original);

        ScreeningQueries.filterBySold(original, 50);
        ScreeningQueries.filmNames(original);
        ScreeningQueries.revenueByHall(original);
        ScreeningQueries.ticketPriceStatistics(original);
        ScreeningQueries.topBySoldAndTime(original, 5);
        ScreeningQueries.findByFilmAndTime(
                original, "Avatar", TIME_10
        );
        ScreeningQueries.countByHallAndKind(original);

        assertEquals(snapshot, original);
    }

    @Test
    void streamResultsMatchLoopBasedCalculation() {
        List<Screening> screenings = sampleScreenings();

        double loopRevenue = 0;
        double loopPriceSum = 0;

        for (Screening screening : screenings) {
            loopRevenue += screening.revenue();
            loopPriceSum += screening.getTicketPrice();
        }

        double streamRevenue =
                ScreeningQueries.revenueByHall(screenings)
                        .values()
                        .stream()
                        .mapToDouble(Double::doubleValue)
                        .sum();

        assertEquals(loopRevenue, streamRevenue, 0.001);
        assertEquals(
                loopPriceSum,
                ScreeningQueries.ticketPriceStatistics(
                        screenings
                ).getSum(),
                0.001
        );
    }

    @Test
    void twoLevelGroupingCountsScreenings() {
        Map<Integer, Map<ScreeningKind, Long>> result =
                ScreeningQueries.countByHallAndKind(
                        sampleScreenings()
                );

        assertEquals(2L, result.get(1).get(ScreeningKind.PREMIUM));
        assertEquals(2L, result.get(2).get(ScreeningKind.REGULAR));
        assertEquals(1L, result.get(3).get(ScreeningKind.REGULAR));
        assertEquals(1L, result.get(4).get(ScreeningKind.REGULAR));
    }

    @Test
    void rejectsNullCollection() {
        assertThrows(
                NullPointerException.class,
                () -> ScreeningQueries.filmNames(null)
        );
    }

    @Test
    void differentStartTimesMakeScreeningsUnequal() {
        Screening first = new RegularScreening(
                "Test", 2, 100.0, 10, 90, TIME_10
        );
        Screening second = new RegularScreening(
                "Test", 2, 100.0, 10, 90, TIME_12
        );

        assertNotEquals(first, second);
    }
}
