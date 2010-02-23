package thompson.core;

import java.util.*;
import java.util.regex.*;

public class BaseExponent {
  private static final Pattern RE_LEFT_PAREN =   Pattern.compile("\\(");
  private static final Pattern RE_X_UNDERSCORE = Pattern.compile("x_");
  private static final Pattern RE_BASE =         Pattern.compile("\\d+");
  private static final Pattern RE_CARET =        Pattern.compile("\\^");
  private static final Pattern RE_EXPONENT =     Pattern.compile("\\-?\\d+");
  private static final Pattern RE_RIGHT_PAREN =  Pattern.compile("\\)");

  public int[] bases, exponents;

  public BaseExponent(int[] bases, int[] exponents) {
    if (!(bases.length == exponents.length)) {
      throw new IllegalArgumentException();
    }
    this.bases = bases;
    this.exponents = exponents;
  }

  public int numTerms() {
    return this.bases.length;
  }

  private static int[] toIntArray(ArrayList<Integer> ints) {
    int[] array = new int[ints.size()];
    for (int i = 0; i < ints.size(); i++) {
      array[i] = ints.get(i);
    }
    return array;
  }
  
  private static int[] intArrayCopy(int [] ints) {
    int size = ints.length;
    int[] copies = new int[size];
    System.arraycopy(ints, 0, copies, 0, size);
    return copies;
  }

  private static String checkNext(Parser parser, Pattern pattern, String expected) {
    String str;
    if ((str = parser.next(pattern)) == null) {
      throw new IllegalArgumentException("expected " + expected + ", got '" + parser.rest() + "'");
    }
    return str;
  }

  public static BaseExponent fromString(String input) {
    Parser parser = new Parser(input);
    ArrayList<Integer> bases = new ArrayList<Integer>();
    ArrayList<Integer> exponents = new ArrayList<Integer>();

    if (parser.isEnd()) {
      throw new IllegalArgumentException("empty input");
    } else {
      while (!parser.isEnd()) {
        checkNext(parser, RE_LEFT_PAREN, "'('");
        checkNext(parser, RE_X_UNDERSCORE, "'x_'");
        String baseStr = checkNext(parser, RE_BASE, "base");
        checkNext(parser, RE_CARET, "'^'");
        String exponentStr = checkNext(parser, RE_EXPONENT, "exponent");
        checkNext(parser, RE_RIGHT_PAREN, "')'");
        bases.add(Integer.valueOf(baseStr));
        exponents.add(Integer.valueOf(exponentStr));
      }
    }
    return new BaseExponent(toIntArray(bases), toIntArray(exponents));
  }

  public String toString() {
    StringBuilder buf = new StringBuilder();
    for (int i = 0; i < this.numTerms(); i++) {
      int base = this.bases[i];
      int exponent = this.exponents[i];
      buf.append("(x_" + base + "^" + exponent + ")");
    }
    return buf.toString();
  }

  public BaseExponent toNormalForm() {
    int origSize = this.numTerms();
    int[] bases = intArrayCopy(this.bases);
    int[] exponents = intArrayCopy(this.exponents);

    // shuffle
    int leftDone = 0;
    int rightDone = origSize;
    while (leftDone < rightDone - 1) {
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
      for (int i = rightDone - 1; i >= leftDone; i--) {
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
      if ((shufPosBase >= 0) && ((shufNegBase < 0) ||
                                (shufPosBase < shufNegBase))) {
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
        if (!((shufNegBase >= 0) && ((shufPosBase < 0) ||
                                    (shufNegBase <= shufPosBase)))) {
          throw new RuntimeException();                              
        }
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
        rightDone--;
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
      while ((i < origSize) && (bases[i] == coalBase)) {
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

  // public boolean isNormalForm() {
  //
  // }

  // Returns a TreePair corresponding to this instance.
  public TreePair toTreePair() {
    TreePair[] factors = new TreePair[this.numTerms()];
    for (int i = 0; i < this.numTerms(); i++) {
      factors[i] = TreePair.fromTerm(this.bases[i], this.exponents[i]);
    }
    return TreePair.product(factors);
  }
}
