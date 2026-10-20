package ua.lpnu.kzp;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class StorageServiceTest {

    @TempDir
    Path tempDir;

    @Test
    void importsSearchesAndExportsScreenings() throws Exception {
        Path input = tempDir.resolve("input.csv");

        Files.writeString(input,
                "фільм,зал,продано,час,ціна\n"
                + "Avatar,2,100,2026-10-20T18:00:00,150.0\n"
                + "Dune,1,80,2026-10-20T20:00:00,200.0\n",
                StandardCharsets.UTF_8);

        StorageService service = new StorageService();

        assertEquals(2, service.importCsv(input));
        assertEquals(2, service.all().size());
        assertEquals(1, service.findByFilm("avatar").size());
        assertEquals("Avatar", service.findByFilm("avatar").get(0).film());
        assertTrue(service.findByFilm("Unknown").isEmpty());

        Path csv = tempDir.resolve("export.csv");
        Path json = tempDir.resolve("export.json");

        service.exportCsv(csv);
        service.exportJson(json);

        assertTrue(Files.exists(csv));
        assertTrue(Files.exists(json));

        String jsonContent = Files.readString(json, StandardCharsets.UTF_8);
        assertTrue(jsonContent.contains("Avatar"));
        assertTrue(jsonContent.contains("Dune"));

        assertEquals(2, ScreeningCsvImporter.read(csv).size());
    }
}
