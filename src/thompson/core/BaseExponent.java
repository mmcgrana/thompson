package thompson.core;

import java.util.*;

public class BaseExponent {
  public int[] bases, exponents;
  
  public BaseExponent(int[] bases, int[] exponents) {
    if (!(bases.length == exponents.length)) {
      throw new IllegalArgumentException();
    }
    this.bases = bases;
    this.exponents = exponents;
  }
  
  public BaseExponent toNormalForm() {
    int origSize = this.bases.length;
    int[] bases = new int[origSize];
    int[] exponents = new int[origSize];
    System.arraycopy(this.bases, 0, bases, 0, origSize);
    System.arraycopy(this.exponents, 0, exponents, 0, origSize);
    
    // shuffle
    int leftDone = 0;
    int rightDone = origSize;
    while (leftDone != rightDone) {
      // find element to shuffle
      int shufPosBase = -1;
      int shufPosI = -1;
      for (int i = leftDone; i < rightDone; i++) {
        int exponent = exponents[i];
        int base = bases[i];
        if ((exponent > 0) &&
            ((shufPosBase == -1) || (base < shufPosBase))) {
          shufPosBase = base;
          shufPosI = i;  
        }
      }
      int shufNegBase = -1;
      int shufNegI = -1;
      for (int i = rightDone - 1; i >= leftDone; i++) {
        int exponent = exponents[i];
        int base = bases[i];
        if ((exponent < 0) &&
            ((shufNegBase == -1) || (base < shufNegBase))) {
          shufNegBase = base;
          shufNegI = i;  
        }
      }
      // shuffle element pairwise until it gets to done marker
      // update done marker
      // for i < j:
      //   (x_j^b)(x_i^a) = (x_i^a)(x_j+a^b)
      //   (x_i^-a)(x_j^b) = (x_j+a^b)(x_i^-a)
      if (shufPosBase < shufNegBase) {
        for (int i = shufPosI - 1; i >= leftDone; i--) {
          int baseI = bases[i+1];
          int exponentA = exponents[i+1];
          int baseJ = bases[i];
          int exponentB = exponents[i];
          bases[i] = baseI;
          exponents[i] = exponentA;
          bases[i+1] = baseJ + exponentA;
          exponents[i+1] = exponentB; 
        }
        leftDone++;
      } else {
        assert shufNegBase <= shufPosBase;
        for (int i = shufNegI; i < (rightDone - 1); i++) {
          int baseI = bases[i];
          int exponentA = -exponents[i];
          int baseJ = bases[i+1];
          int exponentB = exponents[i+1];
          bases[i] = baseJ + exponentA;
          exponents[i] = exponentB;
          bases[i+1] = baseI;
          exponents[i+1] = -exponentA;
        }
        rightDone++;
      }
    }
    
    // coalesce
    int[] coalBases = new int[origSize];
    int[] coalExponents = new int[origSize];
    int i = 0;
    int j = 0;
    while (i < origSize) {
      int coalBase = bases[i];
      int coalExponent = exponents[i];
      i++;
      while (bases[i] == coalBase) {
        coalExponent += exponents[i];
        i++;
      }
      if (coalExponent != 0) {
        coalBases[j] = coalBase;
        coalExponents[j] = coalExponent;
        j++;
      }
    }
    
    // pack
    int[] coalBasesTight = new int[j];
    int[] coalExponentsTight = new int[j];
    System.arraycopy(coalBases, 0, coalBasesTight, 0, j);
    System.arraycopy(coalExponents, 0, coalExponentsTight, 0, j);    
    return new BaseExponent(coalBasesTight, coalExponentsTight);
  }

  public static BaseExponent fromString(String in) {
    
  }
  
  public String toString() {
    StringBuilder buf = new StringBuilder();
    for (int i = 0; i < this.bases.length; i++) {
      int base = this.bases[i];
      int exponent = this.exponents[i];
      buf.append("(x_" + base + "^" + exponent + ")");
    }
    return buf.toString();
  }
}
