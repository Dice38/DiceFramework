import datalists.DiceSkipList;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import static org.assertj.core.api.Assertions.*;
public class DiceSkipListTest {
    DiceSkipList<Integer> skipList;
    static LinkedList<Integer> integerList = new LinkedList<>();
    static LinkedList<Integer> integerLongList = new LinkedList<>();

    @BeforeAll
    static void initialSetUp(){
        for(int i = 0; i < 100000; i++){
            integerList.add((int)Math.pow(i,2)%10000);
        }
        for(int i = 0; i < 1000000; i++){
            integerLongList.add((int)Math.pow(i,2)%10000);
        }
    }
    @BeforeEach
    void setUp(){
        skipList = new DiceSkipList<Integer>();
    }

    @Test
    void testAdd() {
        skipList.addAll(integerList);
        skipList.add(-50);
        skipList.add(11995);
        assertThat(skipList.contains(-50)).isTrue();
        assertThat(skipList.contains(11995)).isTrue();
        assertThat(skipList.contains(857267)).isFalse();
    }
    @Test
    void testAddSpeed(){
        skipList.addAll(integerLongList);
    }

    @Test
    void testRemove(){
        skipList.addAll(integerList);
        skipList.add(-50);
        skipList.remove(-50);
        assertThat(skipList.contains(-50)).isFalse();
    }

    @Test
    void testSize(){
        skipList.addAll(integerList);
        assertThat(skipList.size()).isEqualTo(integerList.size());
    }
}
