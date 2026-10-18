package ua.lpnu.kzp;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Simple reflection metadata benchmark.
 * Measures field discovery, not full CSV/JSON export.
 */
public final class ReflectionBenchmark {

    private static final int WARMUP = 100_000;
    private static final int ITERATIONS = 1_000_000;

    private static volatile int sink;

    private ReflectionBenchmark() {
    }

    public static void main(String[] args) {
        System.out.println("=== REFLECTION BENCHMARK ===");
        System.out.println("Iterations: " + ITERATIONS);

        ReflectionFieldCache.clear();

        for (int i = 0; i < WARMUP; i++) {
            sink = ReflectionFieldCache.scanFields(
                    ScreeningRecord.class).size();
            sink = ReflectionFieldCache.fieldsFor(
                    ScreeningRecord.class).size();
        }

        long withoutCache = measure(false);
        long withCache = measure(true);

        double withoutMs = withoutCache / 1_000_000.0;
        double withMs = withCache / 1_000_000.0;

        System.out.printf(
                java.util.Locale.US,
                "Without cache: %.3f ms%n", withoutMs);
        System.out.printf(
                java.util.Locale.US,
                "With cache:    %.3f ms%n", withMs);
        System.out.printf(
                java.util.Locale.US,
                "Speedup:       %.2fx%n",
                (double) withoutCache / withCache);

        System.out.println("Cached classes: "
                + ReflectionFieldCache.size());
        System.out.println("Sink: " + sink);
    }

    private static long measure(boolean cached) {
        long start = System.nanoTime();

        for (int i = 0; i < ITERATIONS; i++) {
            List<Field> fields = cached
                    ? ReflectionFieldCache.fieldsFor(
                            ScreeningRecord.class)
                    : ReflectionFieldCache.scanFields(
                            ScreeningRecord.class);

            sink = fields.size();
        }

        return System.nanoTime() - start;
    }
}