"""Тести потокової обробки CSV."""

from collections.abc import Iterator

from kzp_lab06.models import Screening
from kzp_lab06.streaming import iter_screenings


def test_iter_screenings(tmp_path):
    """Генератор повертає кіносеанси у правильному порядку."""
    source = tmp_path / "input.csv"

    source.write_text(
        "film,hall,start,price,sold\n"
        "Avatar,2,2026-10-20T18:00,150.00,100\n"
        "Dune,1,2026-10-20T20:00,200.00,80\n",
        encoding="utf-8",
    )

    screenings = iter_screenings(source)

    assert isinstance(screenings, Iterator)

    first = next(screenings)
    second = next(screenings)

    assert isinstance(first, Screening)
    assert first.film == "Avatar"
    assert second.film == "Dune"
    assert first.revenue == 15000
    assert second.revenue == 16000


def test_streaming_is_lazy(tmp_path):
    """Генератор не читає наступний рядок завчасно."""
    source = tmp_path / "input.csv"

    source.write_text(
        "film,hall,start,price,sold\n"
        "Avatar,2,2026-10-20T18:00,150.00,100\n"
        "Dune,1,2026-10-20T20:00,INVALID,80\n",
        encoding="utf-8",
    )

    screenings = iter_screenings(source)

    first = next(screenings)
    assert first.film == "Avatar"

    from kzp_lab06.errors import DataError

    try:
        next(screenings)
    except DataError as error:
        assert error.row == 3
    else:
        raise AssertionError("Очікувалася помилка DataError")
