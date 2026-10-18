package ua.lpnu.kzp;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CsvParserTest {

    @Test
    void shouldParseSimpleCsv() {
        String csv = "film,hall\nDune,Hall 1\nAvatar,Hall 2\n";

        assertEquals(List.of(
                List.of("film", "hall"),
                List.of("Dune", "Hall 1"),
                List.of("Avatar", "Hall 2")
        ), CsvParser.parse(csv));
    }

    @Test
    void shouldParseQuotedCommasAndEscapedQuotes() {
        String csv = "film,hall\n\"Dune, Part Two\","
                + "\"Hall \"\"VIP\"\"\"\n";

        assertEquals(List.of(
                List.of("film", "hall"),
                List.of("Dune, Part Two", "Hall \"VIP\"")
        ), CsvParser.parse(csv));
    }

    @Test
    void shouldPreserveEmbeddedNewlines() {
        String csv = "film,hall\n\"First line\nSecond line\",Hall 1\n";

        assertEquals(
                "First line\nSecond line",
                CsvParser.parse(csv).get(1).get(0)
        );
    }

    @Test
    void shouldSupportCrLfLineEndings() {
        String csv = "film,hall\r\nDune,Hall 1\r\n";

        assertEquals(List.of(
                List.of("film", "hall"),
                List.of("Dune", "Hall 1")
        ), CsvParser.parse(csv));
    }

    @Test
    void shouldPreserveEmptyFields() {
        String csv = "a,b,c\n,hello,\n";

        assertEquals(
                List.of("", "hello", ""),
                CsvParser.parse(csv).get(1)
        );
    }

    @Test
    void shouldHandleEmptyInput() {
        assertTrue(CsvParser.parse("").isEmpty());
    }

    @Test
    void shouldRejectUnclosedQuotedField() {
        assertThrows(
                DataStorageException.class,
                () -> CsvParser.parse("film,hall\n\"Dune,Hall 1")
        );
    }

    @Test
    void shouldRejectUnexpectedQuote() {
        assertThrows(
                DataStorageException.class,
                () -> CsvParser.parse("Du\"ne,Hall 1")
        );
    }

    @Test
    void shouldRejectCharactersAfterClosingQuote() {
        assertThrows(
                DataStorageException.class,
                () -> CsvParser.parse("\"Dune\"invalid,Hall 1")
        );
    }

    @Test
    void shouldPropagateReaderIOException() {
        Reader brokenReader = new Reader() {
            @Override
            public int read(char[] buffer, int offset, int length)
                    throws IOException {
                throw new IOException("Simulated read failure");
            }

            @Override
            public void close() {
            }
        };

        IOException exception = assertThrows(
                IOException.class,
                () -> CsvParser.parse(brokenReader)
        );

        assertEquals("Simulated read failure", exception.getMessage());
    }
}