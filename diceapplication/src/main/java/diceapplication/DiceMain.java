package diceapplication;
import datalists.*;
import java.util.*;

public class DiceMain {
    public static void main(String[] args) {
       int[] array = {1,2,3,4,5};

       for(int i = 0, j = array.length-1; i!=j; i++,j--){
           int iTemp = array[i];
           array[i] = array[j];
           array[j] = array[i];
       }
    }
}