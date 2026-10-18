package ua.lpnu.kzp;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RepositoryTest {

    @Test
    void shouldAddAndReturnItemsInInsertionOrder() {
        Repository<String> repository = new Repository<>();

        repository.add("Dune");
        repository.add("Avatar");

        assertEquals(List.of("Dune", "Avatar"), repository.all());
    }

    @Test
    void shouldFilterItemsUsingPredicate() {
        Repository<Integer> repository = new Repository<>();

        repository.add(10);
        repository.add(25);
        repository.add(30);

        assertEquals(List.of(25, 30), repository.find(value -> value > 20));
    }

    @Test
    void shouldReturnImmutableList() {
        Repository<String> repository = new Repository<>();
        repository.add("Dune");

        List<String> result = repository.all();

        assertThrows(UnsupportedOperationException.class,
                () -> result.add("Avatar"));
    }

    @Test
    void shouldReturnIndependentSnapshot() {
        Repository<String> repository = new Repository<>();
        repository.add("Dune");

        List<String> snapshot = repository.all();
        repository.add("Avatar");

        assertEquals(List.of("Dune"), snapshot);
        assertEquals(List.of("Dune", "Avatar"), repository.all());
    }

    @Test
    void shouldHandleEmptyRepository() {
        Repository<String> repository = new Repository<>();

        assertTrue(repository.all().isEmpty());
        assertTrue(repository.find(value -> true).isEmpty());
    }

    @Test
    void shouldRejectNullItemAndPredicate() {
        Repository<String> repository = new Repository<>();

        assertThrows(NullPointerException.class,
                () -> repository.add(null));

        assertThrows(NullPointerException.class,
                () -> repository.find(null));
    }
}