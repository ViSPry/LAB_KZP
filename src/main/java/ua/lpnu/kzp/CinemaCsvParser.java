package ua.lpnu.kzp;

import java.util.Locale;

public final class CinemaCsvParser {

    private CinemaCsvParser() {
    }

    public record CsvParseResult(int lineNumber, Screening screening, String errorMessage) {
        boolean isValid() {
            return screening != null;
        }
    }

    public static CsvParseResult parseCsvLine(int lineNumber, String line) {
        try {
            return new CsvParseResult(lineNumber, Screening.fromCsv(line), null);
        } catch (IllegalArgumentException e) {
            return new CsvParseResult(lineNumber, null, e.getMessage());
        }
    }

    public static String formatCsvResult(CsvParseResult result) {
        if (result.isValid()) {
            Screening screening = result.screening();
            return "Рядок %d: OK — %s (%s)".formatted(
                    result.lineNumber(), screening.getFilm(), CinemaCsvParser.describeScreening(screening));
        }
        return "Рядок %d: ПОМИЛКА — %s".formatted(
                result.lineNumber(), result.errorMessage());
    }

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

}