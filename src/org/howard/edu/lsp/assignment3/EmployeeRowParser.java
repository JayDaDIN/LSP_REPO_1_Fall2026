package org.howard.edu.lsp.assignment3;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Optional;

/**
 * Turns one raw CSV line into an {@link Employee}, or reports that the row must be skipped.
 *
 * <p>This class owns every rule about what a well-formed input row looks like: field
 * count, whitespace trimming, name casing, and numeric validation. Separating it from
 * file access means the parsing rules can be exercised on a plain string without
 * touching the disk.
 *
 * <p>A row that cannot be parsed yields an empty {@link Optional} rather than
 * {@code null}. Assignment 2 returned {@code null} to mean "skip", which the caller had
 * to remember to check; {@code Optional} states that possibility in the return type.
 *
 * @author Justin Dunbar
 */
public class EmployeeRowParser {

    /** Number of fields every valid input row must contain. */
    private static final int EXPECTED_FIELD_COUNT = 5;

    /** Column position of each field in the input file. */
    private static final int ID_INDEX = 0;
    private static final int NAME_INDEX = 1;
    private static final int DEPARTMENT_INDEX = 2;
    private static final int HOURS_INDEX = 3;
    private static final int RATE_INDEX = 4;

    /** Supplies the payroll rules used to build each {@link Employee}. */
    private final PayrollCalculator calculator;

    /**
     * Creates a parser that builds employees using the given payroll rules.
     *
     * @param calculator payroll rules passed on to each {@link Employee}
     */
    public EmployeeRowParser(PayrollCalculator calculator) {
        this.calculator = calculator;
    }

    /**
     * Parses and validates one raw input line.
     *
     * @param line one non-header line from the input file
     * @return the resulting employee, or empty if the row must be skipped
     */
    public Optional<Employee> parse(String line) {
        if (isBlank(line)) {
            return Optional.empty();
        }

        // -1 keeps trailing empty fields, so "1,2,3,4," is seen as five fields, not four.
        String[] fields = line.split(",", -1);
        if (fields.length != EXPECTED_FIELD_COUNT) {
            return Optional.empty();
        }

        // Normalize every field before validating any of it.
        String idText = fields[ID_INDEX].trim();
        String name = fields[NAME_INDEX].trim().toUpperCase(Locale.ROOT);
        String department = fields[DEPARTMENT_INDEX].trim();
        String hoursText = fields[HOURS_INDEX].trim();
        String rateText = fields[RATE_INDEX].trim();

        int employeeId;
        BigDecimal hoursWorked;
        BigDecimal hourlyRate;
        try {
            employeeId = Integer.parseInt(idText);
            hoursWorked = new BigDecimal(hoursText);
            hourlyRate = new BigDecimal(rateText);
        } catch (NumberFormatException e) {
            return Optional.empty();
        }

        if (isNegative(hoursWorked) || isNegative(hourlyRate)) {
            return Optional.empty();
        }

        return Optional.of(new Employee(employeeId, name, department, hoursWorked,
                hourlyRate, calculator));
    }

    /**
     * Reports whether a line is null, empty, or only whitespace.
     *
     * @param line the line to test
     * @return true if the line carries no content
     */
    private static boolean isBlank(String line) {
        return line == null || line.trim().isEmpty();
    }

    /**
     * Reports whether a value is below zero.
     *
     * @param value the value to test
     * @return true if the value is negative
     */
    private static boolean isNegative(BigDecimal value) {
        return value.signum() < 0;
    }
}
