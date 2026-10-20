package ua.lpnu.kzp;

import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.IntStream;

public final class StreamDemoService {

    private StreamDemoService() {
    }

    public static void run() {
        List<Screening> screenings = demonstrationScreenings();

        System.out.println("===== STREAM API: КІНОТЕАТР =====");
        System.out.println();

        int threshold = 100;

        System.out.println(
                "1. Сеанси, де продано понад %d квитків:"
                        .formatted(threshold)
        );

        ScreeningQueries.filterBySold(screenings, threshold)
                .forEach(screening ->
                        System.out.println(
                                "   %s — %d квитків"
                                        .formatted(
                                                screening.getFilm(),
                                                screening.getSold()
                                        )
                        )
                );

        System.out.println();
        System.out.println("2. Назви фільмів:");

        ScreeningQueries.filmNames(screenings)
                .forEach(film ->
                        System.out.println("   " + film)
                );

        System.out.println();
        System.out.println("3. Виторг за залами:");

        Map<Integer, Double> revenueByHall =
                ScreeningQueries.revenueByHall(screenings);

        revenueByHall.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry ->
                        System.out.println(String.format(
                                Locale.ROOT,
                                "   Зал %d: %.2f грн",
                                entry.getKey(),
                                entry.getValue()
                        ))
                );

        System.out.println();
        System.out.println("4. Статистика ціни квитка:");

        var statistics =
                ScreeningQueries.ticketPriceStatistics(screenings);

        System.out.println(
                "   Кількість: %d".formatted(statistics.getCount())
        );

        System.out.println(String.format(
                Locale.ROOT,
                "   Мінімум: %.2f грн",
                statistics.getMin()
        ));

        System.out.println(String.format(
                Locale.ROOT,
                "   Максимум: %.2f грн",
                statistics.getMax()
        ));

        System.out.println(String.format(
                Locale.ROOT,
                "   Середнє: %.2f грн",
                statistics.getAverage()
        ));

        System.out.println();
        System.out.println(
                "5. Top-5 за кількістю продажів і часом:"
        );

        List<Screening> top =
                ScreeningQueries.topBySoldAndTime(screenings, 5);

        IntStream.range(0, top.size()).forEachOrdered(i -> {
            Screening screening = top.get(i);
            System.out.println(
                    "   %d. %s — %d квитків, %s".formatted(
                            i + 1,
                            screening.getFilm(),
                            screening.getSold(),
                            screening.getStartTime()
                    )
            );
        });

        System.out.println();
        System.out.println(
                "6. Пошук фільму Avatar о 10:00:"
        );

        ScreeningQueries.findByFilmAndTime(
                screenings,
                "Avatar",
                LocalTime.of(10, 0)
        ).ifPresentOrElse(
                screening -> System.out.println(
                        "   Знайдено: %s, зал %d, продано %d"
                                .formatted(
                                        screening.getFilm(),
                                        screening.getHall(),
                                        screening.getSold()
                                )
                ),
                () -> System.out.println("   Сеанс не знайдено")
        );

        System.out.println();
        System.out.println(
                "7. Дворівневе групування: зал і тип сеансу:"
        );

        Map<Integer, Map<ScreeningKind, Long>> grouped =
                ScreeningQueries.countByHallAndKind(screenings);

        grouped.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(hallEntry ->
                        hallEntry.getValue()
                                .entrySet()
                                .stream()
                                .sorted(Comparator.comparing(
                                        entry -> entry.getKey().name()
                                ))
                                .forEach(kindEntry ->
                                        System.out.println(
                                                "   Зал %d, %s: %d"
                                                        .formatted(
                                                                hallEntry.getKey(),
                                                                kindEntry.getKey()
                                                                        .getLabel(),
                                                                kindEntry.getValue()
                                                        )
                                        )
                                )
                );
    }

    /**
     * Створює демонстраційні дані для Stream API.
     *
     * @return шість сеансів із різними параметрами
     */
    private static List<Screening> demonstrationScreenings() {
        return List.of(
                new RegularScreening(
                        "Avatar", 2, 150.0, 100, 180,
                        LocalTime.of(18, 0)
                ),
                new PremiumScreening(
                        "Dune", 1, 200.0, 150, 160, 20.0,
                        LocalTime.of(20, 0)
                ),
                new RegularScreening(
                        "Interstellar", 3, 180.0, 150, 169,
                        LocalTime.of(12, 0)
                ),
                new RegularScreening(
                        "Avatar", 2, 150.0, 50, 180,
                        LocalTime.of(10, 0)
                ),
                new PremiumScreening(
                        "Oppenheimer", 1, 250.0, 80, 180, 30.0,
                        LocalTime.of(16, 0)
                ),
                new RegularScreening(
                        "Matrix", 4, 120.0, 0, 136,
                        LocalTime.of(14, 0)
                )
        );
    }

    /**
     * Повертає текстовий опис конкретного підтипу сеансу.
     *
     * @param screening сеанс для опису
     * @return текстовий опис типу сеансу
     */

}