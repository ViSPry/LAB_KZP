"""Перевірка помилкових CSV-даних для ЛР №7."""

import pytest

from kzp_lab06.errors import DataError
from kzp_lab06.loader import load_screenings


@pytest.mark.parametrize(
    ("invalid_row", "field"),
    [
        ("Дюна,1,невідома-дата,100,60", "start"),
        ("Дюна,1,2026-11-02T18:00+02:00,100,60", "start"),
        ("Дюна,1,2026-11-02T18:00,100,-1", "sold"),
        ("Дюна,1,2026-11-02T18:00,100,abc", "sold"),
        ("Дюна,1,2026-11-02T18:00,NaN,60", "price"),
        ("Дюна,1,2026-11-02T18:00,abc,60", "price"),
        (",1,2026-11-02T18:00,100,60", "film"),
        ("Дюна,,2026-11-02T18:00,100,60", "hall"),
        ("Дюна,1,2026-11-02T18:00,100,60,EXTRA", None),
        ("Дюна,1,2026-11-02T18:00,100", None),
    ],
)
def test_invalid_third_row_reports_error(tmp_path, invalid_row, field):
    csv_file = tmp_path / "invalid.csv"
    csv_file.write_text(
        "film,hall,start,price,sold\n"
        "Аватар,1,2026-11-02T18:00,100,60\n"
        f"{invalid_row}\n",
        encoding="utf-8",
    )

    with pytest.raises(DataError) as error:
        load_screenings(csv_file)

    message = str(error.value)
    assert "3" in message

    if field is not None:
        assert field in message


def test_csv_without_header_raises_data_error(tmp_path):
    csv_file = tmp_path / "empty.csv"
    csv_file.write_text("", encoding="utf-8")

    with pytest.raises(DataError):
        load_screenings(csv_file)


def test_missing_csv_file_raises_data_error(tmp_path):
    csv_file = tmp_path / "missing.csv"

    with pytest.raises(DataError):
        load_screenings(csv_file)
