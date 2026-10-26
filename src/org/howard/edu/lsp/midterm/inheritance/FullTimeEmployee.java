package org.howard.edu.lsp.midterm.inheritance;

/**
 * An employee whose hours beyond forty in a week earn overtime pay.
 *
 * @author Justin Dunbar
 */
public class FullTimeEmployee extends Employee {

    private static final double REGULAR_HOURS = 40.0;
    private static final double OVERTIME_MULTIPLIER = 1.5;

    /**
     * Creates a full-time employee.
     *
     * @param hoursWorked the number of hours worked
     * @param hourlyRate the hourly rate
     */
    public FullTimeEmployee(double hoursWorked, double hourlyRate) {
        super(hoursWorked, hourlyRate);
    }

    /**
     * Calculates straight pay through forty hours and time-and-a-half thereafter.
     *
     * @return calculated pay
     */
    @Override
    public double calculatePay() {
        double regularHours = Math.min(getHoursWorked(), REGULAR_HOURS);
        double overtimeHours = Math.max(0.0, getHoursWorked() - REGULAR_HOURS);
        return (regularHours * getHourlyRate())
                + (overtimeHours * getHourlyRate() * OVERTIME_MULTIPLIER);
    }
}
