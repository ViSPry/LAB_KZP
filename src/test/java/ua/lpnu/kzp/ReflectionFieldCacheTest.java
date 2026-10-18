package ua.lpnu.kzp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReflectionFieldCacheTest {

    @BeforeEach
    void clearCache() {
        ReflectionFieldCache.clear();
    }

    @Test
    void shouldFindAnnotatedFields() {
        List<Field> fields =
                ReflectionFieldCache.fieldsFor(ScreeningRecord.class);

        assertEquals(5, fields.size());
        assertEquals(
                List.of("film", "hall", "soldTickets",
                        "startAt", "ticketPrice"),
                fields.stream().map(Field::getName).toList()
        );
    }

    @Test
    void shouldReuseCachedMetadata() {
        List<Field> first =
                ReflectionFieldCache.fieldsFor(ScreeningRecord.class);

        List<Field> second =
                ReflectionFieldCache.fieldsFor(ScreeningRecord.class);

        assertSame(first, second);
        assertEquals(1, ReflectionFieldCache.size());
    }

    @Test
    void shouldReturnImmutableList() {
        List<Field> fields =
                ReflectionFieldCache.fieldsFor(ScreeningRecord.class);

        assertThrows(
                UnsupportedOperationException.class,
                () -> fields.clear()
        );
    }

    @Test
    void shouldClearCache() {
        ReflectionFieldCache.fieldsFor(ScreeningRecord.class);
        assertEquals(1, ReflectionFieldCache.size());

        ReflectionFieldCache.clear();

        assertEquals(0, ReflectionFieldCache.size());
    }

    @Test
    void shouldCacheDifferentClassesSeparately() {
        ReflectionFieldCache.fieldsFor(ScreeningRecord.class);
        ReflectionFieldCache.fieldsFor(OtherRecord.class);

        assertEquals(2, ReflectionFieldCache.size());
    }

    @Test
    void shouldIgnoreFieldsWithoutAnnotation() {
        List<Field> fields =
                ReflectionFieldCache.fieldsFor(OtherRecord.class);

        assertEquals(1, fields.size());
        assertEquals("name", fields.get(0).getName());
    }

    private static class OtherRecord {
        @CsvColumn("назва")
        private String name;

        private int ignored;
    }
}