
package ua.lpnu.kzp;

import java.time.LocalTime;
import java.util.Locale;
import java.util.Objects;

/**
 * Базовий тип для сеансів кінотеатру.
 *
 * <p>Зберігає спільний незмінний стан.
 * Перевірка аргументів виконується у кінцевих підкласах.</p>
 */
public abstract sealed class Screening
        permits RegularScreening, PremiumScreening {

    private final String film;
    private final int hall;
    private final double ticketPrice;
    private final int sold;
    private final int durationMin;
    private final LocalTime startTime;

    /**
     * Конструктор для сумісності з попередніми лабораторними.
     */
    protected Screening(
            String film,
            int hall,
            double ticketPrice,
            int sold,
            int durationMin
    ) {
        this(
                film,
                hall,
                ticketPrice,
                sold,
                durationMin,
                LocalTime.MIDNIGHT
        );
    }

    /**
     * Зберігає дані без перевірок, що можуть викинути виняток.
     *
     * <p>Конкретні final-підкласи зобов'язані
     * перевірити аргументи конструктора.</p>
     */
    protected Screening(
            String film,
            int hall,
            double ticketPrice,
            int sold,
            int durationMin,
            LocalTime startTime
    ) {
        this.film = film;
        this.hall = hall;
        this.ticketPrice = ticketPrice;
        this.sold = sold;
        this.durationMin = durationMin;
        this.startTime = startTime;
    }

    /**
     * Перевіряє спільні інваріанти предметної моделі.
     *
     * <p>Викликається конструкторами final-підкласів.</p>
     *
     * @throws IllegalArgumentException якщо дані некоректні
     */
    protected static void validateCommon(
            String film,
            int hall,
            double ticketPrice,
            int sold,
            int durationMin,
            LocalTime startTime
    ) {
        if (film == null || film.isBlank()) {
            throw new IllegalArgumentException(
                    "назва фільму порожня"
            );
        }

        if (hall <= 0) {
            throw new IllegalArgumentException(
                    "номер залу має бути додатним"
            );
        }

        if (!Double.isFinite(ticketPrice) || ticketPrice < 0) {
            throw new IllegalArgumentException(
                    "ціна квитка має бути невід'ємною"
            );
        }

        if (sold < 0) {
            throw new IllegalArgumentException(
                    "кількість проданих квитків не може бути від'ємною"
            );
        }

        if (durationMin <= 0) {
            throw new IllegalArgumentException(
                    "тривалість фільму має бути додатною"
            );
        }

        if (startTime == null) {
            throw new IllegalArgumentException(
                    "час початку сеансу не може бути null"
            );
        }
    }

    /**
     * Створює сеанс із CSV-рядка старого формату.
     *
     * @param line CSV-рядок
     * @return конкретний підтип Screening
     */
    public static Screening fromCsv(String line) {
        if (line == null) {
            throw new IllegalArgumentException(
                    "рядок CSV не може бути null"
            );
        }

        String[] parts = line.split(";", -1);

        if (parts.length != 5) {
            throw new IllegalArgumentException(
                    "має бути рівно 5 полів"
            );
        }

        try {
            String film = parts[0].trim();
            int hall = Integer.parseInt(parts[1].trim());
            double ticketPrice =
                    Double.parseDouble(parts[2].trim());
            int sold = Integer.parseInt(parts[3].trim());
            int durationMin =
                    Integer.parseInt(parts[4].trim());

            if (hall == 1) {
                return new PremiumScreening(
                        film, hall, ticketPrice, sold, durationMin
                );
            }

            return new RegularScreening(
                    film, hall, ticketPrice, sold, durationMin
            );
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "неправильний числовий формат",
                    e
            );
        }
    }

    /**
     * Обчислює виторг сеансу.
     *
     * @return виторг
     */
    public abstract double revenue();

    /**
     * Повертає категорію сеансу.
     *
     * @return категорія
     */
    public abstract ScreeningKind getKind();

    /**
     * Порівнює специфічний стан підтипу.
     *
     * @param other інший сеанс
     * @return результат порівняння
     */
    protected abstract boolean hasSameSubtypeState(
            Screening other
    );

    /**
     * Повертає специфічну складову хеш-коду.
     *
     * @return складова хеш-коду
     */
    protected abstract Object subtypeHashComponent();

    /** @return назва фільму */
    public String getFilm() {
        return film;
    }

    /** @return номер залу */
    public int getHall() {
        return hall;
    }

    /** @return ціна квитка */
    public double getTicketPrice() {
        return ticketPrice;
    }

    /** @return кількість проданих квитків */
    public int getSold() {
        return sold;
    }

    /** @return тривалість фільму */
    public int getDurationMin() {
        return durationMin;
    }

    /** @return час початку сеансу */
    public LocalTime getStartTime() {
        return startTime;
    }

    /**
     * Порівнює два сеанси за всіма значущими полями.
     *
     * @param object об'єкт для порівняння
     * @return true, якщо об'єкти рівні
     */
    @Override
    public final boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (object == null
                || getClass() != object.getClass()) {
            return false;
        }

        Screening other = (Screening) object;

        return hall == other.hall
                && Double.compare(
                        ticketPrice, other.ticketPrice
                ) == 0
                && sold == other.sold
                && durationMin == other.durationMin
                && film.equals(other.film)
                && startTime.equals(other.startTime)
                && hasSameSubtypeState(other);
    }

    /**
     * Повертає хеш-код сеансу.
     *
     * @return хеш-код
     */
    @Override
    public final int hashCode() {
        return Objects.hash(
                getClass(),
                film,
                hall,
                ticketPrice,
                sold,
                durationMin,
                startTime,
                subtypeHashComponent()
        );
    }

    /**
     * Повертає текстове представлення сеансу.
     *
     * @return текстове представлення
     */
    @Override
    public String toString() {
        return String.format(
                Locale.ROOT,
                "%s{film='%s', hall=%d, ticketPrice=%.2f, sold=%d, durationMin=%d, startTime=%s}",
                getClass().getSimpleName(),
                film,
                hall,
                ticketPrice,
                sold,
                durationMin,
                startTime
        );
    }
}
