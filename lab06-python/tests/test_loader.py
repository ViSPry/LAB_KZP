"""Тести завантаження CSV-даних кінотеатру."""

from decimal import Decimal

import pytest

from kzp_lab06.errors import DataError
from kzp_lab06.loader import load_screenings


def test_load_valid_csv(tmp_path):
    """Коректний CSV завантажується без помилок."""
    csv_file = tmp_path / "valid.csv"
    csv_file.write_text(
        "film,hall,start,price,sold\nAvatar,2,2026-10-20T18:00,150.00,100\n",
        encoding="utf-8",
    )

    screenings = load_screenings(csv_file)

    assert len(screenings) == 1
    assert screenings[0].film == "Avatar"
    assert screenings[0].hall == "2"
    assert screenings[0].price == Decimal("150.00")
    assert screenings[0].sold == 100
    assert screenings[0].revenue == Decimal("15000.00")


def test_load_java_export(tmp_path):
    """Підтримується формат CSV із попередніх Java-лабораторних."""
    csv_file = tmp_path / "java.csv"
    csv_file.write_text(
        "фільм,зал,продано,час,ціна\nDune,1,80,2026-10-20T20:00,200.0\n",
        encoding="utf-8",
    )

    screenings = load_screenings(csv_file)

    assert len(screenings) == 1
    assert screenings[0].film == "Dune"
    assert screenings[0].sold == 80
    assert screenings[0].revenue == Decimal("16000.0")


def test_empty_csv_with_header(tmp_path):
    """CSV лише із заголовком повертає порожній список."""
    csv_file = tmp_path / "empty.csv"
    csv_file.write_text(
        "film,hall,start,price,sold\n",
        encoding="utf-8",
    )

    assert load_screenings(csv_file) == []


def test_missing_column(tmp_path):
    """Відсутній обов'язковий стовпець викликає DataError."""
    csv_file = tmp_path / "missing.csv"
    csv_file.write_text(
        "film,hall,start,price\nAvatar,2,2026-10-20T18:00,150.00\n",
        encoding="utf-8",
    )

    with pytest.raises(DataError, match="sold"):
        load_screenings(csv_file)


def test_negative_price(tmp_path):
    """Від'ємна ціна не допускається."""
    csv_file = tmp_path / "negative.csv"
    csv_file.write_text(
        "film,hall,start,price,sold\nAvatar,2,2026-10-20T18:00,-150.00,100\n",
        encoding="utf-8",
    )

    with pytest.raises(DataError, match="price"):
        load_screenings(csv_file)


def test_film_with_comma(tmp_path):
    """Назва фільму з комою правильно читається CSV-парсером."""
    csv_file = tmp_path / "quoted.csv"
    csv_file.write_text(
        "film,hall,start,price,sold\n"
        '"Місія, нездійсненна",1,2026-10-20T18:00,190.00,50\n',
        encoding="utf-8",
    )

    screenings = load_screenings(csv_file)

    assert screenings[0].film == "Місія, нездійсненна"
    assert screenings[0].sold == 50
