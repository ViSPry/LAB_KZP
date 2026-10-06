package ua.lpnu.kzp;

/**
 * Визначає категорію сеансу кінотеатру.
 */
public enum ScreeningKind {

    /**
     * Звичайний сеанс.
     */
    REGULAR("Звичайний"),

    /**
     * Преміальний сеанс.
     */
    PREMIUM("Преміальний");

    private final String label;

    /**
     * Створює категорію з текстовою назвою.
     *
     * @param label текстова назва категорії
     */
    ScreeningKind(String label) {
        this.label = label;
    }

    /**
     * Повертає зрозумілу користувачеві назву категорії.
     *
     * @return текстова назва категорії
     */
    public String getLabel() {
        return label;
    }

    /**
     * Повертає текстову назву категорії.
     *
     * @return текстова назва категорії
     */
    @Override
    public String toString() {
        return label;
    }
}