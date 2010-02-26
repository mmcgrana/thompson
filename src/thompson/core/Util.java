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
  
  public static int hashCombine(int seed, int hash) {
	  seed ^= hash + 0x9e3779b9 + (seed << 6) + (seed >> 2);
	  return seed;
  }
}
