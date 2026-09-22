package org.howard.edu.lsp.assignment3;

import java.io.PrintStream;

/**
 * The counts reported at the end of a run.
 *
 * <p>Assignment 2 built this summary from loose integers and printed it with four
 * {@code System.out.println} calls inside {@code main}. Making it an object gives the
 * skipped-row arithmetic a single home and lets the pipeline hand off a value that
 * already knows how to present itself.
 *
 * @author Justin Dunbar
 */
public class RunSummary {

    /** Every non-header line encountered, including blank and malformed ones. */
    private final int rowsRead;

    /** Rows that passed validation and were written to the output file. */
    private final int rowsTransformed;

    /** Output location as reported to the console. */
    private final String outputPath;

    /**
     * Creates a summary for one completed run.
     *
     * @param rowsRead        all non-header lines encountered
     * @param rowsTransformed rows written to the output file
     * @param outputPath      output location, in display form
     */
    public RunSummary(int rowsRead, int rowsTransformed, String outputPath) {
        this.rowsRead = rowsRead;
        this.rowsTransformed = rowsTransformed;
        this.outputPath = outputPath;
    }

    /**
     * Returns the number of non-header lines encountered.
     *
     * @return rows read
     */
    public int getRowsRead() {
        return rowsRead;
    }

    /**
     * Returns the number of rows written to the output file.
     *
     * @return rows transformed
     */
    public int getRowsTransformed() {
        return rowsTransformed;
    }

    /**
     * Returns the number of rows that were read but not written.
     *
     * @return rows skipped
     */
    public int getRowsSkipped() {
        return rowsRead - rowsTransformed;
    }

    /**
     * Prints the summary, one value per line.
     *
     * @param out destination stream
     */
    public void printTo(PrintStream out) {
        out.println("Rows read: " + rowsRead);
        out.println("Rows transformed: " + rowsTransformed);
        out.println("Rows skipped: " + getRowsSkipped());
        out.println("Output file: " + outputPath);
    }
}
