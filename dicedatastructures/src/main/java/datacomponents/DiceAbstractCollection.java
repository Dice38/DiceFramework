package datacomponents;

import java.util.Collection;
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
    ADDING METHODS!
    Methods that perform Adding operations. General operations that rely on a datastructure specific implementation
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
    Removal Operations that make use of the datastructures iterator.remove() operation. The iterator
    is implemented specifically for each datastructure.
     */
    public abstract boolean remove(Object obj);

    /*
    The following removal methods make use of the specified remove(Object) Method, as that method checks each passed Object
    for null references and uses the saver iterator implementation.
     */
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
        int mod = 0;

        for (T temp : this) {
            if (filter.test(temp)) {
                if(this.remove(temp))mod++;
            }
        }

        return mod != 0;
    }

    /**
     * Only Retains elements that are also contained in the parameter Collection
     * @param collection collection containing elements to be retained in this collection
     * @return true if the ArrayList changed as a Result of this Method call
     */
    public boolean retainAll(Collection<?> collection){
        int tempModCount = this.modCount.get();
        for(Object obj : collection){
            if(!this.contains(obj)){
                this.remove(obj);
            }
        }
        return tempModCount != this.modCount.get();
    }

    /*
    MODIFYING METHODS!
    Methods that modify the elements of the Datastructure
     */

    /**
     * Method that replaces all the values in the datastructure by performing the modification specified in the
     * UnaryOperator on them and saving the result
     * @param operator Specifies the modification performed on each value T
     */
    public abstract void replaceAll(UnaryOperator<T> operator);

    /**
     * Method that performs some modification on all the values in the datastructure that fulfill the condition set
     * in the filter.
     * @param operator Specifies the modification performed on each value T
     * @param filter Only values that pass this filter are modified by operator
     */
    public abstract void replaceIf(UnaryOperator<T> operator, Predicate<T> filter);

    /*
    Methods that don't modify the datastructure and generally return some information to the caller
     */

    /**
     * Searches for an Object in O(n) linear search
     * @param obj element whose presence in this collection is to be tested
     * @return true if at least one instance of obj is an element of the ArrayList
     */
    public boolean contains(Object obj){
        var iter = this.iterator();

        if(obj == null){
            while(iter.hasNext()){
                if(iter.next() == null) return true;
            }
        }else{
            while(iter.hasNext()){
                if(iter.next().equals(obj)) return true;
            }
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
     * Method that returns the number of Elements in the current datastructure
     * @return Integer representing the number of elements
     */
    public abstract int size();

    /**
     * Checks if the datastructure contains zero elements
     * @return true if there are zero elements in the datastructure.
     */
    public abstract boolean isEmpty();

    /**
     * Returns all the values in the datastructure sequentially as a string. It is convention that each value is separated
     * by "," and " ".
     * @return
     */
    public abstract String toString();
}
