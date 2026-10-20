package ua.lpnu.kzp;

/**
 * Точка входу до консольного застосунку кінотеатру.
 *
 * Основною логікою програми керує CinemaController.
 */
public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        new CinemaController().run(args);
    }
}