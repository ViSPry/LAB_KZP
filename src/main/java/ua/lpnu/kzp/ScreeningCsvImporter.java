package ua.lpnu.kzp;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Imports cinema screening records from CSV files.
 */
public final class ScreeningCsvImporter {

    private static final List<String> EXPECTED_HEADER =
            List.of("фільм", "зал", "продано", "час", "ціна");

    private ScreeningCsvImporter() {
    }

    public static List<ScreeningRecord> read(Path path) {
        Objects.requireNonNull(path, "Path must not be null");

        final List<List<String>> rows;

        try {
            String content = Files.readString(path, StandardCharsets.UTF_8);
            rows = CsvParser.parse(content);
        } catch (IOException exception) {
            throw new DataStorageException(
                    "Failed to import CSV from " + path, exception);
        }

        if (rows.isEmpty()) {
            throw new DataStorageException("CSV file is empty");
        }

        if (!EXPECTED_HEADER.equals(rows.get(0))) {
            throw new DataStorageException(
                    "Invalid CSV header. Expected: " + EXPECTED_HEADER);
        }

        List<ScreeningRecord> records = new ArrayList<>();

        for (int index = 1; index < rows.size(); index++) {
            List<String> values = rows.get(index);

            if (values.size() != EXPECTED_HEADER.size()) {
                throw new DataStorageException(
                        "Invalid column count at CSV row " + (index + 1));
            }

            try {
                String film = values.get(0);
                String hall = values.get(1);
                int soldTickets = Integer.parseInt(values.get(2));
                LocalDateTime startAt =
                        LocalDateTime.parse(values.get(3));
                double ticketPrice =
                        Double.parseDouble(values.get(4));

                records.add(new ScreeningRecord(
                        film,
                        hall,
                        startAt,
                        ticketPrice,
                        soldTickets
                ));
            } catch (IllegalArgumentException | DateTimeParseException exception) {
                throw new DataStorageException(
                        "Invalid screening data at CSV row "
                                + (index + 1),
                        exception);
            }
        }

        return List.copyOf(records);
    }
}