package org.howard.edu.lsp.assignment3;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Applies the payroll pay rules: overtime, the IT department bonus, and rounding.
 *
 * <p>This class exists so the business rules live in one place, separate from parsing,
 * file access, and the domain object itself. Changing the overtime multiplier or the
 * bonus percentage means editing only this class.
 *
 * <p>All arithmetic uses {@link BigDecimal}. Binary floating point cannot represent
 * values such as {@code 2.675} exactly, which would break the round-half-up rule the
 * specification requires.
 *
 * @author Justin Dunbar
 */
public class PayrollCalculator {

    /** Hours beyond this threshold are paid at the overtime multiplier. */
    private static final BigDecimal OVERTIME_THRESHOLD = new BigDecimal("40");

    /** Pay multiplier applied to hours above the overtime threshold. */
    private static final BigDecimal OVERTIME_MULTIPLIER = new BigDecimal("1.5");

    /** Multiplier representing the 5% bonus applied to the IT department. */
    private static final BigDecimal IT_BONUS_MULTIPLIER = new BigDecimal("1.05");

    /** Department whose employees receive the bonus. Compared case-sensitively, per spec. */
    private static final String BONUS_DEPARTMENT = "IT";

    /** Decimal places used for currency amounts. */
    private static final int CURRENCY_SCALE = 2;

    /**
     * Calculates gross pay including overtime and the IT bonus, rounded half-up to cents.
     *
     * <p>The unrounded {@code hourlyRate} is used throughout. Rounding happens exactly
     * once, at the end, so formatting never feeds back into the calculation.
     *
     * @param hoursWorked hours worked, already validated as non-negative
     * @param hourlyRate  hourly rate, already validated as non-negative
     * @param department  trimmed department name
     * @return gross pay with a scale of exactly two
     */
    public BigDecimal calculateGrossPay(BigDecimal hoursWorked, BigDecimal hourlyRate,
                                        String department) {
        BigDecimal pay = calculateBeforeBonus(hoursWorked, hourlyRate);
        if (qualifiesForBonus(department)) {
            pay = pay.multiply(IT_BONUS_MULTIPLIER);
        }
        return pay.setScale(CURRENCY_SCALE, RoundingMode.HALF_UP);
    }

    /**
     * Calculates pay at the normal rate, adding overtime for hours above the threshold.
     *
     * @param hoursWorked hours worked
     * @param hourlyRate  hourly rate
     * @return unrounded pay before any department bonus
     */
    private BigDecimal calculateBeforeBonus(BigDecimal hoursWorked, BigDecimal hourlyRate) {
        if (hoursWorked.compareTo(OVERTIME_THRESHOLD) <= 0) {
            return hoursWorked.multiply(hourlyRate);
        }
        BigDecimal overtimeHours = hoursWorked.subtract(OVERTIME_THRESHOLD);
        BigDecimal basePay = OVERTIME_THRESHOLD.multiply(hourlyRate);
        BigDecimal overtimePay = overtimeHours.multiply(hourlyRate).multiply(OVERTIME_MULTIPLIER);
        return basePay.add(overtimePay);
    }

    /**
     * Reports whether a department receives the bonus.
     *
     * @param department trimmed department name
     * @return true if the department matches exactly, including case
     */
    private boolean qualifiesForBonus(String department) {
        return BONUS_DEPARTMENT.equals(department);
    }
}
