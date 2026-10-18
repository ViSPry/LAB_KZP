package ua.lpnu.kzp;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InheritedAnnotatedFieldsTest {

    @TempDir
    Path tempDir;

    static class ParentRecord {
        @CsvColumn("батьківське поле")
        private final String parentValue = "parent";
    }

    static class ChildRecord extends ParentRecord {
        @CsvColumn("дочірнє поле")
        private final String childValue = "child";
    }

    static class ShadowParent {
        @CsvColumn("старе значення")
        private final String value = "old";
    }

    static class ShadowChild extends ShadowParent {
        @CsvColumn("нове значення")
        private final String value = "new";
    }

    @Test
    void shouldDiscoverInheritedAnnotatedFields() {
        var fields = ReflectionFieldCache.fieldsFor(ChildRecord.class);

        assertEquals(2, fields.size());
        assertEquals(
                List.of("childValue", "parentValue"),
                fields.stream().map(f -> f.getName()).toList()
        );
    }

    @Test
    void shouldPreferChildFieldWhenNamesMatch() {
        var fields = ReflectionFieldCache.fieldsFor(ShadowChild.class);

        assertEquals(1, fields.size());
        assertEquals(ShadowChild.class,
                fields.get(0).getDeclaringClass());
    }

    @Test
    void shouldExportInheritedFieldsToCsv() throws Exception {
        Path file = tempDir.resolve("inherited.csv");

        CsvExporter.write(
                file, ChildRecord.class, List.of(new ChildRecord()));

        String csv = Files.readString(file, StandardCharsets.UTF_8);

        assertTrue(csv.contains("дочірнє поле,батьківське поле"));
        assertTrue(csv.contains("child,parent"));
    }

    @Test
    void shouldExportInheritedFieldsToJson() throws Exception {
        Path file = tempDir.resolve("inherited.json");

        new JsonExporter<>(ChildRecord.class)
                .export(file, List.of(new ChildRecord()));

        String json = Files.readString(file, StandardCharsets.UTF_8);

        assertTrue(json.contains("\"дочірнє поле\""));
        assertTrue(json.contains("\"батьківське поле\""));
        assertTrue(json.contains("\"child\""));
        assertTrue(json.contains("\"parent\""));
    }

    @Test
    void shouldNotDuplicateShadowedCsvColumns() throws Exception {
        Path file = tempDir.resolve("shadow.csv");

        CsvExporter.write(
                file, ShadowChild.class, List.of(new ShadowChild()));

        String csv = Files.readString(file, StandardCharsets.UTF_8);

        assertEquals("нове значення", csv.lines().findFirst().orElse(""));
        assertTrue(csv.contains("new"));
        assertFalse(csv.contains("старе значення"));
    }

    @Test
    void shouldNotDuplicateShadowedJsonProperties() throws Exception {
        Path file = tempDir.resolve("shadow.json");

        new JsonExporter<>(ShadowChild.class)
                .export(file, List.of(new ShadowChild()));

        String json = Files.readString(file, StandardCharsets.UTF_8);

        assertTrue(json.contains("\"нове значення\""));
        assertFalse(json.contains("\"старе значення\""));
        assertTrue(json.contains("\"new\""));
    }
}