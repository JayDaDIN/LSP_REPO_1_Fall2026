package org.howard.edu.lsp.midterm.inheritance;

/**
 * An employee paid the regular hourly rate for every hour worked.
 *
 * @author Justin Dunbar
 */
public class PartTimeEmployee extends Employee {

    /**
     * Creates a part-time employee.
     *
     * @param hoursWorked the number of hours worked
     * @param hourlyRate the hourly rate
     */
    public PartTimeEmployee(double hoursWorked, double hourlyRate) {
        super(hoursWorked, hourlyRate);
    }

    /**
     * Calculates straight hourly pay.
     *
     * @return calculated pay
     */
    @Override
    public double calculatePay() {
        return getHoursWorked() * getHourlyRate();
    }
}
