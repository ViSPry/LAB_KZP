"""Тести об'єктної моделі кіносеансів ЛР №7."""

from datetime import datetime
from decimal import Decimal

import pytest

from kzp_lab06.models import PremiumScreening, RegularScreening


def make_screening(screening_type=RegularScreening):
    return screening_type(
        film="Інтерстеллар",
        hall="1",
        start=datetime(2026, 11, 2, 18, 0),
        price=Decimal("100.00"),
        sold=2,
    )


def test_regular_screening_revenue():
    screening = make_screening()

    assert screening.revenue() == Decimal("200.00")


def test_premium_screening_revenue():
    screening = make_screening(PremiumScreening)

    assert screening.revenue() == Decimal("300.0000")


def test_sold_setter_accepts_zero():
    screening = make_screening()

    screening.sold = 0

    assert screening.sold == 0
    assert screening.revenue() == Decimal("0.00")


def test_sold_setter_rejects_negative():
    screening = make_screening()

    with pytest.raises(ValueError):
        screening.sold = -1

    assert screening.sold == 2


def test_price_setter_rejects_negative():
    screening = make_screening()

    with pytest.raises(ValueError):
        screening.price = Decimal("-1")

    assert screening.price == Decimal("100.00")


def test_equal_screenings():
    first = make_screening()
    second = make_screening()

    assert first == second


def test_price_setter_accepts_zero():
    screening = make_screening()
    screening.price = Decimal("0")
    assert screening.price == Decimal("0")
    assert screening.revenue() == Decimal("0")


@pytest.mark.parametrize(
    "invalid_price",
    [
        Decimal("NaN"),
        Decimal("Infinity"),
        Decimal("-Infinity"),
    ],
)
def test_price_setter_rejects_non_finite(invalid_price):
    screening = make_screening()
    with pytest.raises(ValueError):
        screening.price = invalid_price
    assert screening.price == Decimal("100.00")


def test_sold_setter_rejects_boolean():
    screening = make_screening()
    with pytest.raises((TypeError, ValueError)):
        screening.sold = True
    assert screening.sold == 2


def test_film_setter_rejects_blank_name():
    screening = make_screening()
    with pytest.raises(ValueError):
        screening.film = "   "
    assert screening.film == "Інтерстеллар"


def test_hall_setter_rejects_blank_name():
    screening = make_screening()
    with pytest.raises(ValueError):
        screening.hall = ""
    assert screening.hall == "1"


def test_start_setter_rejects_invalid_type():
    screening = make_screening()
    with pytest.raises((TypeError, ValueError)):
        screening.start = "2026-11-02T18:00"
    assert screening.start == datetime(2026, 11, 2, 18, 0)


def test_different_screening_types_are_not_equal():
    regular = make_screening(RegularScreening)
    premium = make_screening(PremiumScreening)
    assert regular != premium


def test_str_and_repr_show_screening_information():
    screening = make_screening()
    assert "Інтерстеллар" in str(screening)
    assert "RegularScreening" in repr(screening)
    assert "Інтерстеллар" in repr(screening)


def test_sort_key_is_deterministic():
    screening = make_screening()
    assert screening.sort_key() == (
        2,
        "Інтерстеллар",
        "1",
        datetime(2026, 11, 2, 18, 0),
    )


def test_revenue_updates_after_property_changes():
    screening = make_screening(PremiumScreening)
    screening.price = Decimal("200")
    screening.sold = 3
    assert screening.revenue() == Decimal("900.00")
