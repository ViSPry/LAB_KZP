"""Перевірка поліморфного аналізу кіносеансів ЛР №7."""

from datetime import datetime
from decimal import Decimal

import pytest

from kzp_lab06.analysis import (
    AnalysisResult,
    analyze_screenings,
    analyze_screenings_streaming,
)
from kzp_lab06.models import PremiumScreening, RegularScreening


def make_mixed_screenings():
    """Сім сеансів двох підтипів, зокрема граничні значення."""
    rows = [
        (RegularScreening, "Аватар", "1", 60, "100", 0),
        (PremiumScreening, "Дюна", "1", 70, "100", 5),
        (RegularScreening, "Інтерстеллар", "2", 100, "80", 10),
        (PremiumScreening, "Оппенгеймер", "2", 51, "200", 15),
        (RegularScreening, "Матриця", "3", 50, "100", 20),
        (PremiumScreening, "Титанік", "3", 80, "50", 25),
        (RegularScreening, "Гладіатор", "3", 0, "300", 30),
    ]
    return [
        kind(
            film=film,
            hall=hall,
            start=datetime(2026, 11, 2, 18, minute),
            price=Decimal(price),
            sold=sold,
        )
        for kind, film, hall, sold, price, minute in rows
    ]


@pytest.mark.parametrize(
    "analyzer",
    [analyze_screenings, analyze_screenings_streaming],
)
def test_mixed_screenings_revenue_by_hall(analyzer):
    screenings = make_mixed_screenings()

    result = analyzer(screenings, threshold=50)

    assert result.revenue_by_hall == {
        "1": Decimal("16500"),
        "2": Decimal("23300"),
        "3": Decimal("6000"),
    }
    assert result.total_revenue == Decimal("45800")
    assert result.most_popular is screenings[2]
    assert result.unique_films == {
        "Аватар",
        "Дюна",
        "Інтерстеллар",
        "Оппенгеймер",
        "Титанік",
    }

    if isinstance(result, AnalysisResult):
        assert len(result.selected) == 5
    else:
        assert result.selected_count == 5


@pytest.mark.parametrize(
    "analyzer",
    [analyze_screenings, analyze_screenings_streaming],
)
def test_mixed_screenings_empty_input(analyzer):
    result = analyzer([], threshold=50)

    assert result.revenue_by_hall == {}
    assert result.total_revenue == Decimal("0")
    assert result.unique_films == set()
    assert result.most_popular is None

    if isinstance(result, AnalysisResult):
        assert result.selected == []
    else:
        assert result.selected_count == 0
