package ua.lpnu.kzp;

/**
 * Представляє звичайний сеанс кінотеатру.
 *
 * <p>Виторг звичайного сеансу визначається як добуток
 * ціни одного квитка на кількість проданих квитків.</p>
 */
public final class RegularScreening extends Screening {

    /**
     * Створює звичайний сеанс.
     *
     * @param film назва фільму
     * @param hall номер залу
     * @param ticketPrice ціна квитка
     * @param sold кількість проданих квитків
     * @param durationMin тривалість фільму у хвилинах
     * @throws IllegalArgumentException якщо дані не проходять
     *                                  валідацію базового класу
     */
    public RegularScreening(
            String film,
            int hall,
            double ticketPrice,
            int sold,
            int durationMin
    ) {
        super(
                film,
                hall,
                ticketPrice,
                sold,
                durationMin
        );
    }

    /**
     * Обчислює виторг звичайного сеансу.
     *
     * @return ціна квитка, помножена на кількість проданих квитків
     */
    @Override
    public double revenue() {
        return getTicketPrice() * getSold();
    }

    /**
     * Повертає категорію сеансу.
     *
     * @return {@link ScreeningKind#REGULAR}
     */
    @Override
    public ScreeningKind getKind() {
        return ScreeningKind.REGULAR;
    }
}