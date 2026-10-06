package ua.lpnu.kzp;

import java.util.Locale;
import java.util.Objects;

/**
 * Базовий тип для сеансів кінотеатру.
 *
 * <p>Клас містить спільний стан та правила валідації для всіх
 * типів сеансів. Конкретний спосіб обчислення виторгу визначається
 * у підкласах через метод {@link #revenue()}.</p>
 *
 * <p>Об'єкти є незмінними: усі поля оголошені як
 * {@code private final}, а публічні сеттери відсутні.</p>
 */
public abstract class Screening {

    private final String film;
    private final int hall;
    private final double ticketPrice;
    private final int sold;
    private final int durationMin;

    /**
     * Створює базову частину сеансу кінотеатру.
     *
     * @param film назва фільму
     * @param hall номер залу
     * @param ticketPrice ціна квитка
     * @param sold кількість проданих квитків
     * @param durationMin тривалість фільму у хвилинах
     * @throws IllegalArgumentException якщо передані дані
     *                                  не відповідають правилам валідації
     */
    protected Screening(
            String film,
            int hall,
            double ticketPrice,
            int sold,
            int durationMin
    ) {
        this(validate(
                film,
                hall,
                ticketPrice,
                sold,
                durationMin
        ));
    }

    /**
     * Ініціалізує об'єкт уже перевіреними даними.
     *
     * @param data перевірені дані сеансу
     */
    private Screening(ValidatedData data) {
        this.film = data.film();
        this.hall = data.hall();
        this.ticketPrice = data.ticketPrice();
        this.sold = data.sold();
        this.durationMin = data.durationMin();
    }

    /**
     * Перевіряє дані до початку ініціалізації базового об'єкта.
     *
     * @param film назва фільму
     * @param hall номер залу
     * @param ticketPrice ціна квитка
     * @param sold кількість проданих квитків
     * @param durationMin тривалість фільму у хвилинах
     * @return перевірені дані
     * @throws IllegalArgumentException якщо дані некоректні
     */
    private static ValidatedData validate(
            String film,
            int hall,
            double ticketPrice,
            int sold,
            int durationMin
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

        return new ValidatedData(
                film,
                hall,
                ticketPrice,
                sold,
                durationMin
        );
    }

    /**
     * Створює конкретний сеанс з одного CSV-рядка.
     *
     * <p>Формат залишається таким самим, як у попередній
     * лабораторній роботі:
     * film;hall;ticketPrice;sold;durationMin.</p>
     *
     * <p>Тип сеансу визначається за номером залу:
     * зал 1 створює {@link PremiumScreening}, інші зали —
     * {@link RegularScreening}. Це дозволяє зберегти
     * попередній CSV-формат без додаткового поля.</p>
     *
     * @param line CSV-рядок
     * @return конкретний підтип {@code Screening}
     * @throws IllegalArgumentException якщо рядок має неправильну
     *                                  структуру або некоректні дані
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
            double ticketPrice = Double.parseDouble(parts[2].trim());
            int sold = Integer.parseInt(parts[3].trim());
            int durationMin = Integer.parseInt(parts[4].trim());

            if (hall == 1) {
                return new PremiumScreening(
                        film,
                        hall,
                        ticketPrice,
                        sold,
                        durationMin
                );
            }

            return new RegularScreening(
                    film,
                    hall,
                    ticketPrice,
                    sold,
                    durationMin
            );
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "неправильний числовий формат",
                    e
            );
        }
    }

    /**
     * Обчислює виторг конкретного типу сеансу.
     *
     * @return виторг сеансу
     */
    public abstract double revenue();

    /**
     * Повертає категорію сеансу.
     *
     * @return категорія сеансу
     */
    public abstract ScreeningKind getKind();

    /**
     * Порівнює специфічний стан конкретного підтипу.
     *
     * @param other інший об'єкт того самого конкретного підтипу
     * @return {@code true}, якщо специфічний стан збігається
     */
    protected abstract boolean hasSameSubtypeState(Screening other);

    /**
     * Повертає складову хеш-коду, специфічну для підтипу.
     *
     * @return специфічна складова хеш-коду
     */
    protected abstract Object subtypeHashComponent();

    /**
     * @return назва фільму
     */
    public String getFilm() {
        return film;
    }

    /**
     * @return номер залу
     */
    public int getHall() {
        return hall;
    }

    /**
     * @return ціна квитка
     */
    public double getTicketPrice() {
        return ticketPrice;
    }

    /**
     * @return кількість проданих квитків
     */
    public int getSold() {
        return sold;
    }

    /**
     * @return тривалість фільму у хвилинах
     */
    public int getDurationMin() {
        return durationMin;
    }

    /**
     * Порівнює два сеанси за спільним та специфічним станом.
     *
     * <p>Об'єкти різних конкретних підтипів не вважаються рівними,
     * навіть якщо значення їхніх спільних полів збігаються.</p>
     *
     * @param object об'єкт для порівняння
     * @return {@code true}, якщо об'єкти логічно рівні
     */
    @Override
    public final boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        Screening other = (Screening) object;

        return hall == other.hall
                && Double.compare(ticketPrice, other.ticketPrice) == 0
                && sold == other.sold
                && durationMin == other.durationMin
                && film.equals(other.film)
                && hasSameSubtypeState(other);
    }

    /**
     * Повертає хеш-код, узгоджений з {@link #equals(Object)}.
     *
     * @return хеш-код сеансу
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
                subtypeHashComponent()
        );
    }

    /**
     * Повертає текстове представлення спільної частини сеансу.
     *
     * @return текстове представлення об'єкта
     */
    @Override
    public String toString() {
        return String.format(
                Locale.ROOT,
                "%s{film='%s', hall=%d, ticketPrice=%.2f, sold=%d, durationMin=%d}",
                getClass().getSimpleName(),
                film,
                hall,
                ticketPrice,
                sold,
                durationMin
        );
    }

    /**
     * Внутрішнє незмінне представлення вже перевірених даних.
     *
     * @param film назва фільму
     * @param hall номер залу
     * @param ticketPrice ціна квитка
     * @param sold кількість проданих квитків
     * @param durationMin тривалість фільму у хвилинах
     */
    private record ValidatedData(
            String film,
            int hall,
            double ticketPrice,
            int sold,
            int durationMin
    ) {
    }
}