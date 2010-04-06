package thompson.core;

import java.util.*;
import java.math.*;

public class Util {
  // Returns a primitive array of ints corresponding to the Integer ArrayList.
  public static int[] toIntArray(ArrayList<Integer> ints) {
    int[] array = new int[ints.size()];
    for (int i = 0; i < ints.size(); i++) {
      array[i] = ints.get(i);
    }
    return array;
  }
  
  // Helper for building hash codes for compound objects.
  public static int hashCombine(int seed, int hash) {
    seed ^= hash + 0x9e3779b9 + (seed << 6) + (seed >> 2);
    return seed;
  }
  
  // like random.nextInt(n), but for BigIntegers.
  public static BigInteger nextRandomBigInteger(BigInteger n, Random rand) {
    BigInteger result = new BigInteger(n.bitLength(), rand);
    while(result.compareTo(n) >= 0) {
      result = new BigInteger(n.bitLength(), rand);
    }
    return result;
  }
}
