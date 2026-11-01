"""Інтерфейс командного рядка для аналізу кіносеансів."""

import argparse
import sys
from pathlib import Path

from kzp_lab06.analysis import analyze_screenings
from kzp_lab06.errors import DataError
from kzp_lab06.loader import load_screenings
from kzp_lab06.reporting import save_report


def non_negative_int(value: str) -> int:
    """Перевірити невід'ємний цілий поріг продажів."""
    try:
        number = int(value)
    except ValueError as error:
        raise argparse.ArgumentTypeError("Поріг повинен бути цілим числом") from error

    if number < 0:
        raise argparse.ArgumentTypeError("Поріг не може бути від'ємним")

    return number


def main(argv: list[str] | None = None) -> int:
    """Запустити аналіз CSV і повернути код завершення."""
    parser = argparse.ArgumentParser(description="Аналіз продажів квитків кінотеатру")

    parser.add_argument(
        "--input",
        type=Path,
        required=True,
        help="Шлях до CSV-файлу з кіносеансами",
    )

    parser.add_argument(
        "--output",
        type=Path,
        required=True,
        help="Шлях до текстового звіту",
    )

    parser.add_argument(
        "--threshold",
        type=non_negative_int,
        default=50,
        help="Мінімальна кількість продажів (за замовчуванням 50)",
    )

    args = parser.parse_args(argv)

    try:
        screenings = load_screenings(args.input)
        result = analyze_screenings(
            screenings,
            threshold=args.threshold,
        )
        save_report(result, args.output)

    except (DataError, OSError, UnicodeError) as error:
        print(f"Помилка: {error}", file=sys.stderr)
        return 2

    print(f"Звіт створено: {args.output}")
    print(f"Відібрано сеансів: {len(result.selected)}")
    print(f"Загальна виручка: {result.total_revenue:.2f} грн")

    return 0


if __name__ == "__main__":
    sys.exit(main())
