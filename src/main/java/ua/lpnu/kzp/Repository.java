package ua.lpnu.kzp;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class Repository<T> {

    private final List<T> items = new ArrayList<>();

    public void add(T item) {
        items.add(Objects.requireNonNull(item, "Item must not be null"));
    }

    public List<T> all() {
        return List.copyOf(items);
    }

    public List<T> find(Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate, "Predicate must not be null");

        return items.stream()
                .filter(predicate)
                .toList();
    }
}
