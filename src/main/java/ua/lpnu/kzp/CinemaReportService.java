package ua.lpnu.kzp;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;

/**
 * Сервіс формування звіту кінотеатру на основі CSV.
 */
public final class CinemaReportService {

    public void generate(Path inputPath, Path outputPath) {
        List<String> report = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(inputPath, StandardCharsets.UTF_8);

            // Проміжна операція не змінює зовнішнього стану.
            List<CinemaCsvParser.CsvParseResult> parsed = IntStream.range(0, lines.size())
                    .mapToObj(i -> CinemaCsvParser.parseCsvLine(i + 1, lines.get(i)))
                    .toList();

            // Побічна дія (друк) виконується лише в кінцевій операції.
            parsed.stream()
                    .map(CinemaCsvParser::formatCsvResult)
                    .forEachOrdered(System.out::println);

            List<Screening> screenings = parsed.stream()
                    .filter(CinemaCsvParser.CsvParseResult::isValid)
                    .map(CinemaCsvParser.CsvParseResult::screening)
                    .toList();

            int invalidCount = Math.toIntExact(parsed.stream()
                    .filter(result -> !result.isValid())
                    .count());

            TicketSales ticketSales = TicketSalesService.calculateTicketSales(
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
}