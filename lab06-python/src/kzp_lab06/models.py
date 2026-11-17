"""Об'єктні моделі кіносеансів."""

from __future__ import annotations

from datetime import datetime
from decimal import Decimal


class Screening:
    """Базова модель кіносеансу з перевіркою даних."""

    def __init__(
        self,
        film: str,
        hall: str,
        start: datetime,
        price: Decimal,
        sold: int,
    ) -> None:
        self.film = film
        self.hall = hall
        self.start = start
        self.price = price
        self.sold = sold

    @property
    def film(self) -> str:
        return self._film

    @film.setter
    def film(self, value: str) -> None:
        if not isinstance(value, str) or not value.strip():
            raise ValueError("Назва фільму не може бути порожньою")
        self._film = value.strip()

    @property
    def hall(self) -> str:
        return self._hall

    @hall.setter
    def hall(self, value: str) -> None:
        if not isinstance(value, str) or not value.strip():
            raise ValueError("Назва залу не може бути порожньою")
        self._hall = value.strip()

    @property
    def start(self) -> datetime:
        return self._start

    @start.setter
    def start(self, value: datetime) -> None:
        if not isinstance(value, datetime) or value.tzinfo is not None:
            raise ValueError("Початок сеансу має бути датою без часового поясу")
        self._start = value

    @property
    def price(self) -> Decimal:
        return self._price

    @price.setter
    def price(self, value: Decimal) -> None:
        if not isinstance(value, Decimal) or not value.is_finite() or value < 0:
            raise ValueError("Ціна має бути невід'ємним скінченним Decimal")
        self._price = value

    @property
    def sold(self) -> int:
        return self._sold

    @sold.setter
    def sold(self, value: int) -> None:
        if type(value) is not int or value < 0:
            raise ValueError("Кількість проданих квитків має бути невід'ємною")
        self._sold = value

    def revenue(self) -> Decimal:
        """Стандартна виручка; збережено сумісність із ЛР №6."""
        return self.price * self.sold

    def sort_key(self) -> tuple[int, str, str, datetime]:
        """Детермінований ключ для порівняння популярності."""
        return self.sold, self.film, self.hall, self.start

    def __str__(self) -> str:
        return f"{self.film} — зал {self.hall}, {self.start:%Y-%m-%d %H:%M}"

    def __repr__(self) -> str:
        return (
            f"{type(self).__name__}("
            f"film={self.film!r}, "
            f"hall={self.hall!r}, "
            f"start={self.start!r}, "
            f"price={self.price!r}, "
            f"sold={self.sold!r})"
        )

    def __eq__(self, other: object) -> bool:
        if type(self) is not type(other):
            return NotImplemented

        return (
            self.film,
            self.hall,
            self.start,
            self.price,
            self.sold,
        ) == (
            other.film,
            other.hall,
            other.start,
            other.price,
            other.sold,
        )

    __hash__ = None


class RegularScreening(Screening):
    """Звичайний кіносеанс."""

    def revenue(self) -> Decimal:
        return self.price * self.sold


class PremiumScreening(Screening):
    """Преміальний кіносеанс із коефіцієнтом ціни 1,5."""

    PREMIUM_MULTIPLIER = Decimal("1.50")

    def revenue(self) -> Decimal:
        return self.price * self.sold * self.PREMIUM_MULTIPLIER
