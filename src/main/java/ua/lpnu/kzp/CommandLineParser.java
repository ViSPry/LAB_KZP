package ua.lpnu.kzp;

import java.nio.file.Path;

public final class CommandLineParser {

    public enum Mode {
        REPORT, HELP, VERSION, STREAMS, STORAGE, ERROR
    }

    public record Options(Mode mode, Path inputPath, Path outputPath) {
    }

    private CommandLineParser() {
    }

    public static Options parse(String[] args) {
        Path input = Path.of("data", "input.csv");
        Path output = Path.of("out", "report.txt");

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--help":
                    return new Options(Mode.HELP, input, output);

                case "--version":
                    return new Options(Mode.VERSION, input, output);

                case "--storage":
                    return new Options(Mode.STORAGE, input, output);

                case "--streams":
                    return new Options(Mode.STREAMS, input, output);

                case "--input":
                    if (i + 1 >= args.length
                            || args[i + 1].startsWith("--")) {
                        System.err.println(
                                "Помилка: після --input потрібно вказати шлях."
                        );
                        return new Options(Mode.ERROR, input, output);
                    }
                    input = Path.of(args[++i]);
                    break;

                case "--output":
                    if (i + 1 >= args.length
                            || args[i + 1].startsWith("--")) {
                        System.err.println(
                                "Помилка: після --output потрібно вказати шлях."
                        );
                        return new Options(Mode.ERROR, input, output);
                    }
                    output = Path.of(args[++i]);
                    break;

                default:
                    System.err.println(
                            "Невідомий параметр: %s".formatted(args[i])
                    );
                    System.err.println(
                            "Використайте --help для перегляду довідки."
                    );
                    return new Options(Mode.ERROR, input, output);
            }
        }

        return new Options(Mode.REPORT, input, output);
    }
}