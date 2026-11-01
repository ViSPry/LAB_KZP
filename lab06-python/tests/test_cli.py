"""Тести інтерфейсу командного рядка кінотеатру."""

from kzp_lab06.cli import main


def test_cli_success(tmp_path, capsys):
    """Успішний запуск створює звіт і повертає код 0."""
    source = tmp_path / "input.csv"
    output = tmp_path / "report.txt"

    source.write_text(
        "film,hall,start,price,sold\nAvatar,2,2026-10-20T18:00,150.00,100\n",
        encoding="utf-8",
    )

    exit_code = main(
        [
            "--input",
            str(source),
            "--output",
            str(output),
            "--threshold",
            "50",
        ]
    )

    assert exit_code == 0
    assert output.exists()
    assert "15000.00 грн" in output.read_text(encoding="utf-8")
    assert "Звіт створено:" in capsys.readouterr().out


def test_cli_invalid_csv(tmp_path, capsys):
    """Некоректні дані повертають код 2."""
    source = tmp_path / "invalid.csv"
    output = tmp_path / "report.txt"

    source.write_text(
        "film,hall,start,price,sold\nAvatar,2,2026-10-20T18:00,-150.00,100\n",
        encoding="utf-8",
    )

    exit_code = main(["--input", str(source), "--output", str(output)])

    assert exit_code == 2
    assert not output.exists()
    assert "Помилка:" in capsys.readouterr().err


def test_cli_missing_file(tmp_path, capsys):
    """Відсутній CSV повертає код 2."""
    source = tmp_path / "missing.csv"
    output = tmp_path / "report.txt"

    exit_code = main(["--input", str(source), "--output", str(output)])

    assert exit_code == 2
    assert not output.exists()
    assert "Помилка:" in capsys.readouterr().err


def test_cli_empty_selection(tmp_path):
    """CLI правильно обробляє порожню вибірку."""
    source = tmp_path / "input.csv"
    output = tmp_path / "report.txt"

    source.write_text(
        "film,hall,start,price,sold\nAvatar,2,2026-10-20T18:00,150.00,100\n",
        encoding="utf-8",
    )

    exit_code = main(
        [
            "--input",
            str(source),
            "--output",
            str(output),
            "--threshold",
            "200",
        ]
    )

    assert exit_code == 0
    assert "Відібрано сеансів: 0" in output.read_text(encoding="utf-8")
