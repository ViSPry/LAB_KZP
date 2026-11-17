"""Тести незмінності підсумків і дубльованих записів ЛР №7."""

from dataclasses import FrozenInstanceError
from datetime import datetime
from decimal import Decimal

import pytest

from kzp_lab06.analysis import (
    AnalysisResult,
    analyze_screenings,
    analyze_screenings_streaming,
)
from kzp_lab06.models import RegularScreening


def make_screening():
    return RegularScreening(
        film="Дюна",
        hall="1",
        start=datetime(2026, 11, 2, 18, 0),
        price=Decimal("100"),
        sold=60,
    )


@pytest.mark.parametrize(
    "analyzer",
    [analyze_screenings, analyze_screenings_streaming],
)
def test_summary_fields_are_frozen(analyzer):
    result = analyzer([make_screening()])

    with pytest.raises(FrozenInstanceError):
        result.total_revenue = Decimal("999")

    assert result.total_revenue == Decimal("6000")


@pytest.mark.parametrize(
    "analyzer",
    [analyze_screenings, analyze_screenings_streaming],
)
def test_duplicate_screenings_counted_separately(analyzer):
    first = make_screening()
    second = make_screening()

    assert first == second
    assert first is not second

    result = analyzer([first, second])

    assert result.revenue_by_hall == {"1": Decimal("12000")}
    assert result.total_revenue == Decimal("12000")
    assert result.unique_films == {"Дюна"}
    assert result.most_popular is first

    if isinstance(result, AnalysisResult):
        assert len(result.selected) == 2
    else:
        assert result.selected_count == 2
