package thompson.core;

import java.util.*;

class BaseExponent {
  ArrayList<Integer> bases, exponents;
  
  BaseExponent(ArrayList<Integer> bases, ArrayList<Integer> exponents) {
    assert bases.size() == exponents.size();
    this.bases = bases;
    this.exponents = exponents;
  }

  BaseExponent() {
    this(new ArrayList<Integer>(), new ArrayList<Integer>());
  }

  void addTerm(int base, int exponent) {
    this.bases.add(base);
    this.exponents.add(exponent);
  }
  
  BaseExponent toNormalForm() {
    int origSize = this.bases.size();
    Integer[] bases = new Integer[origSize];
    Integer[] exponents = new Integer[origSize];
    bases = this.bases.toArray(bases);
    exponents = this.exponents.toArray(exponents);
    
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
        leftDone += 1;
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
        rightDone += 1;
      }
    }
    
    // coalesce
    ArrayList<Integer> coalBases = new ArrayList<Integer>();
    ArrayList<Integer> coalExponents = new ArrayList<Integer>();
    int i = 0;
    while (i < origSize) {
      int coalBase = bases[i];
      int coalExponent = exponents[i];
      i += 1;
      while (bases[i] == coalBase) {
        coalExponent += exponents[i];
        i += 1;
      }
      if (coalExponent != 0) {
        coalBases.add(coalBase);
        coalExponents.add(coalExponent);
      }
    }
    
    return new BaseExponent(coalBases, coalExponents);
  }

  public String toString() {
    StringBuilder buf = new StringBuilder();
    for (int i = 0; i < this.bases.size(); i++) {
      int base = this.bases.get(i);
      int exponent = this.exponents.get(i);
      buf.append("(x_" + base + "^" + exponent + ")");
    }
    return buf.toString();
  }
}
