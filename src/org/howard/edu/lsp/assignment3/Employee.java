package org.howard.edu.lsp.assignment3;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * One employee's payroll record: the values read from the input file together with the
 * payroll figures derived from them.
 *
 * <p>Instances are immutable. Everything derived — gross pay, pay level, employment
 * status — is computed once in the constructor, so an {@code Employee} can never be in a
 * half-populated state. In Assignment 2 these values lived as loose local variables
 * inside one long method and were joined into a string on the spot; here the object owns
 * its own data and knows how to render itself.
 *
 * <p>The pay <em>rules</em> deliberately stay in {@link PayrollCalculator} rather than
 * in this class, so the rules can change without touching the domain object.
 *
 * @author Justin Dunbar
 */
public class Employee {

    /** Decimal places used when writing numeric fields to the output file. */
    private static final int OUTPUT_SCALE = 2;

    /** Employee identifier, validated as an integer. */
    private final int employeeId;

    /** Employee name, trimmed and converted to uppercase. */
    private final String name;

    /** Department name, trimmed only. */
    private final String department;

    /** Hours worked, as parsed from the input file. */
    private final BigDecimal hoursWorked;

    /** Hourly rate, as parsed from the input file and never pre-rounded. */
    private final BigDecimal hourlyRate;

    /** Gross pay, rounded half-up to two decimal places. */
    private final BigDecimal grossPay;

    /** Pay band derived from {@link #grossPay}. */
    private final PayLevel payLevel;

    /** Employment status derived from {@link #hoursWorked}. */
    private final EmploymentStatus employmentStatus;

    /**
     * Creates an employee and computes its payroll figures.
     *
     * @param employeeId  validated employee identifier
     * @param name        trimmed, uppercased name
     * @param department  trimmed department name
     * @param hoursWorked validated non-negative hours worked
     * @param hourlyRate  validated non-negative hourly rate
     * @param calculator  supplies the payroll rules
     */
    public Employee(int employeeId, String name, String department, BigDecimal hoursWorked,
                    BigDecimal hourlyRate, PayrollCalculator calculator) {
        this.employeeId = employeeId;
        this.name = name;
        this.department = department;
        this.hoursWorked = hoursWorked;
        this.hourlyRate = hourlyRate;
        this.grossPay = calculator.calculateGrossPay(hoursWorked, hourlyRate, department);
        this.payLevel = PayLevel.forGrossPay(this.grossPay);
        this.employmentStatus = EmploymentStatus.forHours(hoursWorked);
    }

    /**
     * Returns the employee identifier.
     *
     * @return the employee identifier
     */
    public int getEmployeeId() {
        return employeeId;
    }

    /**
     * Returns the uppercased employee name.
     *
     * @return the employee name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the trimmed department name.
     *
     * @return the department name
     */
    public String getDepartment() {
        return department;
    }

    /**
     * Returns hours worked as parsed, without output rounding applied.
     *
     * @return hours worked
     */
    public BigDecimal getHoursWorked() {
        return hoursWorked;
    }

    /**
     * Returns the hourly rate as parsed, without output rounding applied.
     *
     * @return the hourly rate
     */
    public BigDecimal getHourlyRate() {
        return hourlyRate;
    }

    /**
     * Returns gross pay, already rounded to two decimal places.
     *
     * @return gross pay
     */
    public BigDecimal getGrossPay() {
        return grossPay;
    }

    /**
     * Returns the pay band for this employee.
     *
     * @return the pay level
     */
    public PayLevel getPayLevel() {
        return payLevel;
    }

    /**
     * Returns the employment status for this employee.
     *
     * @return the employment status
     */
    public EmploymentStatus getEmploymentStatus() {
        return employmentStatus;
    }

    /**
     * Renders this employee as one comma-separated output row.
     *
     * <p>Rounding applied here is presentation only; it never affects {@link #grossPay},
     * which was calculated from the unrounded rate.
     *
     * @return the CSV row, without a trailing newline
     */
    public String toCsvRow() {
        return String.join(",",
                String.valueOf(employeeId),
                name,
                department,
                formatForOutput(hoursWorked),
                formatForOutput(hourlyRate),
                formatForOutput(grossPay),
                payLevel.toString(),
                employmentStatus.toString());
    }

    /**
     * Formats a value with exactly two decimal places and no scientific notation.
     *
     * @param value the value to format
     * @return the formatted value
     */
    private static String formatForOutput(BigDecimal value) {
        return value.setScale(OUTPUT_SCALE, RoundingMode.HALF_UP).toPlainString();
    }
}
