
package ua.lpnu.kzp;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.stream.IntStream;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Консольний застосунок для обробки CSV-записів кінотеатру.
 *
 * <p>Програма зчитує CSV-файл, створює об'єкти сеансів,
 * обчислює показники продажів і формує текстовий звіт.
 * Основні обчислення виконуються через Stream API.
 * Додатковий режим --streams показує розширені запити.</p>
 */
public class Main {

    private static final String VERSION = "4.0.0";

    private Main() {
    }

    /**
     * Точка входу до застосунку.
     *
     * @param args аргументи командного рядка
     */
    public static void main(String[] args) {
        Path inputPath = Path.of("data", "input.csv");
        Path outputPath = Path.of("out", "report.txt");

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--help":
                    printHelp();
                    return;

                case "--version":
                    System.out.println("lab01 version " + VERSION);
                    return;

                case "--streams":
                    printStreamDemonstration();
                    return;

                case "--input":
                    if (i + 1 >= args.length
                            || args[i + 1].startsWith("--")) {
                        System.err.println(
                                "Помилка: після --input потрібно вказати шлях."
                        );
                        return;
                    }
                    inputPath = Path.of(args[++i]);
                    break;

                case "--output":
                    if (i + 1 >= args.length
                            || args[i + 1].startsWith("--")) {
                        System.err.println(
                                "Помилка: після --output потрібно вказати шлях."
                        );
                        return;
                    }
                    outputPath = Path.of(args[++i]);
                    break;

                default:
                    System.err.println(
                            "Невідомий параметр: %s".formatted(args[i])
                    );
                    System.err.println(
                            "Використайте --help для перегляду довідки."
                    );
                    return;
            }
        }

        List<String> report = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(inputPath, StandardCharsets.UTF_8);

            // Проміжна операція не змінює зовнішнього стану.
            List<CsvParseResult> parsed = IntStream.range(0, lines.size())
                    .mapToObj(i -> parseCsvLine(i + 1, lines.get(i)))
                    .toList();

            // Побічна дія (друк) виконується лише в кінцевій операції.
            parsed.stream()
                    .map(Main::formatCsvResult)
                    .forEachOrdered(System.out::println);

            List<Screening> screenings = parsed.stream()
                    .filter(CsvParseResult::isValid)
                    .map(CsvParseResult::screening)
                    .toList();

            int invalidCount = Math.toIntExact(parsed.stream()
                    .filter(result -> !result.isValid())
                    .count());

            TicketSales ticketSales = calculateTicketSales(
                    screenings,
                    invalidCount
            );

            report.add("===== ЗВІТ КІНОТЕАТРУ =====");
            report.add(
                    "Правильних записів: %d"
                            .formatted(ticketSales.validCount())
            );
            report.add(
                    "Неправильних записів: %d"
                            .formatted(ticketSales.invalidCount())
            );

            report.add(String.format(
                    Locale.ROOT,
                    "Загальний виторг: %.2f грн",
                    ticketSales.totalRevenue()
            ));

            report.add(String.format(
                    Locale.ROOT,
                    "Середня ціна квитка: %.2f грн",
                    ticketSales.averageTicketPrice()
            ));

            report.add(
                    "Максимальна кількість проданих квитків: %d"
                            .formatted(ticketSales.maxSold())
            );

            System.out.println();

            report.stream().forEachOrdered(System.out::println);

            Path parent = outputPath.toAbsolutePath().getParent();

            if (parent != null) {
                Files.createDirectories(parent);
            }

            Files.write(
                    outputPath,
                    report,
                    StandardCharsets.UTF_8
            );

            System.out.println();
            System.out.println(
                    "Звіт збережено: %s"
                            .formatted(outputPath.toAbsolutePath())
            );

        } catch (IOException e) {
            System.err.println(
                    "Помилка роботи з файлом: %s"
                            .formatted(e.getMessage())
            );
        }
    }

    /**
     * Демонструє запити Stream API на сеансах
     * із явно заданим часом початку.
     *
     * <p>Демонстраційні дані не впливають на CSV-звіт.
     * Старий формат CSV не містить часу початку сеансу.</p>
     */
    private record CsvParseResult(int lineNumber, Screening screening, String errorMessage) {
        boolean isValid() {
            return screening != null;
        }
    }

    private static CsvParseResult parseCsvLine(int lineNumber, String line) {
        try {
            return new CsvParseResult(lineNumber, Screening.fromCsv(line), null);
        } catch (IllegalArgumentException e) {
            return new CsvParseResult(lineNumber, null, e.getMessage());
        }
    }

    private static String formatCsvResult(CsvParseResult result) {
        if (result.isValid()) {
            Screening screening = result.screening();
            return "Рядок %d: OK — %s (%s)".formatted(
                    result.lineNumber(), screening.getFilm(), describeScreening(screening));
        }
        return "Рядок %d: ПОМИЛКА — %s".formatted(
                result.lineNumber(), result.errorMessage());
    }

    private static void printStreamDemonstration() {
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
    private static String describeScreening(Screening screening) {
        return switch (screening) {
            case RegularScreening regular ->
                    regular.getKind().getLabel();

            case PremiumScreening premium ->
                    String.format(
                            Locale.ROOT,
                            "%s, доплата %.2f грн",
                            premium.getKind().getLabel(),
                            premium.getPremiumFee()
                    );
        };
    }

    /**
     * Обчислює статистику продажів для коректних сеансів.
     *
     * <p>Виторг обчислюється поліморфно через
     * {@link Screening#revenue()}.</p>
     *
     * @param screenings коректні записи про сеанси
     * @param invalidCount кількість некоректних CSV-записів
     * @return незмінний підсумок продажів
     */
    private static TicketSales calculateTicketSales(
            List<Screening> screenings,
            int invalidCount
    ) {
        double totalRevenue = screenings.stream()
                .mapToDouble(Screening::revenue)
                .sum();

        double averageTicketPrice = screenings.stream()
                .mapToDouble(Screening::getTicketPrice)
                .average()
                .orElse(0.0);

        int maxSold = screenings.stream()
                .mapToInt(Screening::getSold)
                .max()
                .orElse(0);

        int validCount = screenings.size();

        return new TicketSales(
                validCount,
                invalidCount,
                totalRevenue,
                averageTicketPrice,
                maxSold
        );
    }

    /**
     * Виводить довідку щодо запуску програми.
     */
    private static void printHelp() {
        System.out.println(
                "Використання: java -jar lab01-4.0.0.jar [параметри]"
        );
        System.out.println();
        System.out.println("Параметри:");
        System.out.println("  --help           Показати довідку");
        System.out.println("  --version        Показати версію програми");
        System.out.println("  --streams        Демонстрація Stream API");
        System.out.println("  --input ШЛЯХ     Вказати вхідний CSV-файл");
        System.out.println(
                "  --output ШЛЯХ    Вказати шлях для збереження звіту"
        );
        System.out.println();
        System.out.println(
                "Стандартний вхідний файл: data/input.csv"
        );
        System.out.println(
                "Стандартний вихідний файл: out/report.txt"
        );
    }
}
