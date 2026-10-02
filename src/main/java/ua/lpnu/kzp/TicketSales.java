package ua.lpnu.kzp;

public record TicketSales(
        int validCount,
        int invalidCount,
        double totalRevenue,
        double averageTicketPrice,
        int maxSold
) {

    public TicketSales {
        if (validCount < 0) {
            throw new IllegalArgumentException(
                    "Кількість правильних записів не може бути від'ємною");
        }

        if (invalidCount < 0) {
            throw new IllegalArgumentException(
                    "Кількість неправильних записів не може бути від'ємною");
        }

        if (!Double.isFinite(totalRevenue) || totalRevenue < 0) {
            throw new IllegalArgumentException(
                    "Загальний виторг має бути скінченним та невід'ємним");
        }

        if (!Double.isFinite(averageTicketPrice) || averageTicketPrice < 0) {
            throw new IllegalArgumentException(
                    "Середня ціна має бути скінченною та невід'ємною");
        }

        if (maxSold < 0) {
            throw new IllegalArgumentException(
                    "Максимальна кількість проданих квитків не може бути від'ємною");
        }
    }
}