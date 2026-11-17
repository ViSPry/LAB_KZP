"""Тести структурної типізації кіносеансів."""

from datetime import datetime
from decimal import Decimal
from types import SimpleNamespace

import pytest

from kzp_lab06.models import PremiumScreening, RegularScreening
from kzp_lab06.protocols import ScreeningLike


@pytest.mark.parametrize(
    ("screening_type", "expected_revenue"),
    [
        (RegularScreening, Decimal("200.00")),
        (PremiumScreening, Decimal("300.0000")),
    ],
)
def test_screening_subtypes_implement_protocol(screening_type, expected_revenue):
    screening = screening_type(
        film="Інтерстеллар",
        hall="1",
        start=datetime(2026, 11, 2, 18, 0),
        price=Decimal("100.00"),
        sold=2,
    )

    assert isinstance(screening, ScreeningLike)
    assert screening.revenue() == expected_revenue


def test_protocol_accepts_independent_class():
    class ExternalScreening:
        film = "Дюна"
        hall = "2"
        start = datetime(2026, 11, 2, 19, 30)
        price = Decimal("50.00")
        sold = 3

        def revenue(self) -> Decimal:
            return self.price * self.sold

    screening = ExternalScreening()

    assert isinstance(screening, ScreeningLike)
    assert screening.revenue() == Decimal("150.00")


def test_protocol_rejects_object_without_revenue():
    screening = SimpleNamespace(
        film="Аватар",
        hall="3",
        start=datetime(2026, 11, 3, 17, 0),
        price=Decimal("100.00"),
        sold=2,
    )

    assert not isinstance(screening, ScreeningLike)
