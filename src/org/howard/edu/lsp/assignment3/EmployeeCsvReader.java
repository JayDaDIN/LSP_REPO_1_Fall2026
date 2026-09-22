package org.howard.edu.lsp.assignment3;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Extract step: reads the raw, non-header lines out of the input CSV.
 *
 * <p>The reader is deliberately uninterested in what a row means. It returns blank and
 * malformed lines untouched so that they are still counted as rows read, and leaves
 * validation to {@link EmployeeRowParser}.
 *
 * <p>The path is supplied to the constructor rather than hard-coded as a static
 * constant, so the class is not welded to one location on disk.
 *
 * @author Justin Dunbar
 */
public class EmployeeCsvReader {

    /** Location of the input file. */
    private final Path inputPath;

    /**
     * Creates a reader for the given input file.
     *
     * @param inputPath location of the input CSV
     */
    public EmployeeCsvReader(Path inputPath) {
        this.inputPath = inputPath;
    }

    /**
     * Reads every line after the header row.
     *
     * @return the data lines, in file order; empty if the file has no rows at all
     * @throws IOException if the file cannot be read
     */
    public List<String> readDataRows() throws IOException {
        List<String> allLines = Files.readAllLines(inputPath, StandardCharsets.UTF_8);
        if (allLines.isEmpty()) {
            return new ArrayList<>();
        }
        // The first row is a header and must not be transformed.
        return new ArrayList<>(allLines.subList(1, allLines.size()));
    }

    /**
     * Returns the input location using forward slashes on every platform.
     *
     * @return the display form of the input path
     */
    public String getDisplayPath() {
        return inputPath.toString().replace('\\', '/');
    }
}
