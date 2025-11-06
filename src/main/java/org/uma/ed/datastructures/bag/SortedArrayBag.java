package org.uma.ed.datastructures.bag;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class SortedArrayBag<T extends Comparable<? super T>> implements Bag<T>{

    private final static int INITIAL_CAPACITY = 5;
    protected T[] elements;
    protected int[] count;
    protected int nextFree;

    public SortedArrayBag(){
        this(INITIAL_CAPACITY);
    }

    @SuppressWarnings("unchecked")
    public SortedArrayBag(int n){
        this.elements = (T[]) new Comparable[n];
        this.count = new int[n];
        this.nextFree = 0;
    }

    public SortedArrayBag<T> empty(){
        return new SortedArrayBag<>();
    }

    private void ensureCapacity(){
        if(this.nextFree == this.elements.length-1){
            this.elements = Arrays.copyOf(this.elements, 2*this.elements.length);
            this.count = Arrays.copyOf(this.count, 2*this.count.length);
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEmpty(){
        return nextFree == 0;
    }

    /**
     * {@inheritDoc}
     */
    public int size(){
        int size = 0;
        for(int i = 0; i < this.nextFree; i++){
            size += this.count[i];
        }
        return size;
    }



    /**
     * Finder is a utility class used to locate an element within the sorted array of the SortedArrayBag.
     * It performs a binary search to find the position of an element.
     * After a search operation:
     * - If the element is found:
     *   - `found` is set to true.
     *   - `index` is assigned the position of the element within the array.
     * - If the element is not found:
     *   - `found` is set to false.
     *   - `index` indicates the position where the element would be inserted to maintain the sorted order of the array.
     * This class is used to implement the insert, contains, and delete operations in a way that avoids redundant
     * searches.
     */
    private final class Finder {
        boolean found;
        int index;

        /**
         * Constructs a Finder and performs a binary search for the specified element.
         * After construction, the `found` and `index` fields are set according to the outcome of the search.
         *
         * @param element the element to search for
         */
        Finder(T element) {
            found = false;
            int left = 0, right = nextFree - 1, mid = 0;
            while (!found && left <= right) {
                mid = left + (right - left) / 2;
                int cmp = elements[mid].compareTo(element);
                if (cmp == 0) {
                    found = true;
                } else if (cmp < 0) {
                    left = mid + 1;
                } else {
                    right = mid - 1;
                }
            }
            index = found ? mid : left;
        }
    }

    private void shiftRight (int index){
        ensureCapacity();
        System.arraycopy(this.elements, index, this.elements, index + 1, this.nextFree - index);
        System.arraycopy(this.count, index, this.count, index + 1, this.nextFree - index);
        this.nextFree++;
    }

    private void shiftLeft (int index){
        System.arraycopy(this.elements, index + 1, this.elements, index, this.nextFree - index - 1);
        System.arraycopy(this.count, index + 1, this.count, index, this.nextFree - index - 1);
        this.nextFree--;
    }

    /**
     * {@inheritDoc}
     */
    public void insert(T item){
        Finder finder = new Finder(item);
        if(finder.found){
            this.count[finder.index]++;
        }else{
            shiftRight(finder.index);
            this.elements[finder.index] = item;
            this.count[finder.index] = 1;
        }
    }

    /**
     * {@inheritDoc}
     */
    public int occurrences(T item){
        Finder finder = new Finder(item);
        return finder.found ? this.count[finder.index] : 0;
    }

    /**
     * {@inheritDoc}
     */
    public void delete(T item){
        Finder finder = new Finder(item);
        if(!finder.found) {
           return;
        }

        this.count[finder.index]--;

        if(this.count[finder.index] == 0){
            shiftLeft(finder.index);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void clear(){
        for(int i = 0; i < this.nextFree; i++){
            this.elements[i] = null;
            this.count[i] = 0;
        }
        this.nextFree = 0;
    }

    @Override
    public Iterator<T> iterator() {
        return new BagIterator();
    }

    /** Invariant conditions:
     * - `current` refers to the index holding the next element to be returned, or is 0 if no elements remain.
     * - `returned` tracks the count of elements already yielded from the current node.
     */
    private final class BagIterator implements Iterator<T> {
        int current;
        int returned;

        BagIterator() {
            current = 0;
            returned = 0;
        }

        public boolean hasNext() {
            return (current < nextFree);
        }

        public T next() {
            if(!hasNext()){
                throw new NoSuchElementException("No more elements in the bag");
            }

            T elemento = elements[current];
            returned++;
            if (returned >= count[current]) {
                current++;
                returned = 0;
            }
            return elemento;
        }
    }

}
