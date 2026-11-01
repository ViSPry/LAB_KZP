"""Моделі даних кінотеатру."""

from dataclasses import dataclass
from datetime import datetime
from decimal import Decimal


@dataclass(frozen=True)
class Screening:
    """Один кіносеанс, імпортований із CSV."""

    film: str
    hall: str
    start: datetime
    price: Decimal
    sold: int

    @property
    def revenue(self) -> Decimal:
        """Обчислити виручку від кіносеансу."""
        return self.price * self.sold
