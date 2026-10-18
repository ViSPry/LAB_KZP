package ua.lpnu.kzp;

import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InaccessibleObjectException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Generic reflection-based CSV exporter.
 */
public final class CsvExporter {

    /**
     * Creates a CSV exporter compatible with the common Exporter interface.
     */
    public static <T> Exporter<T> forType(Class<T> type) {
        java.util.Objects.requireNonNull(type, "Type must not be null");
        return (path, records) -> write(path, type, records);
    }


    private CsvExporter() {
    }

    public static <T> void write(
            Path path,
            Class<T> type,
            List<? extends T> records) {

        Objects.requireNonNull(path, "Path must not be null");
        Objects.requireNonNull(type, "Type must not be null");
        Objects.requireNonNull(records, "Records must not be null");

        List<Field> fields = findAnnotatedFields(type);

        if (fields.isEmpty()) {
            throw new DataStorageException(
                    "No @CsvColumn fields found in " + type.getName());
        }

        try {
            Path parent = path.toAbsolutePath().getParent();

            if (parent != null) {
                Files.createDirectories(parent);
            }

            try (BufferedWriter writer = Files.newBufferedWriter(
                    path, StandardCharsets.UTF_8)) {

                String header = fields.stream()
                        .map(field -> field.getAnnotation(
                                CsvColumn.class).value())
                        .map(CsvExporter::escape)
                        .collect(Collectors.joining(","));

                writer.write(header);
                writer.newLine();

                for (T record : records) {
                    if (record == null) {
                        throw new DataStorageException(
                                "CSV records must not contain null");
                    }

                    List<String> values = new ArrayList<>();

                    for (Field field : fields) {
                        Object value = field.get(record);
                        values.add(escape(formatValue(value)));
                    }

                    writer.write(String.join(",", values));
                    writer.newLine();
                }
            }
        } catch (IOException | IllegalAccessException
                 | InaccessibleObjectException | SecurityException exception) {
            throw new DataStorageException(
                    "Failed to export CSV to " + path, exception);
        }
    }

    private static List<Field> findAnnotatedFields(Class<?> type) {
        List<Field> fields = new ArrayList<>();

        for (Class<?> current = type;
             current != null && current != Object.class;
             current = current.getSuperclass()) {

            for (Field field : current.getDeclaredFields()) {
                if (field.isAnnotationPresent(CsvColumn.class)) {
                    try {
                        field.setAccessible(true);
                    } catch (InaccessibleObjectException
                             | SecurityException exception) {
                        throw new DataStorageException(
                                "Cannot access CSV field: "
                                        + field.getName(), exception);
                    }

                    fields.add(field);
                }
            }
        }

        fields.sort(Comparator.comparing(Field::getName));
        return fields;
    }

    private static String formatValue(Object value) {
        if (value == null) {
            return "";
        }

        if (value instanceof Double number) {
            return String.format(Locale.ROOT, "%s", number);
        }

        if (value instanceof Float number) {
            return String.format(Locale.ROOT, "%s", number);
        }

        return value.toString();
    }

    private static String escape(String value) {
        if (value.contains(",")
                || value.contains("\"")
                || value.contains("\n")
                || value.contains("\r")) {

            return "\"" + value.replace("\"", "\"\"") + "\"";
        }

        return value;
    }
}