package org.howard.edu.lsp.midterm.crccards;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Maintains the library's books and coordinates borrowing and returning.
 *
 * @author Justin Dunbar
 */
public class Library {

    private static final int MAX_BORROWED_BOOKS = 3;

    private final Map<String, Book> books = new LinkedHashMap<>();

    /**
     * Creates an empty library.
     */
    public Library() {
    }

    /**
     * Adds a book to this library.
     *
     * @param book the book to add
     * @throws IllegalArgumentException if the ISBN is already in the library
     */
    public void addBook(Book book) {
        if (books.containsKey(book.getIsbn())) {
            throw new IllegalArgumentException("A book with this ISBN already exists.");
        }
        books.put(book.getIsbn(), book);
    }

    /**
     * Finds a book by ISBN.
     *
     * @param isbn the ISBN to search for
     * @return the matching book, or {@code null} if none exists
     */
    public Book findBook(String isbn) {
        return books.get(isbn);
    }

    /**
     * Borrows a book from this library.
     *
     * @param isbn the ISBN of the book to borrow
     * @throws IllegalArgumentException if no book has the ISBN
     * @throws IllegalStateException if the book is already borrowed or the limit is reached
     */
    public void borrowBook(String isbn) {
        Book book = requireBook(isbn);
        if (getBorrowedBookCount() >= MAX_BORROWED_BOOKS) {
            throw new IllegalStateException("The borrowing limit has been reached.");
        }
        book.borrowBook();
    }

    /**
     * Returns a borrowed book to this library.
     *
     * @param isbn the ISBN of the book to return
     * @throws IllegalArgumentException if no book has the ISBN
     * @throws IllegalStateException if the book is already available
     */
    public void returnBook(String isbn) {
        requireBook(isbn).returnBook();
    }

    /**
     * Returns a snapshot of all currently available books.
     *
     * @return the available books
     */
    public List<Book> getAvailableBooks() {
        List<Book> availableBooks = new ArrayList<>();
        for (Book book : books.values()) {
            if (book.isAvailable()) {
                availableBooks.add(book);
            }
        }
        return availableBooks;
    }

    /**
     * Returns books whose author matches the supplied name exactly.
     *
     * @param author the author name to match
     * @return matching books, including borrowed books
     */
    public List<Book> getBooksByAuthor(String author) {
        List<Book> matchingBooks = new ArrayList<>();
        for (Book book : books.values()) {
            if (author == null ? book.getAuthor() == null : author.equals(book.getAuthor())) {
                matchingBooks.add(book);
            }
        }
        return matchingBooks;
    }

    /**
     * Returns a snapshot of every book sorted by ISBN in ascending order.
     *
     * @return all books sorted by ISBN
     */
    public List<Book> getBooksSortedByIsbn() {
        List<Book> sortedBooks = new ArrayList<>(books.values());
        sortedBooks.sort(Comparator.comparing(Book::getIsbn,
                Comparator.nullsFirst(Comparator.naturalOrder())));
        return sortedBooks;
    }

    /**
     * Returns the number of books currently borrowed from this library.
     *
     * @return the number of borrowed books
     */
    public int getBorrowedBookCount() {
        int borrowedCount = 0;
        for (Book book : books.values()) {
            if (!book.isAvailable()) {
                borrowedCount++;
            }
        }
        return borrowedCount;
    }

    private Book requireBook(String isbn) {
        Book book = books.get(isbn);
        if (book == null) {
            throw new IllegalArgumentException("No book with this ISBN exists.");
        }
        return book;
    }
}
