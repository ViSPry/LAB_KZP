"""Порівняння використання пам'яті під час читання CSV."""

import argparse
import csv
import tempfile
import tracemalloc
from pathlib import Path

from kzp_lab06.loader import iter_screenings, load_screenings


def measure(operation, path):
    """Виміряти пікове використання пам'яті."""
    tracemalloc.start()

    try:
        count = operation(path)
        _, peak = tracemalloc.get_traced_memory()
    finally:
        tracemalloc.stop()

    return count, peak / (1024 * 1024)


def load_all(path):
    """Завантажити всі записи у список."""
    return len(load_screenings(path))


def stream_all(path):
    """Обробити записи по одному."""
    return sum(1 for _ in iter_screenings(path))


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

        print(f"Кількість записів: {args.rows}")
        print(f"Звичайне читання: {regular_memory:.2f} MiB")
        print(f"Потокове читання: {stream_memory:.2f} MiB")
        print(f"Оброблено записів: {regular_count} / {stream_count}")


if __name__ == "__main__":
    main()
