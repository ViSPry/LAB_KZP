package ua.lpnu.kzp;

import java.nio.file.Path;
import java.util.Scanner;

public final class StorageController {

    private final StorageService service = new StorageService();

    public void run() {
        Scanner scanner = new Scanner(System.in, java.nio.charset.StandardCharsets.UTF_8);

        while (true) {
            System.out.println();
            System.out.println("===== СХОВИЩЕ КІНОСЕАНСІВ =====");
            System.out.println("1. Імпортувати CSV");
            System.out.println("2. Переглянути всі сеанси");
            System.out.println("3. Знайти сеанси за назвою фільму");
            System.out.println("4. Експортувати CSV");
            System.out.println("5. Експортувати JSON");
            System.out.println("0. Вийти");
            System.out.print("Ваш вибір: ");

            if (!scanner.hasNextLine()) {
                return;
            }

            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                    case "1" -> {
                        System.out.print("Шлях до CSV: ");
                        int count = service.importCsv(
                                Path.of(scanner.nextLine().trim()));
                        System.out.println("Імпортовано записів: " + count);
                    }
                    case "2" -> {
                        service.all().forEach(System.out::println);
                        System.out.println(
                                "Усього записів: " + service.all().size());
                    }
                    case "3" -> {
                        System.out.print("Назва фільму: ");
                        String film = scanner.nextLine().trim();
                        service.findByFilm(film)
                                .forEach(System.out::println);
                    }
                    case "4" -> {
                        System.out.print("Шлях для CSV: ");
                        Path path = Path.of(scanner.nextLine().trim());
                        service.exportCsv(path);
                        System.out.println("CSV збережено: " + path);
                    }
                    case "5" -> {
                        System.out.print("Шлях для JSON: ");
                        Path path = Path.of(scanner.nextLine().trim());
                        service.exportJson(path);
                        System.out.println("JSON збережено: " + path);
                    }
                    case "0" -> {
                        return;
                    }
                    default -> System.out.println("Невідомий пункт меню.");
                }
            } catch (DataStorageException | IllegalArgumentException exception) {
                System.err.println("Помилка: " + exception.getMessage());
            }
        }
    }
}
