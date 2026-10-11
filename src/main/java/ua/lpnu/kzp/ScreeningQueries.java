
package ua.lpnu.kzp;

import java.time.LocalTime;
import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Запити до колекції сеансів кінотеатру засобами Stream API.
 *
 * <p>Усі операції не змінюють вхідну колекцію.
 * Результати спискових операцій створюються через
 * {@link java.util.stream.Stream#toList()}.</p>
 */
public final class ScreeningQueries {

    private ScreeningQueries() {
    }

    /**
     * Відбирає сеанси, на які продано більше квитків,
     * ніж заданий поріг.
     *
     * @param screenings колекція сеансів
     * @param threshold поріг кількості квитків
     * @return список сеансів, де sold строго більше threshold
     */
    public static List<Screening> filterBySold(
            List<Screening> screenings,
            int threshold
    ) {
        Objects.requireNonNull(screenings, "screenings");

        return screenings.stream()
                .filter(screening -> screening.getSold() > threshold)
                .toList();
    }

    /**
     * Перетворює сеанси на список назв фільмів.
     *
     * <p>Повторні назви зберігаються, оскільки різні
     * сеанси можуть показувати той самий фільм.</p>
     *
     * @param screenings колекція сеансів
     * @return назви фільмів у початковому порядку
     */
    public static List<String> filmNames(
            List<Screening> screenings
    ) {
        Objects.requireNonNull(screenings, "screenings");

        return screenings.stream()
                .map(Screening::getFilm)
                .toList();
    }

    /**
     * Групує сеанси за номером залу та обчислює
     * загальний виторг кожного залу.
     *
     * <p>Використовується поліморфний метод
     * {@link Screening#revenue()}, тому враховується
     * додаткова плата преміальних сеансів.</p>
     *
     * @param screenings колекція сеансів
     * @return номер залу та сумарний виторг
     */
    public static Map<Integer, Double> revenueByHall(
            List<Screening> screenings
    ) {
        Objects.requireNonNull(screenings, "screenings");

        return screenings.stream()
                .collect(Collectors.groupingBy(
                        Screening::getHall,
                        Collectors.summingDouble(Screening::revenue)
                ));
    }

    /**
     * Обчислює статистику базової ціни квитка:
     * кількість, суму, мінімум, максимум і середнє.
     *
     * @param screenings колекція сеансів
     * @return статистика ціни квитка
     */
    public static DoubleSummaryStatistics ticketPriceStatistics(
            List<Screening> screenings
    ) {
        Objects.requireNonNull(screenings, "screenings");

        return screenings.stream()
                .collect(Collectors.summarizingDouble(
                        Screening::getTicketPrice
                ));
    }

    /**
     * Повертає N найпопулярніших сеансів.
     *
     * <p>Основне сортування: кількість проданих
     * квитків за спаданням. Додаткове сортування:
     * час початку сеансу за зростанням.</p>
     *
     * @param screenings колекція сеансів
     * @param n максимальна кількість результатів
     * @return відсортований список довжиною не більше n
     * @throws IllegalArgumentException якщо n від'ємне
     */
    public static List<Screening> topBySoldAndTime(
            List<Screening> screenings,
            int n
    ) {
        Objects.requireNonNull(screenings, "screenings");

        if (n < 0) {
            throw new IllegalArgumentException(
                    "кількість результатів не може бути від'ємною"
            );
        }

        Comparator<Screening> comparator =
                Comparator.comparingInt(Screening::getSold)
                        .reversed()
                        .thenComparing(Screening::getStartTime);

        return screenings.stream()
                .sorted(comparator)
                .limit(n)
                .toList();
    }

    /**
     * Шукає перший сеанс за назвою фільму та часом початку.
     *
     * <p>Якщо кілька сеансів відповідають умовам,
     * повертається перший у порядку вхідної колекції.</p>
     *
     * @param screenings колекція сеансів
     * @param film назва фільму
     * @param startTime час початку
     * @return Optional із знайденим сеансом або порожній
     */
    public static Optional<Screening> findByFilmAndTime(
            List<Screening> screenings,
            String film,
            LocalTime startTime
    ) {
        Objects.requireNonNull(screenings, "screenings");
        Objects.requireNonNull(film, "film");
        Objects.requireNonNull(startTime, "startTime");

        return screenings.stream()
                .filter(screening ->
                        screening.getFilm().equals(film)
                                && screening.getStartTime()
                                        .equals(startTime)
                )
                .findFirst();
    }

    /**
     * Додаткове завдання: дворівневе групування
     * за номером залу та категорією сеансу.
     *
     * @param screenings колекція сеансів
     * @return зал -> категорія -> кількість сеансів
     */
    public static Map<Integer, Map<ScreeningKind, Long>>
            countByHallAndKind(List<Screening> screenings) {
        Objects.requireNonNull(screenings, "screenings");

        return screenings.stream()
                .collect(Collectors.groupingBy(
                        Screening::getHall,
                        Collectors.groupingBy(
                                Screening::getKind,
                                Collectors.counting()
                        )
                ));
    }
}
