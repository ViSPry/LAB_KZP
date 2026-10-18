package ua.lpnu.kzp;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InaccessibleObjectException;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Generic reflection-based JSON exporter.
 *
 * @param <T> type of exported records
 */
public final class JsonExporter<T> implements Exporter<T> {

    private final Class<T> type;

    public JsonExporter(Class<T> type) {
        this.type = Objects.requireNonNull(type, "Type must not be null");
    }

    @Override
    public void export(Path path, List<? extends T> records) {
        Objects.requireNonNull(path, "Path must not be null");
        Objects.requireNonNull(records, "Records must not be null");

        List<Field> fields = ReflectionFieldCache.fieldsFor(type);

        if (fields.isEmpty()) {
            throw new DataStorageException(
                    "No @CsvColumn fields found in " + type.getName());
        }

        StringBuilder json = new StringBuilder(records.isEmpty() ? "[" : "[\n");

        for (int index = 0; index < records.size(); index++) {
            T record = records.get(index);

            if (record == null) {
                throw new DataStorageException(
                        "Null record at index " + index);
            }

            json.append("  {");

            for (int fieldIndex = 0;
                    fieldIndex < fields.size(); fieldIndex++) {

                Field field = fields.get(fieldIndex);

                if (fieldIndex > 0) {
                    json.append(",");
                }

                json.append("\n    ")
                        .append(quote(field.getAnnotation(
                                CsvColumn.class).value()))
                        .append(": ");

                try {
                    Object value = readField(field, record);
                    json.append(formatValue(value));
                } catch (IllegalAccessException
                         | InaccessibleObjectException
                         | SecurityException exception) {
                    throw new DataStorageException(
                            "Failed to access field " + field.getName(),
                            exception);
                }
            }

            json.append("\n  }");

            if (index < records.size() - 1) {
                json.append(",");
            }

            json.append("\n");
        }

        json.append("]\n");

        try {
            Path parent = path.toAbsolutePath().getParent();

            if (parent != null) {
                Files.createDirectories(parent);
            }

            Files.writeString(path, json.toString(),
                    StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new DataStorageException(
                    "Failed to export JSON to " + path,
                    exception);
        }
    }

    /**
     * Reads an annotated field with controlled reflective access.
     */
    @SuppressWarnings("java:S3011")
    private static Object readField(Field field, Object record)
            throws IllegalAccessException {
        if (!field.canAccess(record) && !field.trySetAccessible()) {
            throw new DataStorageException(
                    "Cannot access field " + field.getName());
        }
        return field.get(record);
    }


    private static String formatValue(Object value) {
        if (value == null) {
            return "null";
        }

        if (value instanceof Number number) {
            double numericValue = number.doubleValue();

            if (!Double.isFinite(numericValue)) {
                throw new DataStorageException(
                        "JSON cannot represent NaN or Infinity");
            }

            return number.toString();
        }

        if (value instanceof Boolean booleanValue) {
            return booleanValue.toString();
        }

        return quote(value.toString());
    }

    private static String quote(String value) {
        StringBuilder result = new StringBuilder("\"");

        for (int index = 0; index < value.length(); index++) {
            char ch = value.charAt(index);

            switch (ch) {
                case '"' -> result.append("\\\"");
                case '\\' -> result.append("\\\\");
                case '\n' -> result.append("\\n");
                case '\r' -> result.append("\\r");
                case '\t' -> result.append("\\t");
                default -> {
                    if (ch < 0x20) {
                        result.append(String.format(
                                java.util.Locale.ROOT,
                                "\\u%04x", (int) ch));
                    } else {
                        result.append(ch);
                    }
                }
            }
        }

        return result.append('"').toString();
    }
}