package ua.lpnu.kzp;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Консольний застосунок для обробки CSV-записів кінотеатру.
 *
 * <p>Програма зчитує вхідний файл, створює об'єкти {@link Screening},
 * обчислює статистичні показники та формує текстовий звіт
 * у кодуванні UTF-8.</p>
 */
public class Main {

    /**
     * Забороняє створення екземплярів службового класу.
     */
    private Main() {
    }

    /**
     * Точка входу до застосунку.
     *
     * @param args аргументи командного рядка:
     *             --help, --version, --input та --output
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
                    System.out.println("lab01 version 2.0.0");
                    return;

                case "--input":
                    if (i + 1 >= args.length || args[i + 1].startsWith("--")) {
                        System.err.println(
                                "Помилка: після --input потрібно вказати шлях."
                        );
                        return;
                    }
                    inputPath = Path.of(args[++i]);
                    break;

                case "--output":
                    if (i + 1 >= args.length || args[i + 1].startsWith("--")) {
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
        List<Screening> screenings = new ArrayList<>();

        int invalidCount = 0;

        try {
            List<String> lines = Files.readAllLines(
                    inputPath,
                    StandardCharsets.UTF_8
            );

            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                int lineNumber = i + 1;

                try {
                    Screening screening = Screening.fromCsv(line);
                    screenings.add(screening);

                    System.out.println(
                            "Рядок %d: OK — %s (%s)"
                                    .formatted(
                                            lineNumber,
                                            screening.getFilm(),
                                            describeScreening(screening)
                                    )
                    );

                } catch (IllegalArgumentException e) {
                    invalidCount++;

                    System.out.println(
                            "Рядок %d: ПОМИЛКА — %s"
                                    .formatted(lineNumber, e.getMessage())
                    );
                }
            }

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

            for (String reportLine : report) {
                System.out.println(reportLine);
            }

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
     * Повертає текстовий опис конкретного підтипу сеансу.
     *
     * <p>Метод демонструє pattern matching для {@code switch}
     * над sealed-ієрархією {@link Screening}. Оскільки базовий клас
     * дозволяє лише {@link RegularScreening} і
     * {@link PremiumScreening}, компілятор може перевірити
     * вичерпність цього {@code switch}.</p>
     *
     * <p>Цей метод не обчислює виторг. Поліморфна бізнес-логіка
     * залишається в перевизначених методах {@link Screening#revenue()}.</p>
     *
     * @param screening сеанс для опису
     * @return текстовий опис типу сеансу
     */
    private static String describeScreening(Screening screening) {
        return switch (screening) {
            case RegularScreening regular ->
                    regular.getKind().getLabel();

            case PremiumScreening premium ->
                    "%s, доплата %.2f грн".formatted(
                            premium.getKind().getLabel(),
                            premium.getPremiumFee()
                    );
        };
    }

    /**
     * Обчислює статистику продажів для коректних сеансів.
     *
     * <p>Виторг обчислюється поліморфно через
     * {@link Screening#revenue()}. Метод не перевіряє конкретний
     * підтип сеансу через {@code if} або {@code switch}.</p>
     *
     * @param screenings коректні записи про сеанси різних підтипів
     * @param invalidCount кількість некоректних CSV-записів
     * @return незмінний підсумок продажів
     */
    private static TicketSales calculateTicketSales(
            List<Screening> screenings,
            int invalidCount
    ) {
        double totalRevenue = 0;
        double totalTicketPrice = 0;
        int maxSold = 0;

        for (Screening screening : screenings) {
            totalRevenue += screening.revenue();
            totalTicketPrice += screening.getTicketPrice();
            maxSold = Math.max(maxSold, screening.getSold());
        }

        int validCount = screenings.size();

        double averageTicketPrice = validCount > 0
                ? totalTicketPrice / validCount
                : 0;

        return new TicketSales(
                validCount,
                invalidCount,
                totalRevenue,
                averageTicketPrice,
                maxSold
        );
    }

    /**
     * Виводить довідку щодо запуску програми, доступних параметрів
     * командного рядка та стандартних шляхів до файлів.
     */
    private static void printHelp() {
        System.out.println(
                "Використання: java -jar lab01-2.0.0.jar [параметри]"
        );
        System.out.println();
        System.out.println("Параметри:");
        System.out.println("  --help           Показати довідку");
        System.out.println("  --version        Показати версію програми");
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