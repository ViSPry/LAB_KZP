package ua.lpnu.kzp;

import java.util.Locale;

/**
 * Представляє один сеанс кінотеатру.
 *
 * <p>Клас є незмінним: усі поля оголошені як {@code private final},
 * а публічні сеттери відсутні. Коректність даних перевіряється
 * під час створення об'єкта.</p>
 */
public final class Screening {

    private final String film;
    private final int hall;
    private final double ticketPrice;
    private final int sold;
    private final int durationMin;

    /**
     * Створює новий сеанс кінотеатру.
     *
     * @param film назва фільму
     * @param hall номер залу
     * @param ticketPrice ціна квитка
     * @param sold кількість проданих квитків
     * @param durationMin тривалість фільму у хвилинах
     * @throws IllegalArgumentException якщо передані дані
     *                                  не відповідають правилам валідації
     */
    public Screening(
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

        this.film = film;
        this.hall = hall;
        this.ticketPrice = ticketPrice;
        this.sold = sold;
        this.durationMin = durationMin;
    }

    /**
     * Створює об'єкт {@code Screening} з одного CSV-рядка.
     *
     * <p>Очікується п'ять полів у форматі:
     * film;hall;ticketPrice;sold;durationMin.</p>
     *
     * @param line CSV-рядок
     * @return створений об'єкт {@code Screening}
     * @throws IllegalArgumentException якщо рядок має неправильну
     *                                  структуру або містить некоректні дані
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

            return new Screening(
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
     * Створює Builder для покрокового формування об'єкта.
     *
     * @return новий Builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Повертає текстове представлення сеансу.
     *
     * <p>{@link Locale#ROOT} використовується для отримання
     * стабільного форматування незалежно від системної локалі.</p>
     *
     * @return текстове представлення об'єкта
     */
    @Override
    public String toString() {
        return String.format(
                Locale.ROOT,
                "Screening{film='%s', hall=%d, "
                        + "ticketPrice=%.2f, sold=%d, durationMin=%d}",
                film,
                hall,
                ticketPrice,
                sold,
                durationMin
        );
    }

    /**
     * Builder для створення незмінного {@link Screening}.
     *
     * <p>Builder не обходить правила валідації:
     * метод {@link #build()} викликає основний конструктор
     * {@code Screening}.</p>
     */
    public static final class Builder {

        private String film;
        private int hall;
        private double ticketPrice;
        private int sold;
        private int durationMin;

        private Builder() {
        }

        /**
         * @param film назва фільму
         * @return поточний Builder
         */
        public Builder film(String film) {
            this.film = film;
            return this;
        }

        /**
         * @param hall номер залу
         * @return поточний Builder
         */
        public Builder hall(int hall) {
            this.hall = hall;
            return this;
        }

        /**
         * @param ticketPrice ціна квитка
         * @return поточний Builder
         */
        public Builder ticketPrice(double ticketPrice) {
            this.ticketPrice = ticketPrice;
            return this;
        }

        /**
         * @param sold кількість проданих квитків
         * @return поточний Builder
         */
        public Builder sold(int sold) {
            this.sold = sold;
            return this;
        }

        /**
         * @param durationMin тривалість фільму у хвилинах
         * @return поточний Builder
         */
        public Builder durationMin(int durationMin) {
            this.durationMin = durationMin;
            return this;
        }

        /**
         * Створює {@link Screening} через його основний конструктор.
         *
         * @return валідний незмінний об'єкт {@code Screening}
         * @throws IllegalArgumentException якщо значення Builder
         *                                  не проходять валідацію
         */
        public Screening build() {
            return new Screening(
                    film,
                    hall,
                    ticketPrice,
                    sold,
                    durationMin
            );
        }
    }
}