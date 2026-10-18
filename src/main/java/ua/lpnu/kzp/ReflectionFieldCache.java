package ua.lpnu.kzp;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Thread-safe cache of annotated fields discovered through reflection.
 */
public final class ReflectionFieldCache {

    private static final ConcurrentMap<Class<?>, List<Field>> CACHE =
            new ConcurrentHashMap<>();

    private ReflectionFieldCache() {
    }

    /**
     * Returns cached annotated fields for the specified class.
     */
    public static List<Field> fieldsFor(Class<?> type) {
        return CACHE.computeIfAbsent(type, ReflectionFieldCache::scanFields);
    }

    /**
     * Performs an uncached reflection scan.
     * Also used to compare performance in benchmarks.
     */
    public static List<Field> scanFields(Class<?> type) {
        List<Field> fields = new ArrayList<>();
        Set<String> seenNames = new HashSet<>();

        for (Class<?> current = type;
                current != null && current != Object.class;
                current = current.getSuperclass()) {

            for (Field field : current.getDeclaredFields()) {
                if (!Modifier.isStatic(field.getModifiers())
                        && seenNames.add(field.getName())
                        && field.isAnnotationPresent(CsvColumn.class)) {
                    try {
                        if (!field.trySetAccessible()) {
                            throw new DataStorageException(
                                    "Cannot access annotated field: "
                                            + field.getName());
                        }
                    } catch (SecurityException exception) {
                        throw new DataStorageException(
                                "Cannot access annotated field: "
                                        + field.getName(),
                                exception);
                    }

                    fields.add(field);
                }
            }
        }

        fields.sort(Comparator.comparing(Field::getName));
        return List.copyOf(fields);
    }

    /**
     * Clears cached metadata. Intended for tests and benchmarks.
     */
    public static void clear() {
        CACHE.clear();
    }

    /**
     * Returns the number of classes currently cached.
     */
    public static int size() {
        return CACHE.size();
    }
}