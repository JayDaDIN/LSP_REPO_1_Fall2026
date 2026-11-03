package org.howard.edu.lsp.assignment5;

import java.util.TreeSet;

/**
 * A mutable mathematical set of integers.
 *
 * <p>Each integer appears at most once. Set-operation methods return independent
 * results and do not modify this set or their argument.</p>
 */
public class IntegerSet {
    private final TreeSet<Integer> elements = new TreeSet<>();

    /**
     * Creates an empty integer set.
     */
    public IntegerSet() {
    }

    /**
     * Removes all elements from this set.
     */
    public void clear() {
        elements.clear();
    }

    /**
     * Returns the number of elements in this set.
     *
     * @return the number of unique integers
     */
    public int length() {
        return elements.size();
    }

    /**
     * Reports whether this set contains the same elements as another set.
     *
     * @param b the set to compare with
     * @return {@code true} if both sets contain exactly the same integers
     */
    public boolean equals(IntegerSet b) {
        return b != null && elements.equals(b.elements);
    }

    /**
     * Reports whether this set contains an integer.
     *
     * @param value the integer to find
     * @return {@code true} if the integer is present
     */
    public boolean contains(int value) {
        return elements.contains(value);
    }

    /**
     * Returns the largest integer in this set.
     *
     * @return the largest integer
     * @throws IllegalStateException if this set is empty
     */
    public int largest() {
        if (elements.isEmpty()) {
            throw new IllegalStateException("Cannot find the largest element of an empty set.");
        }
        return elements.last();
    }

    /**
     * Returns the smallest integer in this set.
     *
     * @return the smallest integer
     * @throws IllegalStateException if this set is empty
     */
    public int smallest() {
        if (elements.isEmpty()) {
            throw new IllegalStateException("Cannot find the smallest element of an empty set.");
        }
        return elements.first();
    }

    /**
     * Adds an integer to this set.
     *
     * @param item the integer to add
     */
    public void add(int item) {
        elements.add(item);
    }

    /**
     * Removes an integer from this set if it is present.
     *
     * @param item the integer to remove
     */
    public void remove(int item) {
        elements.remove(item);
    }

    /**
     * Returns the union of this set and another set.
     *
     * @param b the other set
     * @return a new set containing elements present in either set
     */
    public IntegerSet union(IntegerSet b) {
        IntegerSet result = new IntegerSet();
        result.elements.addAll(elements);
        result.elements.addAll(b.elements);
        return result;
    }

    /**
     * Returns the intersection of this set and another set.
     *
     * @param b the other set
     * @return a new set containing elements present in both sets
     */
    public IntegerSet intersect(IntegerSet b) {
        IntegerSet result = new IntegerSet();
        for (int value : elements) {
            if (b.elements.contains(value)) {
                result.add(value);
            }
        }
        return result;
    }

    /**
     * Returns the difference of this set and another set.
     *
     * @param b the other set
     * @return a new set containing elements in this set but not in {@code b}
     */
    public IntegerSet diff(IntegerSet b) {
        IntegerSet result = new IntegerSet();
        for (int value : elements) {
            if (!b.elements.contains(value)) {
                result.add(value);
            }
        }
        return result;
    }

    /**
     * Returns the assignment-defined complement of this set relative to another:
     * the elements of {@code b} that are not in this set.
     *
     * @param b the other set
     * @return a new set containing elements in {@code b} but not in this set
     */
    public IntegerSet complement(IntegerSet b) {
        return b.diff(this);
    }

    /**
     * Reports whether every element of this set is also in another set.
     *
     * @param b the other set
     * @return {@code true} if this set is a subset of {@code b}
     */
    public boolean isSubsetOf(IntegerSet b) {
        return b.elements.containsAll(elements);
    }

    /**
     * Reports whether this set and another set have no elements in common.
     *
     * @param b the other set
     * @return {@code true} if the sets are disjoint
     */
    public boolean isDisjoint(IntegerSet b) {
        for (int value : elements) {
            if (b.elements.contains(value)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the symmetric difference of this set and another set.
     *
     * @param b the other set
     * @return a new set containing elements present in exactly one set
     */
    public IntegerSet symmetricDiff(IntegerSet b) {
        IntegerSet result = diff(b);
        result.elements.addAll(b.diff(this).elements);
        return result;
    }

    /**
     * Reports whether this set contains no elements.
     *
     * @return {@code true} if this set is empty
     */
    public boolean isEmpty() {
        return elements.isEmpty();
    }

    /**
     * Returns this set's elements in ascending order.
     *
     * @return the elements in square brackets, separated by a comma and a space
     */
    @Override
    public String toString() {
        return elements.toString();
    }
}
