package diceapplication;
import datalists.*;
import java.util.*;

public class DiceMain {
    public static void main(String[] args) {
        DiceArrayList<Integer> list = new DiceArrayList<Integer>(14,5);
        for(int i = 2; i <30; i+=3){
            list.add(i);
        }
        Integer[] intArr = {5,2,47};
        var utilList = new LinkedList<Integer>();
        for(int value:intArr){
            utilList.add(value);
        }
        System.out.println(list.toString());
        list.clear();
        System.out.println(list.toString());
    }
}