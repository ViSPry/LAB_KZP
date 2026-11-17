"""Власний контекстний менеджер для роботи зі звітами."""

from pathlib import Path
from types import TracebackType
from typing import Literal, TextIO


class SafeReportFile:
    """Відкрити текстовий звіт і гарантовано закрити файл."""

    def __init__(
        self,
        path: str | Path,
        mode: Literal["r", "w"],
    ) -> None:
        if mode not in ("r", "w"):
            raise ValueError("Підтримуються лише режими 'r' та 'w'")

        self.path = Path(path)
        self.mode = mode
        self._file: TextIO | None = None

    def __enter__(self) -> TextIO:
        """Відкрити файл для читання або запису."""
        if self.mode == "w":
            self.path.parent.mkdir(parents=True, exist_ok=True)

        self._file = self.path.open(
            mode=self.mode,
            encoding="utf-8",
            newline="\n" if self.mode == "w" else None,
        )
        return self._file

    def __exit__(
        self,
        exc_type: type[BaseException] | None,
        exc_value: BaseException | None,
        traceback: TracebackType | None,
    ) -> bool:
        """Закрити файл, не приховуючи можливий виняток."""
        try:
            if self._file is not None:
                self._file.close()
        finally:
            self._file = None

        return False
