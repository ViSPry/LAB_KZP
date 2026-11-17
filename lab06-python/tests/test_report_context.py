"""Тести власного контекстного менеджера звітів."""

import pytest

from kzp_lab06.report_context import SafeReportFile


def test_report_context_writes_utf8(tmp_path):
    """Контекстний менеджер створює папку та записує UTF-8."""
    output = tmp_path / "reports" / "cinema.txt"

    with SafeReportFile(output, "w") as file:
        file.write("Звіт кінотеатру\n")

    assert file.closed
    assert output.read_text(encoding="utf-8") == "Звіт кінотеатру\n"
    assert b"\r\n" not in output.read_bytes()


def test_report_context_reads_utf8(tmp_path):
    """Контекстний менеджер читає файл та закриває його."""
    source = tmp_path / "cinema.txt"
    source.write_text("Виручка: 104600.00 грн\n", encoding="utf-8")

    with SafeReportFile(source, "r") as file:
        content = file.read()

    assert file.closed
    assert content == "Виручка: 104600.00 грн\n"


def test_report_context_closes_on_exception(tmp_path):
    """Файл закривається навіть після винятку всередині with."""
    output = tmp_path / "cinema.txt"

    with pytest.raises(RuntimeError, match="Тестова помилка"):
        with SafeReportFile(output, "w") as file:
            file.write("Початок звіту\n")
            raise RuntimeError("Тестова помилка")

    assert file.closed
    assert output.read_text(encoding="utf-8") == "Початок звіту\n"


def test_report_context_rejects_invalid_mode(tmp_path):
    """Непідтримуваний режим відкриття заборонено."""
    with pytest.raises(ValueError, match="режими"):
        SafeReportFile(tmp_path / "cinema.txt", "a")
