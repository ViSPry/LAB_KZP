package ua.lpnu.kzp;

import java.nio.file.Path;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;

public final class CinemaController {

    public void run(String[] args) {
        CommandLineParser.Options options = CommandLineParser.parse(args);

        switch (options.mode()) {
            case HELP -> printHelp();
            case VERSION -> System.out.println("lab01 version " + getVersion());
            case STREAMS -> StreamDemoService.run();
            case STORAGE -> new StorageController().run();
            case REPORT -> runReport(options.inputPath(), options.outputPath());
            case ERROR -> {
                // Повідомлення про помилку вже вивів CommandLineParser.
            }
        }
    }

    private void runReport(Path inputPath, Path outputPath) {
        new CinemaReportService().generate(inputPath, outputPath);
    }
    private String getVersion() {
        Package appPackage = Main.class.getPackage();
        String version = appPackage.getImplementationVersion();
        return version != null ? version : "development";
    }

    private void printHelp() {
        System.out.println(
                "Використання: java -jar lab01-" + getVersion() + ".jar [параметри]"
        );
        System.out.println();
        System.out.println("Параметри:");
        System.out.println("  --help           Показати довідку");
        System.out.println("  --version        Показати версію програми");
        System.out.println("  --streams        Демонстрація Stream API");
        System.out.println("  --storage        Сховище кіносеансів (ЛР №5)");
        System.out.println("  --input ШЛЯХ     Вказати вхідний CSV-файл");
        System.out.println("  --output ШЛЯХ    Вказати шлях для збереження звіту");
        System.out.println();
        System.out.println("Стандартний вхідний файл: data/input.csv");
        System.out.println("Стандартний вихідний файл: out/report.txt");
    }
}