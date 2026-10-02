package ua.lpnu.kzp;

/**
 * Незмінний підсумок статистики продажів квитків кінотеатру.
 *
 * <p>Record використовується як допоміжний об'єкт-значення
 * для передачі обчислених показників звіту. Компоненти record
 * автоматично є незмінними після створення об'єкта.</p>
 *
 * @param validCount кількість правильних записів
 * @param invalidCount кількість неправильних записів
 * @param totalRevenue загальний виторг
 * @param averageTicketPrice середня ціна квитка
 * @param maxSold максимальна кількість проданих квитків
 */
public record TicketSales(
        int validCount,
        int invalidCount,
        double totalRevenue,
        double averageTicketPrice,
        int maxSold
) {

    /**
     * Перевіряє інваріанти статистики під час створення record.
     *
     * @throws IllegalArgumentException якщо будь-який показник
     *                                  має некоректне значення
     */
    public TicketSales {
        if (validCount < 0) {
            throw new IllegalArgumentException(
                    "Кількість правильних записів не може бути від'ємною"
            );
        }

        if (invalidCount < 0) {
            throw new IllegalArgumentException(
                    "Кількість неправильних записів не може бути від'ємною"
            );
        }

        if (!Double.isFinite(totalRevenue) || totalRevenue < 0) {
            throw new IllegalArgumentException(
                    "Загальний виторг має бути скінченним та невід'ємним"
            );
        }

        if (!Double.isFinite(averageTicketPrice)
                || averageTicketPrice < 0) {
            throw new IllegalArgumentException(
                    "Середня ціна має бути скінченною та невід'ємною"
            );
        }

        if (maxSold < 0) {
            throw new IllegalArgumentException(
                    "Максимальна кількість проданих квитків "
                            + "не може бути від'ємною"
            );
        }
    }
}