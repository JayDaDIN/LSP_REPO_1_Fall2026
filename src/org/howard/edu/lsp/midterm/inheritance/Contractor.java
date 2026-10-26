package org.howard.edu.lsp.midterm.inheritance;

/**
 * An independent contractor paid a fifteen-percent premium on all hours worked.
 *
 * @author Justin Dunbar
 */
public class Contractor extends Employee {

    private static final double PREMIUM_MULTIPLIER = 1.15;

    /**
     * Creates a contractor.
     *
     * @param hoursWorked the number of hours worked
     * @param hourlyRate the hourly rate
     */
    public Contractor(double hoursWorked, double hourlyRate) {
        super(hoursWorked, hourlyRate);
    }

    /**
     * Calculates pay with the contractor premium applied to all hours.
     *
     * @return calculated pay
     */
    @Override
    public double calculatePay() {
        return getHoursWorked() * getHourlyRate() * PREMIUM_MULTIPLIER;
    }
}
