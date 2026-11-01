"""Тести потокового аналізу кіносеансів."""

from datetime import datetime
from decimal import Decimal

import pytest

from kzp_lab06.analysis import (
    analyze_screenings,
    analyze_screenings_streaming,
)
from kzp_lab06.models import Screening


def make_screening(
    film: str,
    hall: str,
    sold: int,
    price: str = "100",
) -> Screening:
    return Screening(
        film=film,
        hall=hall,
        start=datetime(2026, 10, 20, 18, 0),
        price=Decimal(price),
        sold=sold,
    )


def test_streaming_matches_regular_analysis():
    screenings = [
        make_screening("Avatar", "1", 100, "150"),
        make_screening("Dune", "2", 80, "200"),
        make_screening("Avatar", "1", 50, "150"),
        make_screening("Interstellar", "3", 120, "180"),
    ]

    regular = analyze_screenings(screenings, threshold=50)
    streaming = analyze_screenings_streaming(
        iter(screenings),
        threshold=50,
    )

    assert streaming.total_revenue == regular.total_revenue
    assert streaming.revenue_by_hall == regular.revenue_by_hall
    assert streaming.unique_films == regular.unique_films
    assert streaming.most_popular == regular.most_popular
    assert streaming.selected_count == len(regular.selected)


def test_streaming_with_generator():
    screenings = (make_screening(f"Film {number}", "1", 60) for number in range(1000))

    result = analyze_screenings_streaming(screenings)

    assert result.selected_count == 1000
    assert result.total_revenue == Decimal("6000000")
    assert len(result.unique_films) == 1000


def test_streaming_empty_result():
    screenings = [
        make_screening("Avatar", "1", 20),
        make_screening("Dune", "2", 50),
    ]

    result = analyze_screenings_streaming(screenings)

    assert result.selected_count == 0
    assert result.total_revenue == Decimal("0")
    assert result.revenue_by_hall == {}
    assert result.unique_films == set()
    assert result.most_popular is None


def test_streaming_rejects_negative_threshold():
    with pytest.raises(ValueError):
        analyze_screenings_streaming([], threshold=-1)
