
# Cinema CSV Processing Application

Laboratory Work No. 1 — Variant 19: Cinema.

A Java console application for processing cinema records from a CSV file, validating input data, calculating statistics, and generating a UTF-8 report.

## Technologies

- Java 21
- Maven 3.9+
- JUnit 5
- SpotBugs
- Git and GitHub
- GitHub Actions

## CSV Format

The application uses a semicolon-separated CSV file with five fields:

`film;hall;ticketPrice;sold;durationMin`

Example:

```text
Дюна 2;1;180.00;120;166
Оппенгеймер;2;200.00;95;180
Інтерстеллар;3;160.00;150;169
```

## Validation

The application checks that:

- Each record contains exactly five fields.
- The film title is not empty.
- The hall number is positive.
- The ticket price is finite and non-negative.
- The number of sold tickets is non-negative.
- The film duration is positive.
- Numeric fields have a valid format.

Invalid records are counted but excluded from statistical calculations.

## Calculated Statistics

- Number of valid records.
- Number of invalid records.
- Total revenue.
- Average ticket price.
- Maximum number of sold tickets.

## Build

On Windows:

```cmd
mvnw.cmd clean package
```

On Linux and macOS:

```bash
./mvnw clean package
```

## Run

Default execution:

```bash
java -jar target/lab01-1.0.0.jar
```

Display help:

```bash
java -jar target/lab01-1.0.0.jar --help
```

Display version:

```bash
java -jar target/lab01-1.0.0.jar --version
```

Use custom input and output files:

```bash
java -jar target/lab01-1.0.0.jar --input data/input.csv --output out/custom-report.txt
```

## Input and Output

Default input: `data/input.csv`

Default output: `out/report.txt`

Both files use UTF-8 encoding.

## Testing

Run JUnit tests:

```cmd
mvnw.cmd clean test
```

The project includes 8 automated tests.

## Static Analysis

Run SpotBugs:

```cmd
mvnw.cmd clean compile spotbugs:check
```

## Continuous Integration

GitHub Actions automatically builds and tests the project on:

- Ubuntu
- Windows
- macOS

Each successful build uploads an executable JAR as a workflow artifact.

## Version

Current version: `1.0.0`

Git tag: `v1.0.0`
