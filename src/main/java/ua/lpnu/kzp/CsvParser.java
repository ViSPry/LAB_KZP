package ua.lpnu.kzp;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Parses CSV according to standard quoting and escaping rules.
 */
public final class CsvParser {

    private CsvParser() {
    }

    public static List<List<String>> parse(String csv) {
        Objects.requireNonNull(csv, "CSV content must not be null");

        try {
            return parse(new StringReader(csv));
        } catch (IOException exception) {
            throw new DataStorageException(
                    "Failed to read CSV content", exception);
        }
    }

    public static List<List<String>> parse(Reader reader)
            throws IOException {

        Objects.requireNonNull(reader, "Reader must not be null");

        List<List<String>> rows = new ArrayList<>();
        List<String> row = new ArrayList<>();
        StringBuilder field = new StringBuilder();

        boolean quoted = false;
        boolean afterQuote = false;
        boolean fieldStarted = false;

        java.io.PushbackReader input =
                new java.io.PushbackReader(reader, 1);

        int next;

        while ((next = input.read()) != -1) {
            char ch = (char) next;

            if (quoted) {
                if (ch == '"') {
                    int following = input.read();

                    if (following == '"') {
                        field.append('"');
                    } else {
                        quoted = false;
                        afterQuote = true;

                        if (following != -1) {
                            input.unread(following);
                        }
                    }
                } else {
                    field.append(ch);
                }

                continue;
            }

            if (afterQuote) {
                if (ch != ',' && ch != '\n' && ch != '\r') {
                    throw new DataStorageException(
                            "Unexpected character after closing CSV quote");
                }
                afterQuote = false;
            } else if (ch == '"') {
                if (fieldStarted) {
                    throw new DataStorageException(
                            "Unexpected quote in unquoted CSV field");
                }

                quoted = true;
                fieldStarted = true;
                continue;
            }

            if (ch == ',') {
                row.add(field.toString());
                field.setLength(0);
                fieldStarted = false;
            } else if (ch == '\n' || ch == '\r') {
                if (ch == '\r') {
                    int following = input.read();

                    if (following != '\n' && following != -1) {
                        input.unread(following);
                    }
                }

                row.add(field.toString());
                rows.add(List.copyOf(row));

                row = new ArrayList<>();
                field.setLength(0);
                fieldStarted = false;
            } else {
                field.append(ch);
                fieldStarted = true;
            }
        }

        if (quoted) {
            throw new DataStorageException(
                    "Unclosed quoted CSV field");
        }

        if (fieldStarted || afterQuote || !row.isEmpty()) {
            row.add(field.toString());
            rows.add(List.copyOf(row));
        }

        return List.copyOf(rows);
    }
}