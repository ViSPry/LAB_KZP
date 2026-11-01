"""Власні винятки для обробки даних кінотеатру."""


class DataError(Exception):
    """Помилка вхідних даних CSV."""

    def __init__(
        self,
        message: str,
        row: int | None = None,
        field: str | None = None,
    ) -> None:
        self.row = row
        self.field = field

        details = []

        if row is not None:
            details.append(f"рядок {row}")

        if field is not None:
            details.append(f"поле '{field}'")

        if details:
            message = f"{', '.join(details)}: {message}"

        super().__init__(message)
