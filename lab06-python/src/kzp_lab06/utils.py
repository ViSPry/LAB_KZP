"""Допоміжні функції для роботи з кіносеансами."""


def describe_screening(film: str = "Невідомий", *args: str, **kwargs: object) -> str:
    """Сформувати текстовий опис кіносеансу."""
    parts = [f"Фільм: {film}"]

    for detail in args:
        parts.append(str(detail))

    for key, value in sorted(kwargs.items()):
        parts.append(f"{key}: {value}")

    return " | ".join(parts)
