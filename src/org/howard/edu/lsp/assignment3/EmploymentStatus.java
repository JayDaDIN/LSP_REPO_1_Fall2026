package org.howard.edu.lsp.assignment3;

import java.math.BigDecimal;

/**
 * Whether an employee is part-time or full-time, based on hours worked.
 *
 * <p>Like {@link PayLevel}, this replaces a string literal produced by a conditional in
 * Assignment 2 with a named type, so the set of valid values is fixed at compile time.
 *
 * @author Justin Dunbar
 */
public enum EmploymentStatus {

    /** Fewer than 30.00 hours worked. */
    PART_TIME("Part-Time"),

    /** 30.00 or more hours worked. */
    FULL_TIME("Full-Time");

    /** Fewest hours that still count as {@link #FULL_TIME}. */
    private static final BigDecimal FULL_TIME_MINIMUM = new BigDecimal("30.00");

    /** Text written to the output CSV for this status. */
    private final String label;

    /**
     * Creates an employment status with its CSV label.
     *
     * @param label text written to the output file
     */
    EmploymentStatus(String label) {
        this.label = label;
    }

    /**
     * Classifies hours worked into an employment status.
     *
     * @param hoursWorked parsed hours worked, before any output formatting
     * @return the matching employment status
     */
    public static EmploymentStatus forHours(BigDecimal hoursWorked) {
        return hoursWorked.compareTo(FULL_TIME_MINIMUM) < 0 ? PART_TIME : FULL_TIME;
    }

    /**
     * Returns the label written to the output CSV.
     *
     * @return the CSV label, either {@code Part-Time} or {@code Full-Time}
     */
    @Override
    public String toString() {
        return label;
    }
}
