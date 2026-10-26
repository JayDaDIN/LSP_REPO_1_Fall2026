package org.howard.edu.lsp.midterm.inheritance;

/**
 * Base abstraction for an employee paid according to hours worked and an hourly rate.
 *
 * @author Justin Dunbar
 */
public abstract class Employee {

    private final double hoursWorked;
    private final double hourlyRate;

    /**
     * Creates an employee with a work schedule and hourly rate.
     *
     * @param hoursWorked the number of hours worked
     * @param hourlyRate the hourly rate
     * @throws IllegalArgumentException if either value is negative
     */
    public Employee(double hoursWorked, double hourlyRate) {
        if (hoursWorked < 0 || hourlyRate < 0) {
            throw new IllegalArgumentException("Hours worked and hourly rate must be nonnegative.");
        }
        this.hoursWorked = hoursWorked;
        this.hourlyRate = hourlyRate;
    }

    /**
     * Returns the number of hours worked.
     *
     * @return hours worked
     */
    public double getHoursWorked() {
        return hoursWorked;
    }

    /**
     * Returns the employee's hourly rate.
     *
     * @return hourly rate
     */
    public double getHourlyRate() {
        return hourlyRate;
    }

    /**
     * Calculates pay according to this employee's pay rules.
     *
     * @return calculated pay
     */
    public abstract double calculatePay();
}
