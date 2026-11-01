"""Завантаження та перевірка CSV-даних кінотеатру."""

import csv
from collections.abc import Iterator
from datetime import datetime
from decimal import Decimal, InvalidOperation
from pathlib import Path

from kzp_lab06.errors import DataError
from kzp_lab06.models import Screening

COLUMNS = {
    "film": ("film", "фільм"),
    "hall": ("hall", "зал"),
    "start": ("start", "час"),
    "price": ("price", "ціна"),
    "sold": ("sold", "продано"),
}


def iter_screenings(path: str | Path) -> Iterator[Screening]:
    """Читати та перевіряти кіносеанси по одному запису."""
    source = Path(path)

    try:
        with source.open(encoding="utf-8-sig", newline="") as file:
            reader = csv.DictReader(file)

            if reader.fieldnames is None:
                raise DataError("CSV-файл порожній або не містить заголовка")

            headers = {
                name.strip(): name for name in reader.fieldnames if name is not None
            }

            mapping = {}
            for field, aliases in COLUMNS.items():
                matches = [headers[a] for a in aliases if a in headers]

                if len(matches) != 1:
                    raise DataError(f"очікується один стовпець для '{field}'")

                mapping[field] = matches[0]

            for row_number, row in enumerate(reader, start=2):
                if None in row or any(value is None for value in row.values()):
                    raise DataError(
                        "некоректна кількість стовпців",
                        row=row_number,
                    )

                values = {}
                for field, column in mapping.items():
                    value = row[column].strip()
                    if not value:
                        raise DataError(
                            "порожнє значення",
                            row=row_number,
                            field=field,
                        )
                    values[field] = value

                try:
                    start = datetime.fromisoformat(values["start"])
                    if start.tzinfo is not None:
                        raise ValueError("Часовий пояс не підтримується")
                except ValueError as error:
                    raise DataError(
                        "некоректна дата або час",
                        row=row_number,
                        field="start",
                    ) from error

                try:
                    price = Decimal(values["price"])
                    if not price.is_finite() or price < 0:
                        raise ValueError("Ціна має бути невід'ємною")
                except (InvalidOperation, ValueError) as error:
                    raise DataError(
                        "некоректна ціна",
                        row=row_number,
                        field="price",
                    ) from error

                try:
                    sold = int(values["sold"])
                    if sold < 0:
                        raise ValueError("Продажі мають бути невід'ємними")
                except ValueError as error:
                    raise DataError(
                        "некоректна кількість проданих квитків",
                        row=row_number,
                        field="sold",
                    ) from error

                yield Screening(
                    film=values["film"],
                    hall=values["hall"],
                    start=start,
                    price=price,
                    sold=sold,
                )

    except (OSError, UnicodeError, csv.Error) as error:
        raise DataError(f"не вдалося прочитати CSV: {source}") from error


def load_screenings(path: str | Path) -> list[Screening]:
    """Завантажити всі кіносеанси у список."""
    return list(iter_screenings(path))
