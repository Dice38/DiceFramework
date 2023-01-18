package datalists;

import datacomponents.AccessType;
import datacomponents.DiceAbstractCollection;

import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;


public class ArrayList<T> extends DiceAbstractCollection<T> {
    //Static Parameters
    public static int defaultInitialCapacity = 10;
    public static float resizeFactor = (float)0.5;
    public static AccessType accessType = AccessType.RANDOM;
    //Attributes
    private T[] baseArray;
    private int currentCapacity;
    private int size = 0;

    /*
    Constructors
     */
    public ArrayList(int initialCapacity){this.baseArray = this.castTypeArray(initialCapacity);}

    public ArrayList(){this(defaultInitialCapacity);}

    public ArrayList(Collection<? extends T> collection){
        this(collection.size());
        this.addAll(collection);
    }

    @SuppressWarnings("unchecked")
    public ArrayList(T ... values){
        this(values.length*3);
        Collections.addAll(this, values);
    }

    //Methods
    /*
    Modifying Methods that either add, remove, replace, or change the ArrayList in some other way
     */

    /*
    Base Adding Method. All other adding methods use this one to perform the actual adding. Since the ArrayList
    offeres random access the base adding method is not add(T value) but add(index, value).
     */
    public boolean add(int index, T value){
        this.checkIndexRange(index);

        for(int i = this.size+1; i > index; i-- ){
            this.baseArray[i] = this.baseArray[i-1];
        }

        this.baseArray[index] = value;
        this.size++;
        this.resize();
        this.modCount.incrementAndGet();

        return true;
    }

    public boolean add(T value){
        this.baseArray[this.size()] = value;
        this.size++;
        this.resize();
        this.modCount.incrementAndGet();

        return true;
    }

    /*
    All Removal Operations
     */
    /*
    The base removal operation that all other removing operations are based on.
     */

    /**
     * Removes Element at specific Index. Is the underlying removal operation for all removals in ArrayList
     * @param index
     * @return
     */
    public boolean remove(int index){
        this.checkIndexRange(index);

        //shift every subsequent element to the left by one
        for(int i = index; i < this.size-1; i++){
            this.baseArray[index] = this.baseArray[index+1];
        }
        //reduce size
        this.size--;
        this.modCount.incrementAndGet();
        return true;
    }

    /**
     * Method that removes ALL instances of the passed object from the collection
     * @param obj element to be removed from this collection, if present
     * @return Returns true if elements have been removed as a consequence of this call
     */
    public boolean remove(Object obj){
        var iter = this.iterator();
        boolean bFlag = false;

        if(obj == null){
            while(iter.hasNext()){
                if(iter.next() == null){
                    iter.remove();
                    bFlag = true;
                }
            }
        }else{
            while(iter.hasNext()){
                if(iter.next().equals(obj)){
                    iter.remove();
                    bFlag = true;
                }
            }
        }

        return bFlag;
    }

    public boolean replaceAt(int index, T value){
        this.checkIndexRange(index);

        this.baseArray[index] = value;
        this.modCount.incrementAndGet();
        return true;
    }

    /*
    Non-Modifying Methods that don't change the ArrayList @
     */

    /**
     *
     * @return Returns true if the ArrayList contains no elements
     */
    public boolean isEmpty(){
        return this.size == 0;
    }

    /**
     * Clears the ArrayList of all elements and resets the capacity to standard capacity
     */
    public void clear(){
        this.size = 0;
        this.baseArray = this.castTypeArray(defaultInitialCapacity);
    }


    /**
     * @return Returns Iterator Object for the current Collection. Fail-Fast
     */
    public Iterator<T> iterator(){
        return new ListIter();
    }

    public DiceIterator<T> diceIterator(){
        return new ListIter();
    }

    /**
     * Returns the Element at the specified position.
     * @param index The index of the returned Element.
     * @throws ArrayIndexOutOfBoundsException If Index is not within 0<Index<size.
     */
    public T get(int index) {
        this.checkIndexRange(index);
        return this.baseArray[index];
    }

    @Override
    public int size() {
        return this.size;
    }

    /**
     * Provides an Array containing all the elements in the ArrayList in O(n)
     * @return Object[] with the length of the ArrayLists size
     */
    @Override
    public Object[] toArray(){
        Object[] objArray = new Object[this.size];
        System.arraycopy(this.baseArray, 0, objArray, 0, this.size);
        return objArray;
    }

    /**
     *
     * @param array the array into which the elements of this collection are to be
     *        stored, if it is big enough; otherwise, a new array of the same
     *        runtime type is allocated for this purpose.
     * @return Either the newly created Array or the passed Array with the index size+1 being assigned the null reference
     * as a demarcation.
     * @param <E> Type of the passed Array. Must be compatible
     */
    @SuppressWarnings("unchecked")
    public <E> E[] toArray(E[] array){
        if(array == null) throw new NullPointerException("Passed null object as Array");

        if(array.length <= this.size) {
            return (E[]) Arrays.copyOf(this.baseArray, this.size, array.getClass());
        }else{
            System.arraycopy(this.baseArray, 0, array, 0, this.size);
            array[this.size] = null;
            return array;
        }
    }


    /*
    Internal Methods only for use within the class
     */

    /*
    Method that checks whether a resize is necessary and performs it if so
     */
    private void resize(){

        if(this.size == this.currentCapacity){

            T[] array = this.castTypeArray((int)(currentCapacity*1.5));
            System.arraycopy(this.baseArray,0, array, 0, this.size);
            this.baseArray = array;
            this.currentCapacity = baseArray.length;
        }
    }


    /*
    Method to check an Index for Range. If it's outside the bounds of the used ArrayList an Exception is thrown.
     */
    private void checkIndexRange(int index){
        if(index < 0 || index >= this.size) throw new IndexOutOfBoundsException("Index out of Bound at "+index);
    }


    /*
    ITERATOR
    List Iterator Implementation that offers increased functionality over the standard Iterator
     */
    private class ListIter implements ListIterator<T>, DiceIterator<T>{
        //Attributes
        private Cursor cursor;

        private int expectedModCount;

        private LastOperation lastOperation = LastOperation.FIRST;

        //Constructors
        public ListIter(){
            this(0);
        }
        public ListIter(int startIndex){
            ArrayList.this.checkIndexRange(startIndex);
            this.cursor = new Cursor(startIndex -1, startIndex);
        }

        //Methods
        public void add(T value){
            this.checkModification();
            if(this.lastOperation == LastOperation.MODIFIED) throw new IllegalStateException("Element has already been modified since last next() call");

            ArrayList.this.add(this.cursor.getNextIndex(), value);
            this.cursor.moveForward();
            this.expectedModCount++;
            this.lastOperation = LastOperation.MODIFIED;
        }

        public boolean hasNext(){
            return this.cursor.nextIndex < ArrayList.this.size();
        
        }

        public boolean hasPrevious(){
            return this.cursor.getPreviousIndex() > -1;
        }

        public T next(){
            this.checkModification();
            if(!this.hasNext()) throw new NoSuchElementException("The Collection has no next Element");
            if(this.lastOperation == LastOperation.FIRST) expectedModCount = ArrayList.this.modCount.get();

            this.cursor.moveForward();

            this.lastOperation = LastOperation.NEXT;

            return ArrayList.this.get(cursor.getPreviousIndex());

        }
        
        public int nextIndex(){
            return this.cursor.getNextIndex();
        }

        public T previous(){
            this.checkModification();
            if(!this.hasPrevious()) throw new NoSuchElementException("The Collection has no previous Element");
            if(this.lastOperation == LastOperation.FIRST) expectedModCount = ArrayList.this.modCount.get();

            this.cursor.moveBackward();

            this.lastOperation = LastOperation.PREVIOUS;

            return ArrayList.this.get(cursor.getNextIndex());
        }

        public int previousIndex(){
            return this.cursor.getPreviousIndex();
        }

        public void remove(){
            this.checkModification();
            if(this.lastOperation == LastOperation.MODIFIED) throw new IllegalStateException("Element has already been modified since last next() call");

            if(this.lastOperation == LastOperation.NEXT){
                ArrayList.this.remove(this.cursor.getPreviousIndex());

            }else if(this.lastOperation == LastOperation.PREVIOUS){
                ArrayList.this.remove(this.cursor.getNextIndex());
            }

            this.lastOperation = LastOperation.MODIFIED;
            this.expectedModCount++;
        }

        public void set(T value){
            this.checkModification();
            if(this.lastOperation == LastOperation.MODIFIED) throw new IllegalStateException("Element has already been modified since last next() call");

            if(this.lastOperation == LastOperation.NEXT){
                ArrayList.this.baseArray[this.cursor.previousIndex] = value;
            }else if(this.lastOperation == LastOperation.PREVIOUS){
                ArrayList.this.baseArray[this.cursor.nextIndex] = value;
            }

            this.lastOperation = LastOperation.MODIFIED;
            this.expectedModCount++;
            ArrayList.this.modCount.incrementAndGet();

        }

        public void modify(UnaryOperator<T> operator){
            this.checkModification();
            if(this.lastOperation == LastOperation.MODIFIED) throw new IllegalStateException("Element has already been modified since last next() call");

            if(this.lastOperation == LastOperation.NEXT){
                ArrayList.this.baseArray[this.cursor.previousIndex] = operator.apply(ArrayList.this.baseArray[this.cursor.previousIndex]);
            }else if(this.lastOperation == LastOperation.PREVIOUS){
                ArrayList.this.baseArray[this.cursor.nextIndex] = operator.apply(ArrayList.this.baseArray[this.cursor.previousIndex]);
            }

            
            this.lastOperation = LastOperation.MODIFIED;
            this.expectedModCount++;
            ArrayList.this.modCount.incrementAndGet();
        }
        /*
         * Internal Methods
         */

        /*
        Checks the if the List has been modified in an unchecked manner while the Iterator is active.
        Compares the expected Modification count with the current modCount.
         */
        private void checkModification(){
            if(this.expectedModCount != ArrayList.this.modCount.get())
                throw new ConcurrentModificationException("ArrayList has been Modified");

        }

        enum LastOperation{NEXT, PREVIOUS, MODIFIED, FIRST}
        
        }
        /*
         * Internal Cursor Class that saves both the previous and the succeeding position
         */
        private class Cursor{
            //Attributes
            private int previousIndex;
            private int nextIndex;

            //Cunstructor
            public Cursor(int previousIndex, int nextIndex){
                this.previousIndex = previousIndex;
                this.nextIndex = nextIndex;
            }

            //Method
            public void moveForward(){
                this.previousIndex++;
                this.nextIndex++;
            }

            public void moveBackward(){
                this.previousIndex--;
                this.nextIndex--;
            }

            public int getNextIndex(){
                return this.nextIndex;
            }

            public int getPreviousIndex(){
                return this.previousIndex;
            }
        }
    }

