package datacomponents;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public abstract class DiceAbstractCollection<T> implements Collection<T> {
    //Attributes
    /*
    modCount that counts the number of modifications performed on the datastructure
     */
    protected volatile AtomicInteger modCount = new AtomicInteger(0);
    /*
    Enum that shows if the Collection is Sorted. Default is Unsorted
     */
    protected Order order = Order.UNSORTED;
    /*
    Enum that shows if the Collection allows for null Elements
     */
    protected Nullable nullable = Nullable.NULLABLE;
    /*
    Enum that shows if the Collection is read only
     */
    protected ReadOnly readOnly = ReadOnly.READ_WRITE;

    /*
    ADDING METHODS!
    Methods that perform Adding operations. General operations that rely on a Data Structure specific implementation
    of the add(Object) method.
     */
    public abstract boolean add(T value);


    /**
     * Adds all the elements in a collection
     * @param collection collection containing elements to be added to this collection
     * @return true if all operations have been completed successfully
     */
    public boolean addAll(Collection<? extends T> collection){
        for(T value:collection){
            this.add(value);
        }
        return true;
    }
    /**
     * Method that allows adding elements from a collection to the ArrayList if they meet a certain condition.
     * @param collection The collection of Objects that is checked for values that conform to the passed Filter
     * @param filter The Filter Functional Interface. Implements some condition
     * @return Returns true if the ArrayList changed as a result of this method call
     */
    public boolean addIf(Collection<? extends T> collection, Predicate<T> filter){
        int tempModCount = this.modCount.get();
        for(T value:collection){
            if(filter.test(value)) this.add(value);
        }
        return tempModCount != this.modCount.get();
    }

    /*
    REMOVAL METHODS!
     */
    public abstract void clear();

    public abstract boolean remove(Object obj);

    /**
     * Removes all the instances of all the elements in the collection from the ArrayList.
     * @param collection collection containing elements to be removed from this collection
     * @return true if the ArrayList has been modified as a result of this call.
     */
    public boolean removeAll(Collection<?> collection){
        int mod = 0;
        for(Object obj:collection){
            if(this.remove(obj))mod++;
        }
        return mod != 0;
    }

    /**
     *
     * @param filter a predicate which returns {@code true} for elements to be
     *        removed
     * @return True if the ArrayList has been changed as a result of this call.
     */
    public boolean removeIf(Predicate<? super T> filter){
        var iter = this.diceIterator();
        int localMod = 0;

        while(iter.hasNext()){
            if(filter.test(iter.next())){
                iter.remove();
                localMod++;
            }
        }
        return localMod!=0;
    }

    /**
     * Only Retains elements that are also contained in the parameter Collection.
     * O(n^2) if contains() implements lineal search. O(nlogn) if contains() implements binary search 
     * @param collection collection containing elements to be retained in this collection
     * @return true if the ArrayList changed as a Result of this Method call
     */
    public boolean retainAll(Collection<?> collection){
        int tempModCount = 0;
        var iter = this.diceIterator();

        while(iter.hasNext()){
            if(!(collection.contains(iter.next()))){
                iter.remove();
                tempModCount++;
            }
        }
        
        return tempModCount != this.modCount.get();
    }

    /*
    MODIFYING METHODS!
     */

    /**
     * Method that irreversibly sets the DataStructure to ReadOnly. Once flagged no elements can be modified in any way.
     */
    public void setReadOnly(){
        this.readOnly = ReadOnly.READ_ONLY;
        this.modCount.incrementAndGet();
    }
    /**
     * Method that replaces all the values in the dataStructure by performing the modification specified in the
     * UnaryOperator on them and saving the result
     * @param operator Specifies the modification performed on each value T
     */
    public void modifyAll(UnaryOperator<T> operator){
        var iter = this.diceIterator();

        while(iter.hasNext()){
            iter.next();
            iter.modify(operator);
        }
    }

    /**
     * Method that performs some modification on all the values in the datastructure that fulfill the condition set
     * in the filter.
     * @param operator Specifies the modification performed on each value T
     * @param filter Only values that pass this filter are modified by operator
     */
    public void modifyIf(UnaryOperator<T> operator, Predicate<T> filter){
        var iter = this.diceIterator();

        while(iter.hasNext()){
            if(filter.test(iter.next())){
                iter.modify(operator);
            }
        }
    }

    /*
    OTHER METHODS!
     */

     /**
      * Method that gets all elements of the collection to fulfill the condition passed in Predicate
      * @param filter The conditionary filter
      * @return Array of all the Elements in the collection that passed the filter
      */
    public T[] getAll(Predicate<T> filter){
        var result = new LinkedList<T>(); 
        
        for(T value:this){
            if(filter.test(value)){
                result.add(value);
            }
        }

        return this.castTypeArray(result.toArray());
    }
    /**
     * Searches for an Object in O(n) linear search. Override for more efficient time complexity
     * @param obj element whose presence in this collection is to be tested
     * @return true if at least one instance of obj is an element of the ArrayList
     */
    public boolean contains(Object obj){
        var iter = this.iterator();

        while(iter.hasNext()){
            if(this.diceEquals(iter.next(), obj)) return true;
        }
        return false;
    }

    /**
     * Checks if all elements are contained in O(n^2)
     * @param collection collection to be checked for containment in this collection
     * @return true if all objects of the collection are part of the ArrayList
     */
    public boolean containsAll(Collection<?> collection){
        for(Object obj:collection){
            if(!this.contains(obj))return false;
        }
        return true;
    }


    /**
     * Checks if the datastructure contains zero elements
     * @return true if there are zero elements in the datastructure.
     */
    public boolean isEmpty(){
        return this.size() == 0;
    }

    /**
     * Returns all the values in the datastructure sequentially as a string. It is convention that each value is separated
     * by "," and " ".
     * @return
     */
    public String toString(){
        var stringBuilder = new StringBuilder();
        for(T value:this){
            stringBuilder.append(value.toString() + ", ");
        }

        return stringBuilder.toString();
    }

    public abstract Iterator<T> iterator();

    public abstract DiceIterator<T> diceIterator();

    /*
    META METHODS
     */

    /**
     * Method that returns the number of Elements in the current datastructure
     * @return Integer representing the number of elements
     */
    public abstract int size();

    /**
     * Method that checks if the DataStructure is declared readOnly.
     * @return true if the DataStructure is ReadOnly
     */
    public boolean isReadOnly(){
        return this.readOnly == ReadOnly.READ_ONLY;
    }

    /**
     * Method that checks of the DataStructure allows null Elements
     * @return true if null Elements are allowed
     */
    public boolean allowsNullElements(){
        return this.nullable == Nullable.NULLABLE;
    }

    /**
     * Method that checks if the DataStructure is Ordered
     * @return
     */
    public boolean isOrdered(){
        return this.order == Order.SORTED;
    }

    /*
     * Internal Methods
     */

    /**
     * Method for all DiceDataStructures to create generic T[] Arrays.
     * @param size The size of the new generic Arrray
     * @return Generic Array of the specififed size
     */
    @SuppressWarnings ("unchecked")
    protected T[] castTypeArray(int size){
        if(size<1) throw new IllegalArgumentException("Passed Array Capacity is smaller than one");
    
        return (T[]) new Object[size];
    }

    @SuppressWarnings ("unchecked")
    protected T[] castTypeArray(Object[] convertArray){

        return(T[])convertArray;
    }

    /**
     * Internal Method to compare two Objects. Necessary to avoid difficulty if one of the Objects is null
     * @param firstValue
     * @param secondValue
     * @return Returns true if both Values are null or if equals() returns true.
     */
    protected boolean diceEquals(Object firstValue, Object secondValue){
        if(firstValue == null || secondValue == null){
            return firstValue == secondValue;
        }

        return firstValue.equals(secondValue);

    }
    /**
     * Extension of the Iterator interface that provides additional Functionality. Used for functionality in all DiceDataStructures within the framework.
     */
        public interface DiceIterator<T> extends Iterator<T>{
        /**
         * Method that modifies the current Element with the operation specified
         * @param operation The operation applied to the last element returned by next()
         */
        public abstract void modify(UnaryOperator<T> operation);

    }
}
