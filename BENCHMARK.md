# Reflection Caching Benchmark

## Environment

- Project: LAB_KZP
- Laboratory work: 5
- Java: 21
- Benchmark class: `ReflectionBenchmark`
- Model: `ScreeningRecord`
- Annotated fields: 5
- Iterations: 1,000,000
- Warm-up iterations: 100,000

## Results

| Metric | Result |
|---|---:|
| Without caching | 157.416 ms |
| With caching | 6.164 ms |
| Speedup | 25.54x |
| Cached classes | 1 |

## Methodology

The benchmark compares two approaches:

1. `ReflectionFieldCache.scanFields()` performs a new reflection scan.
2. `ReflectionFieldCache.fieldsFor()` reuses cached field metadata.

Both approaches were executed after JVM warm-up.

## Conclusion

In this benchmark run, cached reflection metadata lookup was
approximately 25.54 times faster than repeated field discovery.

The measurements concern reflection metadata lookup only,
not complete CSV or JSON file export.

Results may vary depending on hardware, JVM optimizations,
system load, and benchmark execution order.