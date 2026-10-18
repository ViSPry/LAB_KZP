package ua.lpnu.kzp;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ScreeningCsvImporterTest {

    @TempDir
    Path tempDir;

    private static final String HEADER =
            "фільм,зал,продано,час,ціна\n";

    @Test
    void shouldRoundTripFiveRecordsWithoutDataLoss() {
        Path file = tempDir.resolve("screenings.csv");

        List<ScreeningRecord> original = List.of(
                new ScreeningRecord("Дюна", "Зал 1",
                        LocalDateTime.of(2026, 10, 18, 19, 30), 180.50, 75),
                new ScreeningRecord("Аватар", "Зал 2",
                        LocalDateTime.of(2026, 10, 19, 20, 0), 220.00, 100),
                new ScreeningRecord("Фільм, частина 2", "VIP",
                        LocalDateTime.of(2026, 10, 20, 18, 15), 300.25, 45),
                new ScreeningRecord("Назва \"Особлива\"", "Зал 3",
                        LocalDateTime.of(2026, 10, 21, 16, 0), 150.00, 30),
                new ScreeningRecord("Перший рядок\nДругий рядок", "Зал 4",
                        LocalDateTime.of(2026, 10, 22, 21, 45), 0.00, 0)
        );

        CsvExporter.write(file, ScreeningRecord.class, original);

        List<ScreeningRecord> restored = ScreeningCsvImporter.read(file);

        assertEquals(original, restored);
    }

    @Test
    void shouldImportHeaderOnlyAsEmptyList() throws IOException {
        Path file = writeCsv("empty.csv", HEADER);

        assertTrue(ScreeningCsvImporter.read(file).isEmpty());
    }

    @Test
    void shouldRejectIncorrectHeader() throws IOException {
        Path file = writeCsv("wrong-header.csv",
                "film,hall,soldTickets,startAt,ticketPrice\n");

        assertThrows(DataStorageException.class,
                () -> ScreeningCsvImporter.read(file));
    }

    @Test
    void shouldRejectIncorrectColumnCount() throws IOException {
        Path file = writeCsv("columns.csv",
                HEADER + "Дюна,Зал 1,75\n");

        assertThrows(DataStorageException.class,
                () -> ScreeningCsvImporter.read(file));
    }

    @Test
    void shouldRejectInvalidNumber() throws IOException {
        Path file = writeCsv("number.csv",
                HEADER + "Дюна,Зал 1,abc,2026-10-18T19:30,180.5\n");

        DataStorageException exception = assertThrows(
                DataStorageException.class,
                () -> ScreeningCsvImporter.read(file));

        assertNotNull(exception.getCause());
    }

    @Test
    void shouldRejectInvalidDate() throws IOException {
        Path file = writeCsv("date.csv",
                HEADER + "Дюна,Зал 1,75,invalid-date,180.5\n");

        assertThrows(DataStorageException.class,
                () -> ScreeningCsvImporter.read(file));
    }

    @Test
    void shouldRejectInvalidBusinessValues() throws IOException {
        Path file = writeCsv("invalid-values.csv",
                HEADER + "Дюна,Зал 1,-5,2026-10-18T19:30,180.5\n");

        assertThrows(DataStorageException.class,
                () -> ScreeningCsvImporter.read(file));
    }

    @Test
    void shouldPreserveIOExceptionCause() {
        Path missing = tempDir.resolve("missing.csv");

        DataStorageException exception = assertThrows(
                DataStorageException.class,
                () -> ScreeningCsvImporter.read(missing));

        assertInstanceOf(IOException.class, exception.getCause());
    }

    @Test
    void shouldRejectEmptyFile() throws IOException {
        Path file = writeCsv("blank.csv", "");

        assertThrows(DataStorageException.class,
                () -> ScreeningCsvImporter.read(file));
    }

    private Path writeCsv(String filename, String content)
            throws IOException {
        Path file = tempDir.resolve(filename);
        Files.writeString(file, content, StandardCharsets.UTF_8);
        return file;
    }
}