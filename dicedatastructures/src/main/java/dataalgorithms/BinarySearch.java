package dataalgorithms;

import java.util.Comparator;

public class BinarySearch {
/*
Central Implementation of the BinarySearch Algorithm for the whole Module.
 */

    /*
    !!MAKE SURE THE ARRAY IS SORTED
     */

    /**
     * Interface for BinarySearch that implements the natural ordering as specified by .compareTo().
     * @param array SORTED array that is supposed to be searched
     * @param value The value the Array is searched for
     * @return The index value of the passed object. If it is not contained in the Array -1 is returned
     * @param <T> Any Type that implements Comparable
     */
   public static<T extends Comparable<T>> int binarySearch(T[] array, T value){
       return binarySearchIndex(array, value, 0, array.length,Comparator.naturalOrder());
   }


    /*
    Type safe implementation of the binarySearch algorithm. Passed Array should not contain null pointers
     */
    private static<T extends Comparable<T>> int binarySearchIndex(T[] array, T value, int iStart, int iEnd, Comparator<T> comparator){

        int iMid = (iStart+iEnd)/2;
        //Check for null Objects.
        try {
            //BaseCase #1: Index iMid is the location of the targeted value
            if (comparator.compare(array[iMid], value) == 0) return iMid;

            //BaseCase #2: The starting Index is lower than the End Index. The searched value is not in the Array
            if (iStart >= iEnd) return -1;

            //Recursive Call
            if (comparator.compare(array[iMid], value) < 0) {
                return binarySearchIndex(array, value, iMid + 1, iEnd, comparator);
            } else {
                return binarySearchIndex(array, value, iStart, iMid - 1, comparator);
            }
        }catch(NullPointerException ex){
            throw new NullPointerException("Array contained null objects");
        }
    }
}
