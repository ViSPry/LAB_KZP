
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
 * <p>Програма зчитує вхідний файл, перевіряє коректність записів,
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
     * <p>Обробляє аргументи командного рядка, зчитує CSV-файл,
     * перевіряє записи, обчислює статистику та записує звіт.</p>
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
                    System.out.println("lab01 version 1.0.0");
                    return;

                case "--input":
                    if (i + 1 >= args.length || args[i + 1].startsWith("--")) {
                        System.err.println("Помилка: після --input потрібно вказати шлях.");
                        return;
                    }
                    inputPath = Path.of(args[++i]);
                    break;

                case "--output":
                    if (i + 1 >= args.length || args[i + 1].startsWith("--")) {
                        System.err.println("Помилка: після --output потрібно вказати шлях.");
                        return;
                    }
                    outputPath = Path.of(args[++i]);
                    break;

                default:
                    System.err.println("Невідомий параметр: " + args[i]);
                    System.err.println("Використайте --help для перегляду довідки.");
                    return;
            }
        }

        List<String> report = new ArrayList<>();

        int validCount = 0;
        int invalidCount = 0;
        double totalRevenue = 0;
        double totalTicketPrice = 0;
        int maxSold = 0;

        try {
            List<String> lines = Files.readAllLines(inputPath, StandardCharsets.UTF_8);

            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                int lineNumber = i + 1;

                try {
                    String[] fields = line.split(";", -1);

                    if (fields.length != 5) {
                        throw new IllegalArgumentException("має бути рівно 5 полів");
                    }

                    String film = fields[0].trim();
                    int hall = Integer.parseInt(fields[1].trim());
                    double ticketPrice = Double.parseDouble(fields[2].trim());
                    int sold = Integer.parseInt(fields[3].trim());
                    int durationMin = Integer.parseInt(fields[4].trim());

                    if (film.isEmpty()) {
                        throw new IllegalArgumentException("назва фільму порожня");
                    }

                    if (hall <= 0) {
                        throw new IllegalArgumentException("номер залу має бути додатним");
                    }

                    if (!Double.isFinite(ticketPrice) || ticketPrice < 0) {
                        throw new IllegalArgumentException("ціна квитка має бути невід'ємною");
                    }

                    if (sold < 0) {
                        throw new IllegalArgumentException(
                                "кількість проданих квитків не може бути від'ємною"
                        );
                    }

                    if (durationMin <= 0) {
                        throw new IllegalArgumentException("тривалість фільму має бути додатною");
                    }

                    validCount++;
                    totalRevenue += ticketPrice * sold;
                    totalTicketPrice += ticketPrice;
                    maxSold = Math.max(maxSold, sold);

                    System.out.println("Рядок " + lineNumber + ": OK — " + film);

                } catch (NumberFormatException e) {
                    invalidCount++;
                    System.out.println(
                            "Рядок " + lineNumber + ": ПОМИЛКА — неправильний числовий формат"
                    );

                } catch (IllegalArgumentException e) {
                    invalidCount++;
                    System.out.println(
                            "Рядок " + lineNumber + ": ПОМИЛКА — " + e.getMessage()
                    );
                }
            }

            double averageTicketPrice = validCount > 0
                    ? totalTicketPrice / validCount
                    : 0;

            report.add("===== ЗВІТ КІНОТЕАТРУ =====");
            report.add("Правильних записів: " + validCount);
            report.add("Неправильних записів: " + invalidCount);

            report.add(String.format(
                    Locale.ROOT,
                    "Загальний виторг: %.2f грн",
                    totalRevenue
            ));

            report.add(String.format(
                    Locale.ROOT,
                    "Середня ціна квитка: %.2f грн",
                    averageTicketPrice
            ));

            report.add("Максимальна кількість проданих квитків: " + maxSold);

            System.out.println();

            for (String reportLine : report) {
                System.out.println(reportLine);
            }

            Path parent = outputPath.toAbsolutePath().getParent();

            if (parent != null) {
                Files.createDirectories(parent);
            }

            Files.write(outputPath, report, StandardCharsets.UTF_8);

            System.out.println();
            System.out.println("Звіт збережено: " + outputPath.toAbsolutePath());

        } catch (IOException e) {
            System.err.println("Помилка роботи з файлом: " + e.getMessage());
        }
    }

    /**
     * Виводить довідку щодо запуску програми, доступних параметрів
     * командного рядка та стандартних шляхів до файлів.
     */
    private static void printHelp() {
        System.out.println("Використання: java -jar lab01-1.0.0.jar [параметри]");
        System.out.println();
        System.out.println("Параметри:");
        System.out.println("  --help           Показати довідку");
        System.out.println("  --version        Показати версію програми");
        System.out.println("  --input ШЛЯХ     Вказати вхідний CSV-файл");
        System.out.println("  --output ШЛЯХ    Вказати шлях для збереження звіту");
        System.out.println();
        System.out.println("Стандартний вхідний файл: data/input.csv");
        System.out.println("Стандартний вихідний файл: out/report.txt");
    }
}
