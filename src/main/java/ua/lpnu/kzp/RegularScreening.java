
package ua.lpnu.kzp;

import java.time.LocalTime;

/**
 * Представляє звичайний сеанс кінотеатру.
 *
 * <p>Виторг дорівнює добутку ціни квитка
 * на кількість проданих квитків.</p>
 */
public final class RegularScreening extends Screening {

    /**
     * Створює звичайний сеанс без явно заданого часу.
     *
     * @param film назва фільму
     * @param hall номер залу
     * @param ticketPrice ціна квитка
     * @param sold кількість проданих квитків
     * @param durationMin тривалість фільму
     */
    public RegularScreening(
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
     * Створює звичайний сеанс із часом початку.
     *
     * @param film назва фільму
     * @param hall номер залу
     * @param ticketPrice ціна квитка
     * @param sold кількість проданих квитків
     * @param durationMin тривалість фільму
     * @param startTime час початку сеансу
     * @throws IllegalArgumentException якщо дані некоректні
     */
    public RegularScreening(
            String film,
            int hall,
            double ticketPrice,
            int sold,
            int durationMin,
            LocalTime startTime
    ) {
        super(
                film,
                hall,
                ticketPrice,
                sold,
                durationMin,
                startTime
        );

        validateCommon(
                film,
                hall,
                ticketPrice,
                sold,
                durationMin,
                startTime
        );
    }

    /**
     * Обчислює виторг звичайного сеансу.
     *
     * @return виторг
     */
    @Override
    public double revenue() {
        return getTicketPrice() * getSold();
    }

    /**
     * Повертає категорію звичайного сеансу.
     *
     * @return REGULAR
     */
    @Override
    public ScreeningKind getKind() {
        return ScreeningKind.REGULAR;
    }

    /**
     * Звичайний сеанс не має додаткового стану.
     *
     * @param other інший сеанс
     * @return завжди true
     */
    @Override
    protected boolean hasSameSubtypeState(Screening other) {
        return true;
    }

    /**
     * Звичайний сеанс не має додаткової складової хеш-коду.
     *
     * @return null
     */
    @Override
    protected Object subtypeHashComponent() {
        return null;
    }
}
