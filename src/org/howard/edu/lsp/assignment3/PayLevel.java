package org.howard.edu.lsp.assignment3;

import java.math.BigDecimal;

/**
 * Pay bands an employee can fall into, based on final rounded gross pay.
 *
 * <p>In Assignment 2 these were bare strings returned from an {@code if} chain, so a
 * misspelling would have compiled cleanly and only surfaced in the output file. Making
 * them an enum turns the four bands into a closed set the compiler enforces, and keeps
 * the threshold logic beside the values it produces.
 *
 * @author Justin Dunbar
 */
public enum PayLevel {

    /** Gross pay below $500.00. */
    LOW("Low"),

    /** Gross pay from $500.00 through $999.99. */
    STANDARD("Standard"),

    /** Gross pay from $1000.00 through $1999.99. */
    HIGH("High"),

    /** Gross pay of $2000.00 or more. */
    EXECUTIVE("Executive");

    /** Lowest gross pay that still counts as {@link #STANDARD}. */
    private static final BigDecimal STANDARD_MINIMUM = new BigDecimal("500.00");

    /** Lowest gross pay that still counts as {@link #HIGH}. */
    private static final BigDecimal HIGH_MINIMUM = new BigDecimal("1000.00");

    /** Lowest gross pay that still counts as {@link #EXECUTIVE}. */
    private static final BigDecimal EXECUTIVE_MINIMUM = new BigDecimal("2000.00");

    /** Text written to the output CSV for this band. */
    private final String label;

    /**
     * Creates a pay level with its CSV label.
     *
     * @param label text written to the output file
     */
    PayLevel(String label) {
        this.label = label;
    }

    /**
     * Classifies a gross pay amount into a pay band.
     *
     * <p>Must be called with the <em>final rounded</em> gross pay, as the specification
     * requires the band to be determined after rounding.
     *
     * @param grossPay final rounded gross pay
     * @return the matching pay level
     */
    public static PayLevel forGrossPay(BigDecimal grossPay) {
        if (grossPay.compareTo(STANDARD_MINIMUM) < 0) {
            return LOW;
        }
        if (grossPay.compareTo(HIGH_MINIMUM) < 0) {
            return STANDARD;
        }
        if (grossPay.compareTo(EXECUTIVE_MINIMUM) < 0) {
            return HIGH;
        }
        return EXECUTIVE;
    }

    /**
     * Returns the label written to the output CSV.
     *
     * @return the CSV label, such as {@code Executive}
     */
    @Override
    public String toString() {
        return label;
    }
}
