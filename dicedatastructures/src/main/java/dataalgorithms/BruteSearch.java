package dataalgorithms;
import java.util.Iterator;
public class BruteSearch {
    /*
    Method that compares the values of an Iterator with an Object. If a matching Object is found, the index of
    Iterator call is returned. A match with the first next() call would return and Index 0. If no match is found
    -1 is returned.
     */
    public static <T> int bruteSearch(Iterator<T> iterator, Object obj){
        for(int i = 0;iterator.hasNext(); i++){
            if(iterator.next().equals(obj)){
                return i;
            }
        }
        return -1;
    }
}
