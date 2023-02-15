import datalists.DiceSkipList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import static org.assertj.core.api.Assertions.*;
public class DiceSkipListTest {
    DiceSkipList<Integer> skipList;
    LinkedList<Integer> integerList = new LinkedList<>();

    @BeforeEach
    void setUp(){
        skipList = new DiceSkipList<Integer>();
        integerList.add(5);
        integerList.add(null);
        integerList.add(-50);
        integerList.add(0);
    }

    @Test
    void testAdd() {
        skipList.add(integerList.get(0));
        assertThat(skipList.contains(5)).isTrue();
        assertThat(skipList.add(null)).isFalse();
        skipList.add(10);
        skipList.add(-100);
        assertThat(skipList.contains(10)).isTrue();
        assertThat(skipList.contains(199)).isFalse();
        assertThat(skipList.contains(-100)).isTrue();
    }

    @Test
    void testRemove(){
        skipList.addAll(this.integerList);
        skipList.remove(-50);
        assertThat(skipList.contains(-50)).isFalse();
    }


}
