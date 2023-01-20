package datatest;
import datalists.DiceArrayList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ArrayListTest {

    DiceArrayList list = new DiceArrayList(1,5,66,53453,12,1,4);

    @Test
    void add_Element(){
        Assertions.assertTrue(list.add(5));
    }


}
