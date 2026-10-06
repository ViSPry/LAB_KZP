package ua.lpnu.kzp;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ScreeningHierarchyTest {

    @Test
    void regularScreeningCalculatesRevenue() {
        Screening screening = new RegularScreening(
                "Матриця",
                2,
                150.0,
                50,
                136
        );

        assertEquals(7500.0, screening.revenue());
        assertEquals(ScreeningKind.REGULAR, screening.getKind());
    }

    @Test
    void premiumScreeningCalculatesRevenueWithFee() {
        Screening screening = new PremiumScreening(
                "Дюна 2",
                1,
                200.0,
                100,
                166,
                25.0
        );

        assertEquals(22500.0, screening.revenue());
        assertEquals(ScreeningKind.PREMIUM, screening.getKind());
    }

    @Test
    void premiumScreeningAcceptsZeroFee() {
        PremiumScreening screening = new PremiumScreening(
                "Оппенгеймер",
                1,
                220.0,
                120,
                180,
                0.0
        );

        assertEquals(26400.0, screening.revenue());
        assertEquals(0.0, screening.getPremiumFee());
    }

    @Test
    void premiumScreeningRejectsNegativeFee() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new PremiumScreening(
                        "Фільм",
                        1,
                        100.0,
                        10,
                        120,
                        -1.0
                )
        );
    }

    @Test
    void premiumScreeningRejectsNonFiniteFee() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new PremiumScreening(
                        "Фільм",
                        1,
                        100.0,
                        10,
                        120,
                        Double.NaN
                )
        );
    }

    @Test
    void heterogeneousCollectionUsesPolymorphism() {
        List<Screening> screenings = List.of(
                new RegularScreening(
                        "Матриця",
                        2,
                        100.0,
                        10,
                        120
                ),
                new PremiumScreening(
                        "Дюна 2",
                        1,
                        100.0,
                        10,
                        120,
                        20.0
                )
        );

        double totalRevenue = 0.0;

        for (Screening screening : screenings) {
            totalRevenue += screening.revenue();
        }

        assertEquals(2200.0, totalRevenue);
    }

    @Test
    void equalRegularScreeningsAreEqual() {
        Screening first = new RegularScreening(
                "Матриця",
                2,
                150.0,
                50,
                136
        );

        Screening second = new RegularScreening(
                "Матриця",
                2,
                150.0,
                50,
                136
        );

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void differentScreeningSubtypesAreNotEqual() {
        Screening regular = new RegularScreening(
                "Фільм",
                1,
                100.0,
                10,
                120
        );

        Screening premium = new PremiumScreening(
                "Фільм",
                1,
                100.0,
                10,
                120
        );

        assertNotEquals(regular, premium);
    }

    @Test
    void hashSetDoesNotStoreDuplicateScreenings() {
        Set<Screening> screenings = new HashSet<>();

        screenings.add(
                new RegularScreening(
                        "Матриця",
                        2,
                        150.0,
                        50,
                        136
                )
        );

        screenings.add(
                new RegularScreening(
                        "Матриця",
                        2,
                        150.0,
                        50,
                        136
                )
        );

        assertEquals(1, screenings.size());
    }

    @Test
    void screeningKindProvidesReadableLabels() {
        assertEquals("Звичайний", ScreeningKind.REGULAR.getLabel());
        assertEquals("Преміальний", ScreeningKind.PREMIUM.getLabel());
    }
}