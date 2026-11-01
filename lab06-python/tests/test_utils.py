"""Тести допоміжних функцій кінотеатру."""

from kzp_lab06.utils import describe_screening


def test_default_argument():
    """Перевірка параметра за замовчуванням."""
    assert describe_screening() == "Фільм: Невідомий"


def test_positional_arguments():
    """Перевірка *args."""
    result = describe_screening("Avatar", "Вечірній сеанс", "3D")

    assert result == "Фільм: Avatar | Вечірній сеанс | 3D"


def test_keyword_arguments():
    """Перевірка **kwargs."""
    result = describe_screening("Dune", sold=80, hall=1)

    assert result == "Фільм: Dune | hall: 1 | sold: 80"


def test_combined_arguments():
    """Перевірка одночасного використання *args і **kwargs."""
    result = describe_screening(
        "Interstellar",
        "IMAX",
        hall=3,
        sold=120,
    )

    assert result == "Фільм: Interstellar | IMAX | hall: 3 | sold: 120"
