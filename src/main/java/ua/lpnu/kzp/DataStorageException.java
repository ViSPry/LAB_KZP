package ua.lpnu.kzp;

/**
 * Represents an error during data import or export.
 */
public class DataStorageException extends RuntimeException {

    public DataStorageException(String message) {
        super(message);
    }

    public DataStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}