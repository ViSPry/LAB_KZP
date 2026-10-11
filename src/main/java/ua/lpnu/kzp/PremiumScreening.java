
package ua.lpnu.kzp;

import java.time.LocalTime;

/**
 * Представляє преміальний сеанс кінотеатру.
 *
 * <p>Виторг враховує базову ціну квитка
 * та додаткову преміальну плату.</p>
 */
public final class PremiumScreening extends Screening {

    private final double premiumFee;

    /**
     * Створює преміальний сеанс без додаткової плати.
     */
    public PremiumScreening(
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
                0.0,
                LocalTime.MIDNIGHT
        );
    }

    /**
     * Створює преміальний сеанс із додатковою платою.
     */
    public PremiumScreening(
            String film,
            int hall,
            double ticketPrice,
            int sold,
            int durationMin,
            double premiumFee
    ) {
        this(
                film,
                hall,
                ticketPrice,
                sold,
                durationMin,
                premiumFee,
                LocalTime.MIDNIGHT
        );
    }

    /**
     * Створює преміальний сеанс із часом початку.
     */
    public PremiumScreening(
            String film,
            int hall,
            double ticketPrice,
            int sold,
            int durationMin,
            LocalTime startTime
    ) {
        this(
                film,
                hall,
                ticketPrice,
                sold,
                durationMin,
                0.0,
                startTime
        );
    }

    /**
     * Створює преміальний сеанс із додатковою платою
     * та часом початку.
     *
     * @param film назва фільму
     * @param hall номер залу
     * @param ticketPrice базова ціна квитка
     * @param sold кількість проданих квитків
     * @param durationMin тривалість фільму
     * @param premiumFee додаткова плата за квиток
     * @param startTime час початку сеансу
     * @throws IllegalArgumentException якщо дані некоректні
     */
    public PremiumScreening(
            String film,
            int hall,
            double ticketPrice,
            int sold,
            int durationMin,
            double premiumFee,
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

        if (!Double.isFinite(premiumFee)
                || premiumFee < 0) {
            throw new IllegalArgumentException(
                    "додаткова плата має бути невід'ємною"
            );
        }

        this.premiumFee = premiumFee;
    }

    /**
     * Повертає додаткову плату за преміальний квиток.
     *
     * @return додаткова плата
     */
    public double getPremiumFee() {
        return premiumFee;
    }

    /**
     * Обчислює виторг преміального сеансу.
     *
     * @return виторг
     */
    @Override
    public double revenue() {
        return (getTicketPrice() + premiumFee) * getSold();
    }

    /**
     * Повертає категорію преміального сеансу.
     *
     * @return PREMIUM
     */
    @Override
    public ScreeningKind getKind() {
        return ScreeningKind.PREMIUM;
    }

    /**
     * Порівнює додатковий стан преміальних сеансів.
     *
     * @param other інший сеанс
     * @return true, якщо додаткова плата збігається
     */
    @Override
    protected boolean hasSameSubtypeState(Screening other) {
        PremiumScreening premium = (PremiumScreening) other;

        return Double.compare(
                premiumFee,
                premium.premiumFee
        ) == 0;
    }

    /**
     * Повертає складову хеш-коду преміального сеансу.
     *
     * @return додаткова плата
     */
    @Override
    protected Object subtypeHashComponent() {
        return premiumFee;
    }
}
