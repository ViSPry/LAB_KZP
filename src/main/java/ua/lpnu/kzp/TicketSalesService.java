package ua.lpnu.kzp;

import java.util.List;

public final class TicketSalesService {

    private TicketSalesService() {
    }

    public static TicketSales calculateTicketSales(
            List<Screening> screenings,
            int invalidCount
    ) {
        double totalRevenue = screenings.stream()
                .mapToDouble(Screening::revenue)
                .sum();

        double averageTicketPrice = screenings.stream()
                .mapToDouble(Screening::getTicketPrice)
                .average()
                .orElse(0.0);

        int maxSold = screenings.stream()
                .mapToInt(Screening::getSold)
                .max()
                .orElse(0);

        int validCount = screenings.size();

        return new TicketSales(
                validCount,
                invalidCount,
                totalRevenue,
                averageTicketPrice,
                maxSold
        );
    }

    /**
     * Виводить довідку щодо запуску програми.
     */

}