package org.howard.edu.lsp.midterm.crccards;

/**
 * Represents a book in the library lending system.
 *
 * @author Justin Dunbar
 */
public class Book {

    private final String isbn;
    private final String title;
    private final String author;
    private boolean available;

    /**
     * Creates a book that is initially available.
     *
     * @param isbn the book's ISBN
     * @param title the book's title
     * @param author the book's author
     */
    public Book(String isbn, String title, String author) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.available = true;
    }

    /**
     * Returns this book's ISBN.
     *
     * @return the ISBN
     */
    public String getIsbn() {
        return isbn;
    }

    /**
     * Returns this book's title.
     *
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns this book's author.
     *
     * @return the author
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Reports whether this book is available for borrowing.
     *
     * @return {@code true} if available; otherwise {@code false}
     */
    public boolean isAvailable() {
        return available;
    }

    /**
     * Marks this book as borrowed.
     *
     * @throws IllegalStateException if the book is already borrowed
     */
    public void borrowBook() {
        if (!available) {
            throw new IllegalStateException("Book is already borrowed.");
        }
        available = false;
    }

    /**
     * Marks this book as available.
     *
     * @throws IllegalStateException if the book is already available
     */
    public void returnBook() {
        if (available) {
            throw new IllegalStateException("Book is already available.");
        }
        available = true;
    }

    /**
     * Returns a display string containing the book details and availability.
     *
     * @return the book details
     */
    @Override
    public String toString() {
        String status = available ? "AVAILABLE" : "BORROWED";
        return isbn + " " + title + " by " + author + " [" + status + "]";
    }
}
