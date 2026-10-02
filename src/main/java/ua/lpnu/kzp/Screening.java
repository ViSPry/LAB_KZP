package ua.lpnu.kzp;

public final class Screening {

    private final String film;
    private final int hall;
    private final double ticketPrice;
    private final int sold;
    private final int durationMin;

    public Screening(String film, int hall, double ticketPrice,
                     int sold, int durationMin) {

        if (film == null || film.isBlank()) {
            throw new IllegalArgumentException(
                    "назва фільму порожня");
        }

        if (hall <= 0) {
            throw new IllegalArgumentException(
                    "номер залу має бути додатним");
        }

        if (!Double.isFinite(ticketPrice) || ticketPrice < 0) {
            throw new IllegalArgumentException(
                    "ціна квитка має бути невід'ємною");
        }

        if (sold < 0) {
            throw new IllegalArgumentException(
                    "кількість проданих квитків не може бути від'ємною");
        }

        if (durationMin <= 0) {
            throw new IllegalArgumentException(
                    "тривалість фільму має бути додатною");
        }

        this.film = film;
        this.hall = hall;
        this.ticketPrice = ticketPrice;
        this.sold = sold;
        this.durationMin = durationMin;
    }

    public static Screening fromCsv(String line) {
        if (line == null) {
            throw new IllegalArgumentException(
                    "рядок CSV не може бути null");
        }

        String[] parts = line.split(";", -1);

        if (parts.length != 5) {
            throw new IllegalArgumentException(
                    "має бути рівно 5 полів");
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
                    "неправильний числовий формат", e);
        }
    }

    public String getFilm() {
        return film;
    }

    public int getHall() {
        return hall;
    }

    public double getTicketPrice() {
        return ticketPrice;
    }

    public int getSold() {
        return sold;
    }

    public int getDurationMin() {
        return durationMin;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private String film;
        private int hall;
        private double ticketPrice;
        private int sold;
        private int durationMin;

        private Builder() {
        }

        public Builder film(String film) {
            this.film = film;
            return this;
        }

        public Builder hall(int hall) {
            this.hall = hall;
            return this;
        }

        public Builder ticketPrice(double ticketPrice) {
            this.ticketPrice = ticketPrice;
            return this;
        }

        public Builder sold(int sold) {
            this.sold = sold;
            return this;
        }

        public Builder durationMin(int durationMin) {
            this.durationMin = durationMin;
            return this;
        }

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