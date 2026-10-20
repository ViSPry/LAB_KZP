package ua.lpnu.kzp;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public final class StorageService {

    private final Repository<ScreeningRecord> repository = new Repository<>();

    public int importCsv(Path path) {
        List<ScreeningRecord> records = ScreeningCsvImporter.read(path);

        for (ScreeningRecord record : records) {
            repository.add(record);
        }

        return records.size();
    }

    public List<ScreeningRecord> all() {
        return repository.all();
    }

    public List<ScreeningRecord> findByFilm(String film) {
        Objects.requireNonNull(film, "Film must not be null");

        return repository.find(record ->
                record.film().equalsIgnoreCase(film));
    }

    public void exportCsv(Path path) {
        Exporter<ScreeningRecord> exporter =
                CsvExporter.forType(ScreeningRecord.class);

        exporter.export(path, repository.all());
    }

    public void exportJson(Path path) {
        Exporter<ScreeningRecord> exporter =
                new JsonExporter<>(ScreeningRecord.class);

        exporter.export(path, repository.all());
    }
}
