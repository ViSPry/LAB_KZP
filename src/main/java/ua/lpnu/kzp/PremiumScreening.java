package ua.lpnu.kzp;

/**
 * Представляє преміальний сеанс кінотеатру.
 *
 * <p>Клас демонструє окрему реалізацію поліморфної операції
 * обчислення виторгу. Додаткова плата задається для кожного
 * проданого квитка.</p>
 */
public final class PremiumScreening extends Screening {

    private final double premiumFee;

    /**
     * Створює преміальний сеанс без додаткової плати.
     *
     * <p>Цей конструктор використовується під час читання старого
     * CSV-формату та дозволяє зберегти результати попередньої
     * лабораторної роботи.</p>
     *
     * @param film назва фільму
     * @param hall номер залу
     * @param ticketPrice ціна квитка
     * @param sold кількість проданих квитків
     * @param durationMin тривалість фільму у хвилинах
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
                0.0
        );
    }

    /**
     * Створює преміальний сеанс із додатковою платою за квиток.
     *
     * @param film назва фільму
     * @param hall номер залу
     * @param ticketPrice базова ціна квитка
     * @param sold кількість проданих квитків
     * @param durationMin тривалість фільму у хвилинах
     * @param premiumFee додаткова плата за один квиток
     * @throws IllegalArgumentException якщо дані не проходять валідацію
     */
    public PremiumScreening(
            String film,
            int hall,
            double ticketPrice,
            int sold,
            int durationMin,
            double premiumFee
    ) {
        super(
                film,
                hall,
                ticketPrice,
                sold,
                durationMin
        );

        if (!Double.isFinite(premiumFee) || premiumFee < 0) {
            throw new IllegalArgumentException(
                    "додаткова плата має бути невід'ємною"
            );
        }

        this.premiumFee = premiumFee;
    }

    /**
     * @return додаткова плата за один преміальний квиток
     */
    public double getPremiumFee() {
        return premiumFee;
    }

    /**
     * Обчислює виторг преміального сеансу.
     *
     * <p>На відміну від звичайного сеансу, до базової ціни
     * кожного проданого квитка додається преміальна плата.</p>
     *
     * @return виторг преміального сеансу
     */
    @Override
    public double revenue() {
        return (getTicketPrice() + premiumFee) * getSold();
    }

    /**
     * Повертає категорію сеансу.
     *
     * @return {@link ScreeningKind#PREMIUM}
     */
    @Override
    public ScreeningKind getKind() {
        return ScreeningKind.PREMIUM;
    }
}