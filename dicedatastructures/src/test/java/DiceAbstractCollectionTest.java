import datacomponents.DiceAbstractCollection;
import datalists.DiceArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.LinkedList;

import static org.assertj.core.api.Assertions.*;

public class DiceAbstractCollectionTest {
    final private DiceArrayList<String> test = new DiceArrayList<>();
    private DiceAbstractCollection<String> collection;
    private LinkedList<String> javaCollection;
    private LinkedList<String> otherJavaCollection;


    @BeforeEach

    void setUp(DiceAbstractCollection<String> test){
        this.collection = new DiceArrayList<String>();
        collection.add("Hallo");
        collection.add(null);
        collection.add("nicht");

        this.javaCollection = new LinkedList<String>();
        javaCollection.add("nicht");
        javaCollection.add("Geist");

        this.otherJavaCollection = new LinkedList<>();
        otherJavaCollection.add("notHere");
    }

    @Test
    void testAddMethod(){
        collection.add("Element");
        assertThat(collection.contains("Element")).isTrue();
        assertThat(collection.contains("notHere")).isFalse();
    }

    @Test
    void testAddAll(){
        collection.addAll(javaCollection);
        assertThat(collection.containsAll(javaCollection)).isTrue();
        assertThat(collection.containsAll(otherJavaCollection)).isFalse();
    }

    @Test
    void testClear(){
        collection.clear();
        assertThat(collection.isEmpty()).isTrue();
        assertThat(collection.contains("Hallo")).isFalse();

        collection.add("First");
        assertThat(collection.contains("First")).isTrue();

    }

    @Test
    void testAddIf(){
        collection.addIf(javaCollection, n->n.equals("Geist"));
        assertThat(collection.contains("Geist")).isTrue();

        collection.clear();
        collection.addIf(javaCollection, n->true);
        assertThat(collection.containsAll(javaCollection)).isTrue();
    }

    @Test
    void testRemove(){
        collection.remove(null);
        assertThat(collection.size() == 2).isTrue();
        assertThat(collection.contains(null)).isFalse();

        collection.remove("Hallo");
        collection.remove("nicht");
        assertThat(collection.isEmpty()).isTrue();
        assertThat(collection.remove("Not Here")).isFalse();
    }

    @Test
    void testRemoveAll(){
        collection.removeAll(javaCollection);
        assertThat(collection.contains("nicht")).isFalse();
        assertThat(collection.contains(null)).isTrue();
    }

    @Test void testRemoveIf(){
        collection.removeIf(n -> {
            if(n == null) return false;
            return n.equals("Hallo");
        });
        assertThat(collection.contains("Hallo")).isFalse();
    }

    @Test void testRetainAll(){
        collection.retainAll(javaCollection);
        assertThat(collection.contains("nicht")).isTrue();
        assertThat(collection.size()).isEqualTo(1);
    }

    @Test
    void testModifyIf(){
        collection.modifyIf((n) -> {return "Modified";}, n -> {if(n == null) return false; return n.equals("nicht");});
        assertThat(collection.contains("Modified")).isTrue();
        assertThat(collection.contains("nicht")).isFalse();
    }

    @Test
    void testModifyAll(){
        collection.modifyAll((n)->{return "Modified";});
        assertThat(collection).allMatch(n -> n.equals("Modified"));
    }



}
