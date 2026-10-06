package ua.lpnu.kzp;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MainTest {

    @TempDir
    Path tempDir;

    @Test
    void versionArgumentPrintsVersion() {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        try {
            System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));

            Main.main(new String[]{"--version"});

            assertEquals(
                    "lab01 version 3.0.0",
                    output.toString(StandardCharsets.UTF_8).trim()
            );
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    void helpArgumentPrintsAvailableOptions() {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        try {
            System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));

            Main.main(new String[]{"--help"});

            String result = output.toString(StandardCharsets.UTF_8);

            assertTrue(result.contains("--help"));
            assertTrue(result.contains("--version"));
            assertTrue(result.contains("--input"));
            assertTrue(result.contains("--output"));
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    void validCsvProducesCorrectReport() throws Exception {
        Path input = tempDir.resolve("input.csv");
        Path output = tempDir.resolve("report.txt");

        Files.writeString(
                input,
                "Дюна;1;100.00;10;120\nМатриця;2;200.00;5;136\n",
                StandardCharsets.UTF_8
        );

        Main.main(new String[]{
                "--input", input.toString(),
                "--output", output.toString()
        });

        String report = Files.readString(output, StandardCharsets.UTF_8);

        assertTrue(report.contains("Правильних записів: 2"));
        assertTrue(report.contains("Неправильних записів: 0"));
        assertTrue(report.contains("Загальний виторг: 2000.00 грн"));
        assertTrue(report.contains("Середня ціна квитка: 150.00 грн"));
        assertTrue(report.contains(
                "Максимальна кількість проданих квитків: 10"
        ));
    }

    @Test
    void invalidCsvRowsAreCountedCorrectly() throws Exception {
        Path input = tempDir.resolve("invalid-input.csv");
        Path output = tempDir.resolve("invalid-report.txt");

        Files.writeString(
                input,
                "Дюна;1;100.00;10;120\n"
                        + ";2;180.00;50;120\n"
                        + "Матриця;abc;150.00;20;136\n"
                        + "Барбі;2;-100.00;15;114\n"
                        + "Аватар;1;200.00;5;192\n",
                StandardCharsets.UTF_8
        );

        Main.main(new String[]{
                "--input", input.toString(),
                "--output", output.toString()
        });

        String report = Files.readString(output, StandardCharsets.UTF_8);

        assertTrue(report.contains("Правильних записів: 2"));
        assertTrue(report.contains("Неправильних записів: 3"));
        assertTrue(report.contains("Загальний виторг: 2000.00 грн"));
        assertTrue(report.contains("Середня ціна квитка: 150.00 грн"));
        assertTrue(report.contains(
                "Максимальна кількість проданих квитків: 10"
        ));
    }

    @Test
    void emptyCsvProducesZeroStatistics() throws Exception {
        Path input = tempDir.resolve("empty-input.csv");
        Path output = tempDir.resolve("empty-report.txt");

        Files.writeString(input, "", StandardCharsets.UTF_8);

        Main.main(new String[]{
                "--input", input.toString(),
                "--output", output.toString()
        });

        String report = Files.readString(output, StandardCharsets.UTF_8);

        assertTrue(report.contains("Правильних записів: 0"));
        assertTrue(report.contains("Неправильних записів: 0"));
        assertTrue(report.contains("Загальний виторг: 0.00 грн"));
        assertTrue(report.contains("Середня ціна квитка: 0.00 грн"));
        assertTrue(report.contains(
                "Максимальна кількість проданих квитків: 0"
        ));
    }

    @Test
    void missingInputFileDoesNotCreateReport() {
        Path input = tempDir.resolve("missing.csv");
        Path output = tempDir.resolve("missing-report.txt");

        PrintStream originalErr = System.err;
        ByteArrayOutputStream errorOutput = new ByteArrayOutputStream();

        try {
            System.setErr(
                    new PrintStream(
                            errorOutput,
                            true,
                            StandardCharsets.UTF_8
                    )
            );

            Main.main(new String[]{
                    "--input", input.toString(),
                    "--output", output.toString()
            });

            String errorMessage =
                    errorOutput.toString(StandardCharsets.UTF_8);

            assertTrue(
                    errorMessage.contains("Помилка роботи з файлом:")
            );
            assertFalse(Files.exists(output));
        } finally {
            System.setErr(originalErr);
        }
    }

    @Test
    void inputArgumentRejectsAnotherOptionAsPath() {
        Path output = tempDir.resolve("unexpected-report.txt");

        PrintStream originalErr = System.err;
        ByteArrayOutputStream errorOutput = new ByteArrayOutputStream();

        try {
            System.setErr(
                    new PrintStream(
                            errorOutput,
                            true,
                            StandardCharsets.UTF_8
                    )
            );

            Main.main(new String[]{
                    "--output", output.toString(),
                    "--input", "--version"
            });

            String errorMessage =
                    errorOutput.toString(StandardCharsets.UTF_8);

            assertTrue(
                    errorMessage.contains(
                            "після --input потрібно вказати шлях"
                    )
            );
            assertFalse(Files.exists(output));
        } finally {
            System.setErr(originalErr);
        }
    }

    @Test
    void outputArgumentRejectsAnotherOptionAsPath() {
        Path output = tempDir.resolve("unexpected-report.txt");

        PrintStream originalErr = System.err;
        ByteArrayOutputStream errorOutput = new ByteArrayOutputStream();

        try {
            System.setErr(
                    new PrintStream(
                            errorOutput,
                            true,
                            StandardCharsets.UTF_8
                    )
            );

            Main.main(new String[]{
                    "--output", "--input",
                    "--input", "data/input.csv"
            });

            String errorMessage =
                    errorOutput.toString(StandardCharsets.UTF_8);

            assertTrue(
                    errorMessage.contains(
                            "після --output потрібно вказати шлях"
                    )
            );
            assertFalse(Files.exists(output));
        } finally {
            System.setErr(originalErr);
        }
    }
}