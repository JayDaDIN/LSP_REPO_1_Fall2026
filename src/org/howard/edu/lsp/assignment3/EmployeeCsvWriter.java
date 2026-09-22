package org.howard.edu.lsp.assignment3;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Load step: writes transformed employees to the output CSV.
 *
 * <p>The writer owns the header text and the file mechanics; each {@link Employee}
 * renders its own row. That split keeps the file format in one place without the writer
 * needing to know anything about payroll.
 *
 * @author Justin Dunbar
 */
public class EmployeeCsvWriter {

    /** Header row written before any data. */
    private static final String HEADER =
            "EmployeeID,Name,Department,HoursWorked,HourlyRate,GrossPay,PayLevel,EmploymentStatus";

    /**
     * Line terminator written after every row.
     *
     * <p>Written explicitly rather than using {@code BufferedWriter.newLine()}, which
     * would emit CRLF on Windows and LF elsewhere and make the generated file differ
     * between machines.
     */
    private static final String LINE_SEPARATOR = "\n";

    /** Location of the output file. */
    private final Path outputPath;

    /**
     * Creates a writer for the given output file.
     *
     * @param outputPath location of the output CSV
     */
    public EmployeeCsvWriter(Path outputPath) {
        this.outputPath = outputPath;
    }

    /**
     * Writes the header row followed by one row per employee, replacing any existing file.
     *
     * @param employees employees to write, in output order
     * @throws IOException if the file cannot be written
     */
    public void write(List<Employee> employees) throws IOException {
        Path parent = outputPath.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        try (BufferedWriter writer = Files.newBufferedWriter(outputPath, StandardCharsets.UTF_8)) {
            writer.write(HEADER);
            writer.write(LINE_SEPARATOR);
            for (Employee employee : employees) {
                writer.write(employee.toCsvRow());
                writer.write(LINE_SEPARATOR);
            }
        }
    }

    /**
     * Returns the output location using forward slashes on every platform.
     *
     * <p>Used for the console summary so the reported path reads the same on Windows as
     * it does on macOS or Linux.
     *
     * @return the display form of the output path
     */
    public String getDisplayPath() {
        return outputPath.toString().replace('\\', '/');
    }
}
