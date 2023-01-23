package dataalgorithms;

import java.util.Comparator;

/*
 * UNTESTED !!!
 */
public class QuickSort{

    public static<T extends Comparable<T>> void quickSort(T[] array){

        if(array == null) throw new NullPointerException("Passed null argument");

        quickSort(array, 0, array.length, Comparator.naturalOrder());
    }

    public static <T extends Comparable<T>> void quickSort(T[] array, int iStart, int iEnd, Comparator<T> comparator){

    /*
     * Base Case
     */
        if(iEnd <= iStart) return;

    /*
     * Recursion Call
     */
    T pivot = array[iStart];
    int left = iStart + 1;
    int right = iEnd;

    while(left < right){
        while(left < right && comparator.compare(array[left], pivot) <= 0) left++;
        while(left < right && comparator.compare(array[right], pivot) >0 ) right--;
        
        T temp = array[left];
        array[left] = array[right];
        array[right] = temp;
    }
    if(comparator.compare(pivot, array[left]) >= 0)left++;

    array[iStart] = array[left];
    array[left] = pivot;

    quickSort(array, iStart, left, comparator);
    quickSort(array, left+1, iEnd, comparator);
    } 
}