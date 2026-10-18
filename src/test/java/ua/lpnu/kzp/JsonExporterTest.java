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

class JsonExporterTest {

    @TempDir
    Path tempDir;

    private ScreeningRecord record(String film) {
        return new ScreeningRecord(
                film,
                "Зал 1",
                LocalDateTime.of(2026, 10, 18, 19, 30),
                180.50,
                75
        );
    }

    @Test
    void shouldExportScreeningRecordAsJson() throws IOException {
        Path file = tempDir.resolve("screenings.json");

        Exporter<ScreeningRecord> exporter =
                new JsonExporter<>(ScreeningRecord.class);

        exporter.export(file, List.of(record("Дюна")));

        String json = Files.readString(file, StandardCharsets.UTF_8);

        assertTrue(json.startsWith("["));
        assertTrue(json.stripTrailing().endsWith("]"));
        assertTrue(json.contains("\"фільм\": \"Дюна\""));
        assertTrue(json.contains("\"зал\": \"Зал 1\""));
        assertTrue(json.contains("\"продано\": 75"));
        assertTrue(json.contains("\"ціна\": 180.5"));
        assertTrue(json.contains("\"час\": \"2026-10-18T19:30\""));
    }

    @Test
    void shouldEscapeQuotesAndBackslashes() throws IOException {
        Path file = tempDir.resolve("escaped.json");

        new JsonExporter<>(ScreeningRecord.class)
                .export(file, List.of(record("Фільм \"Тест\" \\ Частина 2")));

        String json = Files.readString(file, StandardCharsets.UTF_8);

        assertTrue(json.contains("\\\"Тест\\\""));
        assertTrue(json.contains("\\\\ Частина 2"));
    }

    @Test
    void shouldEscapeNewlinesAndControlCharacters() throws IOException {
        Path file = tempDir.resolve("newlines.json");

        new JsonExporter<>(ScreeningRecord.class)
                .export(file, List.of(record("Рядок 1\nРядок 2\tТест")));

        String json = Files.readString(file, StandardCharsets.UTF_8);

        assertTrue(json.contains("\\n"));
        assertTrue(json.contains("\\t"));
        assertFalse(json.contains("Рядок 1\nРядок 2"));
    }

    @Test
    void shouldExportEmptyListAsJsonArray() throws IOException {
        Path file = tempDir.resolve("empty.json");

        new JsonExporter<>(ScreeningRecord.class)
                .export(file, List.of());

        assertEquals("[]", Files.readString(file, StandardCharsets.UTF_8).trim());
    }

    @Test
    void shouldCreateParentDirectories() {
        Path file = tempDir.resolve("nested/data/screenings.json");

        new JsonExporter<>(ScreeningRecord.class)
                .export(file, List.of(record("Дюна")));

        assertTrue(Files.exists(file));
    }

    @Test
    void shouldRejectNullRecords() {
        Path file = tempDir.resolve("null.json");

        assertThrows(NullPointerException.class,
                () -> new JsonExporter<>(ScreeningRecord.class)
                        .export(file, null));
    }

    @Test
    void shouldRejectClassWithoutAnnotatedFields() {
        Path file = tempDir.resolve("invalid.json");

        assertThrows(DataStorageException.class,
                () -> new JsonExporter<>(String.class)
                        .export(file, List.of("test")));
    }

    @Test
    void shouldPreserveIOExceptionCause() throws IOException {
        Path directory = tempDir.resolve("destination");
        Files.createDirectory(directory);

        DataStorageException exception = assertThrows(
                DataStorageException.class,
                () -> new JsonExporter<>(ScreeningRecord.class)
                        .export(directory, List.of(record("Дюна"))));

        assertInstanceOf(IOException.class, exception.getCause());
    }

    @Test
    void shouldWorkThroughCommonExporterInterface() throws IOException {
        Path csvFile = tempDir.resolve("data.csv");
        Path jsonFile = tempDir.resolve("data.json");

        List<ScreeningRecord> records = List.of(record("Дюна"));

        Exporter<ScreeningRecord> csv =
                CsvExporter.forType(ScreeningRecord.class);

        Exporter<ScreeningRecord> json =
                new JsonExporter<>(ScreeningRecord.class);

        csv.export(csvFile, records);
        json.export(jsonFile, records);

        assertTrue(Files.exists(csvFile));
        assertTrue(Files.exists(jsonFile));
        assertEquals(records, ScreeningCsvImporter.read(csvFile));
    }
}