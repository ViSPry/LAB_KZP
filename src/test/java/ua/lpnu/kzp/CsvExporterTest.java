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

class CsvExporterTest {

    @TempDir
    Path tempDir;

    private static final LocalDateTime START =
            LocalDateTime.of(2026, 10, 18, 19, 30);

    @Test
    void shouldExportCorrectHeadersAndValues() throws IOException {
        Path file = tempDir.resolve("screenings.csv");

        ScreeningRecord record = new ScreeningRecord(
                "Dune", "Hall 1", START, 180.50, 75);

        CsvExporter.write(file, ScreeningRecord.class, List.of(record));

        List<String> lines = Files.readAllLines(
                file, StandardCharsets.UTF_8);

        assertEquals("фільм,зал,продано,час,ціна", lines.get(0));
        assertEquals(
                "Dune,Hall 1,75,2026-10-18T19:30,180.5",
                lines.get(1));
    }

    @Test
    void shouldExportHeaderForEmptyCollection() throws IOException {
        Path file = tempDir.resolve("empty.csv");

        CsvExporter.write(
                file, ScreeningRecord.class, List.of());

        assertEquals(
                "фільм,зал,продано,час,ціна",
                Files.readAllLines(file, StandardCharsets.UTF_8).get(0));
    }

    @Test
    void shouldEscapeCommasAndQuotes() throws IOException {
        Path file = tempDir.resolve("special.csv");

        ScreeningRecord record = new ScreeningRecord(
                "Film, \"Special\"",
                "Hall 1",
                START,
                100,
                10);

        CsvExporter.write(
                file, ScreeningRecord.class, List.of(record));

        String content = Files.readString(file, StandardCharsets.UTF_8);

        assertTrue(content.contains(
                "\"Film, \"\"Special\"\"\""));
    }

    @Test
    void shouldEscapeEmbeddedNewlines() throws IOException {
        Path file = tempDir.resolve("multiline.csv");

        ScreeningRecord record = new ScreeningRecord(
                "First line\nSecond line",
                "Hall 1",
                START,
                100,
                10);

        CsvExporter.write(
                file, ScreeningRecord.class, List.of(record));

        String content = Files.readString(file, StandardCharsets.UTF_8);

        assertTrue(content.contains(
                "\"First line\nSecond line\""));
    }

    @Test
    void shouldCreateParentDirectories() {
        Path file = tempDir.resolve("nested/data/screenings.csv");

        CsvExporter.write(
                file, ScreeningRecord.class, List.of());

        assertTrue(Files.exists(file));
    }

    @Test
    void shouldWorkWithUnrelatedAnnotatedClass() throws IOException {
        Path file = tempDir.resolve("other.csv");

        CsvExporter.write(
                file,
                OtherRecord.class,
                List.of(new OtherRecord("Test", 42)));

        List<String> lines = Files.readAllLines(
                file, StandardCharsets.UTF_8);

        assertEquals("назва,кількість", lines.get(0));
        assertEquals("Test,42", lines.get(1));
    }

    @Test
    void shouldRejectClassWithoutCsvAnnotations() {
        Path file = tempDir.resolve("invalid.csv");

        assertThrows(DataStorageException.class,
                () -> CsvExporter.write(
                        file, String.class, List.of("Test")));
    }

    private static class OtherRecord {

        @CsvColumn("назва")
        private final String name;

        @CsvColumn("кількість")
        private final int quantity;

        OtherRecord(String name, int quantity) {
            this.name = name;
            this.quantity = quantity;
        }
    }
}