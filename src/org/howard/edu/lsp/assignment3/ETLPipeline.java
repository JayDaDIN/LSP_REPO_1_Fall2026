package org.howard.edu.lsp.assignment3;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Object-oriented refactoring of the Assignment 2 employee payroll ETL pipeline.
 *
 * <p>Behavior is unchanged: this program reads {@code data/employees.csv}, applies the
 * same payroll rules, writes {@code data/transformed_employees.csv}, and prints the same
 * run summary. What changed is the design. Assignment 2 was one class of static methods;
 * here each stage of the pipeline is an object with a single responsibility, and this
 * class does nothing but wire them together and run them in order.
 *
 * <p>See {@code doc/DESIGN.md} for the full comparison.
 *
 * @author Justin Dunbar
 */
public class ETLPipeline {

    /** Relative path of the input CSV, per the assignment's execution contract. */
    private static final String INPUT_PATH = "data/employees.csv";

    /** Relative path of the output CSV, per the assignment's execution contract. */
    private static final String OUTPUT_PATH = "data/transformed_employees.csv";

    /** Extract stage. */
    private final EmployeeCsvReader reader;

    /** Transform stage, one row at a time. */
    private final EmployeeRowParser parser;

    /** Load stage. */
    private final EmployeeCsvWriter writer;

    /**
     * Creates a pipeline from its three stages.
     *
     * <p>The collaborators are passed in rather than constructed here, so the pipeline
     * is not tied to any particular file location.
     *
     * @param reader extract stage
     * @param parser transform stage
     * @param writer load stage
     */
    public ETLPipeline(EmployeeCsvReader reader, EmployeeRowParser parser,
                       EmployeeCsvWriter writer) {
        this.reader = reader;
        this.parser = parser;
        this.writer = writer;
    }

    /**
     * Runs the complete ETL pipeline with no arguments and no keyboard input.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        PayrollCalculator calculator = new PayrollCalculator();
        ETLPipeline pipeline = new ETLPipeline(
                new EmployeeCsvReader(Paths.get(INPUT_PATH)),
                new EmployeeRowParser(calculator),
                new EmployeeCsvWriter(Paths.get(OUTPUT_PATH)));
        pipeline.run();
    }

    /**
     * Executes extract, transform, and load, then prints the run summary.
     *
     * <p>If a file cannot be read or written the problem is reported and the run ends
     * without a stack trace. Malformed data rows never reach this level; they are
     * skipped during transform and reflected in the summary counts.
     */
    public void run() {
        List<String> dataRows;
        try {
            dataRows = reader.readDataRows();
        } catch (IOException e) {
            System.out.println("Error: could not read input file " + reader.getDisplayPath());
            System.out.println("Make sure you are running from the project root and that the file exists.");
            System.out.println("Reason: " + e.getMessage());
            return;
        }

        List<Employee> employees = transform(dataRows);

        try {
            writer.write(employees);
        } catch (IOException e) {
            System.out.println("Error: could not write output file " + writer.getDisplayPath());
            System.out.println("Reason: " + e.getMessage());
            return;
        }

        RunSummary summary = new RunSummary(dataRows.size(), employees.size(),
                writer.getDisplayPath());
        summary.printTo(System.out);
    }

    /**
     * Transform step: parses each raw row, discarding the ones that fail validation.
     *
     * @param dataRows raw non-header lines from the input file
     * @return employees built from the rows that passed validation, in file order
     */
    private List<Employee> transform(List<String> dataRows) {
        List<Employee> employees = new ArrayList<>();
        for (String row : dataRows) {
            parser.parse(row).ifPresent(employees::add);
        }
        return employees;
    }
}
