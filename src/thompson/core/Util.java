package thompson.core;

import java.util.*;

public class Util {
  public static int[] toIntArray(ArrayList<Integer> ints) {
    int[] array = new int[ints.size()];
    for (int i = 0; i < ints.size(); i++) {
      array[i] = ints.get(i);
    }
    return array;
  }
}