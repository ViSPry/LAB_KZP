"""Порівняння пам'яті звичайного та потокового аналізу CSV."""

import argparse
import csv
import tempfile
import tracemalloc
from pathlib import Path

from kzp_lab06.analysis import (
    analyze_screenings,
    analyze_screenings_streaming,
)
from kzp_lab06.loader import iter_screenings, load_screenings


def measure(operation, path):
    """Виміряти пікову пам'ять під час виконання операції."""
    tracemalloc.start()

    try:
        result = operation(path)
        _, peak = tracemalloc.get_traced_memory()
    finally:
        tracemalloc.stop()

    return result, peak / (1024 * 1024)


def load_all(path):
    """Завантажити всі записи у список."""
    return len(load_screenings(path))


def stream_all(path):
    """Прочитати записи по одному через генератор."""
    return sum(1 for _ in iter_screenings(path))


def analysis_summary(result, selected_count):
    """Повернути підсумки для порівняння двох реалізацій."""
    return (
        selected_count,
        result.total_revenue,
        result.revenue_by_hall,
        result.unique_films,
        result.most_popular,
    )


def analyze_all(path):
    """Звичайне читання та аналіз із накопиченням списків."""
    screenings = load_screenings(path)
    result = analyze_screenings(screenings)

    return analysis_summary(result, len(result.selected))


def analyze_stream(path):
    """Потокове читання та аналіз без списку всіх сеансів."""
    result = analyze_screenings_streaming(iter_screenings(path))

    return analysis_summary(result, result.selected_count)


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--rows", type=int, default=10000)
    args = parser.parse_args()

    if args.rows <= 0:
        parser.error("--rows має бути додатним числом")

    with tempfile.TemporaryDirectory() as directory:
        source = Path(directory) / "benchmark.csv"

        with source.open("w", encoding="utf-8", newline="") as file:
            writer = csv.writer(file)
            writer.writerow(["film", "hall", "start", "price", "sold"])

            for index in range(args.rows):
                writer.writerow(
                    [
                        f"Film {index % 100}",
                        str(index % 5 + 1),
                        "2026-10-20T18:00",
                        "150.00",
                        str(index % 200),
                    ]
                )

        regular_count, regular_memory = measure(load_all, source)
        stream_count, stream_memory = measure(stream_all, source)

        regular_result, regular_analysis_memory = measure(analyze_all, source)
        streaming_result, streaming_analysis_memory = measure(analyze_stream, source)

        if regular_count != stream_count:
            raise RuntimeError("Кількість прочитаних записів не збігається")

        if regular_result != streaming_result:
            raise RuntimeError("Результати аналізу не збігаються")

        print(f"Кількість записів: {args.rows}")
        print()
        print("=== ЧИТАННЯ CSV ===")
        print(f"Звичайне читання: {regular_memory:.2f} MiB")
        print(f"Потокове читання: {stream_memory:.2f} MiB")
        print(f"Оброблено записів: {regular_count} / {stream_count}")

        print()
        print("=== ПОВНИЙ АНАЛІЗ ===")
        print(f"Звичайний аналіз: {regular_analysis_memory:.2f} MiB")
        print(f"Потоковий аналіз: {streaming_analysis_memory:.2f} MiB")

        print()
        print(f"Відібрано сеансів: {regular_result[0]}")
        print(f"Загальний дохід: {regular_result[1]}")
        print("Результати двох способів аналізу збігаються!")


if __name__ == "__main__":
    main()
