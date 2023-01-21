import datalists.DiceArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.LinkedList;

import static org.assertj.core.api.Assertions.*;

public class ArrayListTest {

    DiceArrayList<Integer> list;
    LinkedList<Integer> secondList;
    @BeforeEach
    void setup(){
        int[] temp = {5,1,100};
        list = new DiceArrayList<Integer>(1,2,66);
        secondList = new LinkedList<Integer>();

        for(int i = 0; i<temp.length; i++){
            secondList.add(temp[i]);
        }
    }

    /*
    CONSTRUCTOR TEST
     */
    @ParameterizedTest
    @ValueSource(ints = {0,-1})
        void initial_Capacity_Constructor(int capacity){
            assertThatThrownBy(()->new DiceArrayList<Integer>(capacity)).isInstanceOf(IllegalArgumentException.class);
    }
    @Test
    void pass_Collection_To_Constructor(){
        assertThat(new DiceArrayList<Integer>(secondList)).containsAll(secondList).hasSize(3);
    }

    /*
    TEST ADDING METHODS
     */

   @Test
    void add_Element_At_Index(){
        list.add(2,88);
        assertThat(list.get(2)).isEqualTo(88);

        Throwable thrown = catchThrowable(()->list.add(list.size(),5));
        assertThat(thrown).isInstanceOf(IndexOutOfBoundsException.class);
    }
    @Test
    void add_Element(){
       list.add(100);
       assertThat(list.get(list.size()-1)).isEqualTo(100);
    }

    @Test
    void remove_Element(){
        list.add(null);
        list.remove(null);
        assertThat(list.contains(null)).isFalse();
    }
    @ParameterizedTest
    @ValueSource(ints = {-1, 100})
    void replace_At(int index){
       assertThatThrownBy(()->list.replaceAt(index,10));

       list.replaceAt(1,50);
       assertThat(list.get(1)).isEqualTo(50);
    }

    @Test
    void remove_If_Greater(){
       list.removeIf(n->n>3);
       assertThat(list).allMatch(n->n<=3);
    }

    @Test
    void contains_All_From_Collection(){
       assertThat(list.containsAll(secondList)).isFalse();
       list.addAll(secondList);
       assertThat(list.containsAll(secondList)).isTrue();
    }

    @Test
    void modify_All(){
       var tempList = new DiceArrayList<Integer>(list);
       tempList.modifyAll(n->2*n);
       for(int value:tempList){
           assertThat(list).contains(value/2);
       }
    }






}
