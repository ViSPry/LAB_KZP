"""Аналіз продажів квитків у кінотеатрі."""

from collections.abc import Iterable
from dataclasses import dataclass
from decimal import Decimal
from itertools import filterfalse

from kzp_lab06.models import Screening


@dataclass(frozen=True)
class AnalysisResult:
    """Результати аналізу кіносеансів."""

    selected: list[Screening]
    revenue_by_hall: dict[str, Decimal]
    unique_films: set[str]
    total_revenue: Decimal
    most_popular: Screening | None


def analyze_screenings(
    screenings: Iterable[Screening],
    threshold: int = 50,
) -> AnalysisResult:
    """Проаналізувати сеанси, де sold > threshold."""

    if threshold < 0:
        raise ValueError("Поріг продажів не може бути від'ємним")

    selected = [screening for screening in screenings if screening.sold > threshold]

    revenue_by_hall: dict[str, Decimal] = {}

    for screening in selected:
        revenue_by_hall[screening.hall] = (
            revenue_by_hall.get(screening.hall, Decimal("0")) + screening.revenue
        )

    unique_films = {screening.film for screening in selected}

    total_revenue = sum(
        revenue_by_hall.values(),
        Decimal("0"),
    )

    most_popular = max(
        selected,
        key=lambda screening: (
            screening.sold,
            screening.film,
            screening.hall,
            screening.start,
        ),
        default=None,
    )

    return AnalysisResult(
        selected=selected,
        revenue_by_hall=revenue_by_hall,
        unique_films=unique_films,
        total_revenue=total_revenue,
        most_popular=most_popular,
    )


@dataclass(frozen=True)
class StreamingAnalysisResult:
    """Результати потокового аналізу без списку сеансів."""

    revenue_by_hall: dict[str, Decimal]
    unique_films: set[str]
    total_revenue: Decimal
    most_popular: Screening | None
    selected_count: int


def analyze_screenings_streaming(
    screenings: Iterable[Screening],
    threshold: int = 50,
) -> StreamingAnalysisResult:
    """Обчислити статистику за один прохід без збереження всіх записів."""

    if threshold < 0:
        raise ValueError("Поріг продажів не може бути від'ємним")

    revenue_by_hall: dict[str, Decimal] = {}
    unique_films: set[str] = set()
    total_revenue = Decimal("0")
    most_popular: Screening | None = None
    selected_count = 0

    # itertools залишає тільки сеанси, де sold > threshold.
    filtered = filterfalse(
        lambda screening: screening.sold <= threshold,
        screenings,
    )

    for screening in filtered:
        selected_count += 1
        unique_films.add(screening.film)

        revenue = screening.revenue
        total_revenue += revenue

        revenue_by_hall[screening.hall] = (
            revenue_by_hall.get(screening.hall, Decimal("0")) + revenue
        )

        current_key = (
            screening.sold,
            screening.film,
            screening.hall,
            screening.start,
        )

        if most_popular is None:
            most_popular = screening
        else:
            popular_key = (
                most_popular.sold,
                most_popular.film,
                most_popular.hall,
                most_popular.start,
            )

            if current_key > popular_key:
                most_popular = screening

    return StreamingAnalysisResult(
        revenue_by_hall=revenue_by_hall,
        unique_films=unique_films,
        total_revenue=total_revenue,
        most_popular=most_popular,
        selected_count=selected_count,
    )
