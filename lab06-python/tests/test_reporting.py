"""Тести формування текстового звіту кінотеатру."""

from datetime import datetime
from decimal import Decimal

from kzp_lab06.analysis import analyze_screenings
from kzp_lab06.models import Screening
from kzp_lab06.reporting import build_report, save_report


def make_screening(film, hall, sold, price="150.00"):
    """Створити тестовий кіносеанс."""
    return Screening(
        film=film,
        hall=hall,
        start=datetime(2026, 10, 20, 18, 0),
        price=Decimal(price),
        sold=sold,
    )


def test_report_contains_revenue():
    """Звіт містить правильну загальну виручку."""
    result = analyze_screenings([make_screening("Avatar", "1", 100)])

    report = build_report(result)

    assert "ЗАГАЛЬНА ВИРУЧКА: 15000.00 грн" in report
    assert "Фільм: Avatar" in report


def test_report_sorted_halls():
    """Зали у звіті відсортовані за назвою."""
    result = analyze_screenings(
        [
            make_screening("Avatar", "3", 100),
            make_screening("Dune", "1", 80),
        ]
    )

    report = build_report(result)

    assert report.index("Зал 1:") < report.index("Зал 3:")


def test_empty_report():
    """Порожня вибірка формує коректний звіт."""
    result = analyze_screenings([])

    report = build_report(result)

    assert "Відібрано сеансів: 0" in report
    assert "ЗАГАЛЬНА ВИРУЧКА: 0.00 грн" in report
    assert "НАЙПОПУЛЯРНІШИЙ СЕАНС:\nНемає даних" in report


def test_save_report_utf8(tmp_path):
    """Звіт зберігається у UTF-8."""
    result = analyze_screenings([make_screening("Інтерстеллар", "1", 100)])

    output = tmp_path / "reports" / "cinema.txt"

    save_report(result, output)

    assert output.exists()
    assert "Інтерстеллар" in output.read_text(encoding="utf-8")
    assert output.read_bytes().count(b"\r\n") == 0
