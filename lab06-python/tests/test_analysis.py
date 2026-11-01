"""Тести аналізу продажів квитків."""

from datetime import datetime
from decimal import Decimal

import pytest

from kzp_lab06.analysis import analyze_screenings
from kzp_lab06.models import Screening


def make_screening(
    film: str,
    hall: str,
    sold: int,
    price: str = "150.00",
) -> Screening:
    """Створити тестовий кіносеанс."""
    return Screening(
        film=film,
        hall=hall,
        start=datetime(2026, 10, 20, 18, 0),
        price=Decimal(price),
        sold=sold,
    )


def test_revenue_by_hall():
    """Виручка правильно підсумовується за залами."""
    screenings = [
        make_screening("Avatar", "1", 100),
        make_screening("Dune", "1", 80, "200.00"),
        make_screening("Interstellar", "2", 120),
    ]

    result = analyze_screenings(screenings)

    assert result.revenue_by_hall["1"] == Decimal("31000.00")
    assert result.revenue_by_hall["2"] == Decimal("18000.00")
    assert result.total_revenue == Decimal("49000.00")


def test_filter_threshold():
    """Сеанси з sold <= threshold не потрапляють у вибірку."""
    screenings = [
        make_screening("Avatar", "1", 50),
        make_screening("Dune", "2", 51),
    ]

    result = analyze_screenings(screenings, threshold=50)

    assert len(result.selected) == 1
    assert result.selected[0].film == "Dune"


def test_unique_films():
    """Повторювані назви фільмів не дублюються у множині."""
    screenings = [
        make_screening("Avatar", "1", 100),
        make_screening("Avatar", "2", 80),
        make_screening("Dune", "3", 90),
    ]

    result = analyze_screenings(screenings)

    assert result.unique_films == {"Avatar", "Dune"}


def test_most_popular():
    """Найпопулярніший сеанс визначається за sold."""
    screenings = [
        make_screening("Avatar", "1", 100),
        make_screening("Dune", "2", 150),
    ]

    result = analyze_screenings(screenings)

    assert result.most_popular is not None
    assert result.most_popular.film == "Dune"
    assert result.most_popular.sold == 150


def test_empty_selection():
    """Порожня вибірка обробляється без помилок."""
    screenings = [
        make_screening("Avatar", "1", 20),
    ]

    result = analyze_screenings(screenings, threshold=50)

    assert result.selected == []
    assert result.revenue_by_hall == {}
    assert result.unique_films == set()
    assert result.total_revenue == Decimal("0")
    assert result.most_popular is None


def test_negative_threshold():
    """Від'ємний поріг продажів заборонений."""
    with pytest.raises(ValueError, match="Поріг"):
        analyze_screenings([], threshold=-1)
