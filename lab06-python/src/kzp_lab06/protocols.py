"""Протокол для поліморфної обробки кіносеансів."""

from datetime import datetime
from decimal import Decimal
from typing import Protocol, runtime_checkable


@runtime_checkable
class ScreeningLike(Protocol):
    """Спільний інтерфейс кіносеансів різних типів."""

    @property
    def film(self) -> str: ...

    @property
    def hall(self) -> str: ...

    @property
    def start(self) -> datetime: ...

    @property
    def price(self) -> Decimal: ...

    @property
    def sold(self) -> int: ...

    def revenue(self) -> Decimal:
        """Повернути виручку сеансу."""
        ...
