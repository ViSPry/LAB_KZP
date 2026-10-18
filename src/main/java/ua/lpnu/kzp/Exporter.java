package ua.lpnu.kzp;

import java.nio.file.Path;
import java.util.List;

/**
 * Common contract for exporting records to different file formats.
 *
 * @param <T> type of exported records
 */
@FunctionalInterface
public interface Exporter<T> {

    /**
     * Exports records to the specified destination.
     *
     * @param path destination file
     * @param records records to export
     * @throws DataStorageException if exporting fails
     */
    void export(Path path, List<? extends T> records);
}