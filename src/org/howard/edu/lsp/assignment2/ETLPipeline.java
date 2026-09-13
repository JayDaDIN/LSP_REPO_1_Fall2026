package org.howard.edu.lsp.assignment2;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Extract-Transform-Load pipeline for employee payroll data.
 *
 * <p>Reads {@code data/employees.csv}, applies the payroll transformations defined by
 * the assignment specification, and writes {@code data/transformed_employees.csv}.
 * Both paths are relative to the directory the program is launched from, which is
 * expected to be the project root.
 *
 * <p>Malformed rows are skipped rather than terminating the run, and a summary of
 * rows read, transformed, and skipped is printed to the console.
 *
 * @author Justin Dunbar
 */
public class ETLPipeline {

    /** Relative path of the input CSV, per the assignment's execution contract. */
    private static final String INPUT_PATH = "data/employees.csv";

    /** Relative path of the output CSV, per the assignment's execution contract. */
    private static final String OUTPUT_PATH = "data/transformed_employees.csv";

    /** Header written to the output file. */
    private static final String OUTPUT_HEADER =
            "EmployeeID,Name,Department,HoursWorked,HourlyRate,GrossPay,PayLevel,EmploymentStatus";

    /** Number of fields every valid input row must contain. */
    private static final int EXPECTED_FIELD_COUNT = 5;

    /** Hours beyond this threshold are paid at the overtime multiplier. */
    private static final BigDecimal OVERTIME_THRESHOLD = new BigDecimal("40");

    /** Pay multiplier applied to hours above the overtime threshold. */
    private static final BigDecimal OVERTIME_MULTIPLIER = new BigDecimal("1.5");

    /** Multiplier representing the 5% bonus applied to the IT department. */
    private static final BigDecimal IT_BONUS_MULTIPLIER = new BigDecimal("1.05");

    /** Department whose employees receive the bonus (case-sensitive, per spec). */
    private static final String BONUS_DEPARTMENT = "IT";

    /** Hours below this threshold classify an employee as part-time. */
    private static final BigDecimal FULL_TIME_THRESHOLD = new BigDecimal("30.00");

    /** Utility class; not meant to be instantiated. */
    private ETLPipeline() {
    }

    /**
     * Runs the complete ETL pipeline with no arguments and no keyboard input.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        Path inputPath = Paths.get(INPUT_PATH);
        Path outputPath = Paths.get(OUTPUT_PATH);

        List<String> dataRows;
        try {
            dataRows = extract(inputPath);
        } catch (IOException e) {
            System.out.println("Error: could not read input file " + INPUT_PATH);
            System.out.println("Make sure you are running from the project root and that the file exists.");
            System.out.println("Reason: " + e.getMessage());
            return;
        }

        List<String> outputRows = transform(dataRows);

        try {
            load(outputPath, outputRows);
        } catch (IOException e) {
            System.out.println("Error: could not write output file " + OUTPUT_PATH);
            System.out.println("Reason: " + e.getMessage());
            return;
        }

        int rowsRead = dataRows.size();
        int rowsTransformed = outputRows.size();
        System.out.println("Rows read: " + rowsRead);
        System.out.println("Rows transformed: " + rowsTransformed);
        System.out.println("Rows skipped: " + (rowsRead - rowsTransformed));
        System.out.println("Output file: " + OUTPUT_PATH);
    }

    /**
     * Extract step: reads every non-header line from the input file.
     *
     * <p>Blank and malformed lines are returned as-is so that they are counted in the
     * run summary; they are filtered out later by {@link #transform(List)}.
     *
     * @param inputPath relative path of the input CSV
     * @return the data lines, excluding the header row
     * @throws IOException if the file cannot be read
     */
    private static List<String> extract(Path inputPath) throws IOException {
        List<String> allLines = Files.readAllLines(inputPath, StandardCharsets.UTF_8);
        if (allLines.isEmpty()) {
            return new ArrayList<>();
        }
        // The first row is a header and must not be transformed.
        return new ArrayList<>(allLines.subList(1, allLines.size()));
    }

    /**
     * Transform step: validates each data row and converts the valid ones into output rows.
     *
     * @param dataRows raw non-header lines from the input file
     * @return formatted output lines, one per row that survived validation
     */
    private static List<String> transform(List<String> dataRows) {
        List<String> outputRows = new ArrayList<>();
        for (String line : dataRows) {
            String outputRow = transformRow(line);
            if (outputRow != null) {
                outputRows.add(outputRow);
            }
        }
        return outputRows;
    }

    /**
     * Applies the seven transformation steps to a single input row.
     *
     * @param line one raw line from the input file
     * @return the formatted output line, or {@code null} if the row must be skipped
     */
    private static String transformRow(String line) {
        if (line == null || line.trim().isEmpty()) {
            return null;
        }

        // -1 keeps trailing empty fields so that "1,2,3,4," is seen as five fields, not four.
        String[] fields = line.split(",", -1);
        if (fields.length != EXPECTED_FIELD_COUNT) {
            return null;
        }

        // Step 1: normalize every field, then uppercase the name.
        String employeeIdText = fields[0].trim();
        String name = fields[1].trim().toUpperCase(Locale.ROOT);
        String department = fields[2].trim();
        String hoursText = fields[3].trim();
        String rateText = fields[4].trim();

        // Step 2: validate numeric values.
        int employeeId;
        BigDecimal hoursWorked;
        BigDecimal hourlyRate;
        try {
            employeeId = Integer.parseInt(employeeIdText);
            hoursWorked = new BigDecimal(hoursText);
            hourlyRate = new BigDecimal(rateText);
        } catch (NumberFormatException e) {
            return null;
        }
        if (hoursWorked.signum() < 0 || hourlyRate.signum() < 0) {
            return null;
        }

        // Steps 3-5: base pay, overtime, IT bonus, then round.
        BigDecimal grossPay = calculateGrossPay(hoursWorked, hourlyRate, department);

        // Steps 6-7: classify using the final rounded pay and the parsed hours.
        String payLevel = determinePayLevel(grossPay);
        String employmentStatus = determineEmploymentStatus(hoursWorked);

        return String.join(",",
                String.valueOf(employeeId),
                name,
                department,
                formatTwoDecimals(hoursWorked),
                formatTwoDecimals(hourlyRate),
                grossPay.toPlainString(),
                payLevel,
                employmentStatus);
    }

    /**
     * Calculates gross pay including overtime and the IT bonus, rounded half-up to cents.
     *
     * <p>The unrounded {@code hourlyRate} is used throughout; rounding to two decimal
     * places happens only once, at the end.
     *
     * @param hoursWorked hours worked, already validated as non-negative
     * @param hourlyRate  hourly rate, already validated as non-negative
     * @param department  trimmed department name
     * @return gross pay with a scale of exactly two
     */
    private static BigDecimal calculateGrossPay(BigDecimal hoursWorked, BigDecimal hourlyRate,
                                                String department) {
        BigDecimal pay;
        if (hoursWorked.compareTo(OVERTIME_THRESHOLD) > 0) {
            BigDecimal overtimeHours = hoursWorked.subtract(OVERTIME_THRESHOLD);
            BigDecimal basePay = OVERTIME_THRESHOLD.multiply(hourlyRate);
            BigDecimal overtimePay = overtimeHours.multiply(hourlyRate).multiply(OVERTIME_MULTIPLIER);
            pay = basePay.add(overtimePay);
        } else {
            pay = hoursWorked.multiply(hourlyRate);
        }

        if (BONUS_DEPARTMENT.equals(department)) {
            pay = pay.multiply(IT_BONUS_MULTIPLIER);
        }

        return pay.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Classifies pay level from the final rounded gross pay.
     *
     * @param grossPay rounded gross pay
     * @return one of Low, Standard, High, or Executive
     */
    private static String determinePayLevel(BigDecimal grossPay) {
        if (grossPay.compareTo(new BigDecimal("500.00")) < 0) {
            return "Low";
        }
        if (grossPay.compareTo(new BigDecimal("1000.00")) < 0) {
            return "Standard";
        }
        if (grossPay.compareTo(new BigDecimal("2000.00")) < 0) {
            return "High";
        }
        return "Executive";
    }

    /**
     * Classifies employment status from hours worked.
     *
     * @param hoursWorked parsed hours worked
     * @return Part-Time if under 30 hours, otherwise Full-Time
     */
    private static String determineEmploymentStatus(BigDecimal hoursWorked) {
        return hoursWorked.compareTo(FULL_TIME_THRESHOLD) < 0 ? "Part-Time" : "Full-Time";
    }

    /**
     * Formats a value with exactly two decimal places and no scientific notation.
     *
     * @param value the value to format
     * @return the value rendered to two decimal places
     */
    private static String formatTwoDecimals(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    /**
     * Load step: writes the header and every transformed row to the output file.
     *
     * <p>Uses {@code \n} explicitly so the generated file is byte-identical regardless
     * of the operating system it was produced on.
     *
     * @param outputPath relative path of the output CSV
     * @param outputRows formatted rows to write
     * @throws IOException if the file cannot be written
     */
    private static void load(Path outputPath, List<String> outputRows) throws IOException {
        Path parent = outputPath.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        try (BufferedWriter writer = Files.newBufferedWriter(outputPath, StandardCharsets.UTF_8)) {
            writer.write(OUTPUT_HEADER);
            writer.write("\n");
            for (String row : outputRows) {
                writer.write(row);
                writer.write("\n");
            }
        }
    }
}
