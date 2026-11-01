"""Формування та збереження звітів кінотеатру."""

from decimal import Decimal
from pathlib import Path

from kzp_lab06.analysis import AnalysisResult


def format_money(amount: Decimal) -> str:
    """Форматувати грошову суму з двома знаками після крапки."""
    return f"{amount:.2f}"


def build_report(result: AnalysisResult) -> str:
    """Сформувати детермінований текстовий звіт."""
    lines = [
        "ЗВІТ ПРО ПРОДАЖІ КВИТКІВ",
        "=" * 35,
        f"Відібрано сеансів: {len(result.selected)}",
        "",
        "ВИРУЧКА ЗА ЗАЛАМИ:",
    ]

    if result.revenue_by_hall:
        for hall in sorted(result.revenue_by_hall):
            amount = result.revenue_by_hall[hall]
            lines.append(f"Зал {hall}: {format_money(amount)} грн")
    else:
        lines.append("Немає даних")

    lines.extend(
        [
            "",
            "УНІКАЛЬНІ ФІЛЬМИ:",
        ]
    )

    if result.unique_films:
        for film in sorted(result.unique_films):
            lines.append(f"- {film}")
    else:
        lines.append("Немає даних")

    lines.extend(
        [
            "",
            f"ЗАГАЛЬНА ВИРУЧКА: {format_money(result.total_revenue)} грн",
            "",
            "НАЙПОПУЛЯРНІШИЙ СЕАНС:",
        ]
    )

    if result.most_popular is None:
        lines.append("Немає даних")
    else:
        screening = result.most_popular
        lines.extend(
            [
                f"Фільм: {screening.film}",
                f"Зал: {screening.hall}",
                f"Початок: {screening.start:%Y-%m-%dT%H:%M}",
                f"Продано квитків: {screening.sold}",
                f"Виручка: {format_money(screening.revenue)} грн",
            ]
        )

    return "\n".join(lines) + "\n"


def save_report(
    result: AnalysisResult,
    path: str | Path,
) -> None:
    """Записати звіт у файл UTF-8."""
    destination = Path(path)
    destination.parent.mkdir(parents=True, exist_ok=True)

    with destination.open("w", encoding="utf-8", newline="\n") as file:
        file.write(build_report(result))
