package ua.lpnu.kzp;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CsvExporterErrorTest {

    @TempDir
    Path tempDir;

    @Test
    void shouldPreserveOriginalCauseWhenExportFails() throws IOException {
        Path directory = tempDir.resolve("destination");
        Files.createDirectory(directory);

        ScreeningRecord record = new ScreeningRecord(
                "Dune",
                "Hall 1",
                LocalDateTime.of(2026, 10, 18, 19, 30),
                180.50,
                75
        );

        DataStorageException exception = assertThrows(
                DataStorageException.class,
                () -> CsvExporter.write(
                        directory,
                        ScreeningRecord.class,
                        List.of(record)
                )
        );

        assertNotNull(exception.getCause());
        assertInstanceOf(IOException.class, exception.getCause());
        assertTrue(exception.getMessage().contains("Failed to export CSV"));
    }

    @Test
    void shouldRejectNullRecords() {
        Path file = tempDir.resolve("null.csv");

        assertThrows(
                NullPointerException.class,
                () -> CsvExporter.write(
                        file,
                        ScreeningRecord.class,
                        null
                )
        );
    }

    @Test
    void shouldPreserveCauseInCustomException() {
        IOException cause = new IOException("Disk failure");

        DataStorageException exception =
                new DataStorageException("Storage error", cause);

        assertSame(cause, exception.getCause());
        assertEquals("Storage error", exception.getMessage());
    }
}