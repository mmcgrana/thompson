package thompson.core;

import java.util.*;
import java.util.regex.*;

public class GenExp {
  private static final Pattern RE_LEFT_PAREN =   Pattern.compile("\\(");
  private static final Pattern RE_X_UNDERSCORE = Pattern.compile("x_");
  private static final Pattern RE_GEN =          Pattern.compile("\\d+");
  private static final Pattern RE_CARET =        Pattern.compile("\\^");
  private static final Pattern RE_EXP =          Pattern.compile("\\-?\\d+");
  private static final Pattern RE_RIGHT_PAREN =  Pattern.compile("\\)");

  public int[] gens, exps;

  public GenExp(int[] gens, int[] exps) {
    if (!(gens.length == exps.length)) {
      throw new IllegalArgumentException();
    }
    this.gens = gens;
    this.exps = exps;
  }

  public int numTerms() {
    return this.gens.length;
  }

  private static String checkNext(Parser parser, Pattern pattern, String expected) {
    String str;
    if ((str = parser.next(pattern)) == null) {
      throw new IllegalArgumentException("expected " + expected + " next, but rest of input is '" + parser.rest() + "'");
    }
    return str;
  }

  public static GenExp fromString(String input) {
    Parser parser = new Parser(input);
    ArrayList<Integer> gens = new ArrayList<Integer>();
    ArrayList<Integer> exps = new ArrayList<Integer>();

    while (!parser.isEnd()) {
      checkNext(parser, RE_LEFT_PAREN, "'('");
      checkNext(parser, RE_X_UNDERSCORE, "'x_'");
      String genStr = checkNext(parser, RE_GEN, "generator");
      checkNext(parser, RE_CARET, "'^'");
      String expStr = checkNext(parser, RE_EXP, "exponent");
      checkNext(parser, RE_RIGHT_PAREN, "')'");
      gens.add(Integer.valueOf(genStr));
      exps.add(Integer.valueOf(expStr));
    }
    return new GenExp(Util.toIntArray(gens),
                            Util.toIntArray(exps));
  }

  public String toString() {
    StringBuilder buf = new StringBuilder();
    for (int i = 0; i < this.numTerms(); i++) {
      int gen = this.gens[i];
      int exp = this.exps[i];
      buf.append("(x_" + gen + "^" + exp + ")");
    }
    return buf.toString();
  }
  
  public boolean equals(Object obj) {
    if (!(obj instanceof GenExp)) {
      return false;
    } else {
      GenExp be = (GenExp) obj;
      return (Arrays.equals(this.gens, be.gens) &&
              Arrays.equals(this.exps, be.exps));
    }
  }
  
  private GenExp coalesce() {
    int[] coalGens = new int[this.numTerms()];
    int[] coalExps = new int[this.numTerms()];
    int i = 0;
    int j = 0;
    while (i < this.numTerms()) {
      if (this.exps[i] == 0) {
        i++;
      } else {
        int coalGen = this.gens[i];
        int coalExp = this.exps[i];
        i++;
        while ((i < this.numTerms()) && (this.gens[i] == coalGen)) {
          if (!(this.exps[i] == 0)) {
            coalExp += this.exps[i];
          }
          i++;
        }
        if (coalExp != 0) {
          coalGens[j] = coalGen;
          coalExps[j] = coalExp;
          j++;
        }
      }
    }
    int[] coalGensTight = new int[j];
    int[] coalExpsTight = new int[j];
    System.arraycopy(coalGens, 0, coalGensTight, 0, j);
    System.arraycopy(coalExps, 0, coalExpsTight, 0, j);
    return new GenExp(coalGensTight, coalExpsTight);
  }
  
  public GenExp toNormalForm() {
    int[] gens = Arrays.copyOf(this.gens, this.gens.length);
    int[] exps = Arrays.copyOf(this.exps, this.exps.length);
      
    // for i < j, a positive, b anything
    //   (x_j^b)(x_i^a) = (x_i^a)(x_j+a^b)      shuffle x_i^a to left   (inc j base by i exp)
    //   (x_i^-a)(x_j^b) = (x_j+a^b)(x_i^-a)    shuffle x_i^-a to right (inc j base by i exp abs)
    // OPTIMIZE: list-based shuffling
    boolean needsShuffle = true;
    while (needsShuffle) {
      needsShuffle = false;
      // find term to shuffle
      for (int i = 0; i < gens.length - 1; i++) {
        int leftGen = gens[i];
        int leftExp = exps[i];
        int rightGen = gens[i+1];
        int rightExp = exps[i+1];
        if ((leftExp == 0) || (rightExp == 0)) {
          // =~ 1 term, no shuffle
        } else if (leftGen == rightGen) {
          // need to coalesce these
          GenExp coal = new GenExp(gens, exps).coalesce();
          gens = coal.gens;
          exps = coal.exps;
          needsShuffle = true;
          break;
        } else if ((leftExp > 0) && (rightExp < 0)) {
          // pos->neg transition, no shuffle
        } else if ((leftExp < 0) && (rightExp > 0)) {
          // neg->pos transition
          if (leftGen < rightGen) {
            // shuffle x_i^-a to right
            gens[i+1] = leftGen;
            exps[i+1] = leftExp;
            gens[i] = rightGen - leftExp;
            exps[i] = rightExp;
          } else {
            // shuffle x_i^a to left
            gens[i] = rightGen;
            exps[i] = rightExp;
            gens[i+1] = leftGen + rightExp;
            exps[i+1] = leftExp;
          }
          needsShuffle = true;
          break;
        } else if ((leftExp < 0) && (rightExp < 0)) {
          if (leftGen > rightGen) {
            // desc neg, no shuffle
          } else {
            // asc neg, shuffle x_i^-a to right
            gens[i+1] = leftGen;
            exps[i+1] = leftExp;
            gens[i] = rightGen - leftExp;
            exps[i] = rightExp;
            needsShuffle = true;
            break;
          }
        } else if ((leftExp > 0) && (rightExp > 0)) {
          if (leftGen < rightGen) {
            // asc pos, no shuffle
          } else {
            // desc pos, shuffle x_i^a to left
            gens[i] = rightGen;
            exps[i] = rightExp;
            gens[i+1] = leftGen + rightExp;
            exps[i+1] = leftExp;
            needsShuffle = true;
            break;
          }
        } else {
          throw new RuntimeException("unreachable: " + leftExp + ", " + rightExp);
        }
      }
    }
    return new GenExp(gens, exps).coalesce();
  }
  
  private static int[][] splitTerms(GenExp be) {
    int firstNI = be.numTerms();
    for (int i = (be.numTerms() - 1); i >= 0; i--) {
      if (be.exps[i] < 0) {
        firstNI = i;
      }
    }

    int pLength = firstNI;
    int nLength = be.numTerms() - firstNI;
    int[] pGens =     new int[pLength];
    int[] pExps = new int[pLength];
    int[] nGens =     new int[nLength];
    int[] nExps = new int[nLength];
    for (int i = 0; i < pLength; i++) {
      pGens[i]     = be.gens[firstNI - 1 - i]; 
      pExps[i] = be.exps[firstNI - 1 - i];
    }
    for (int i = 0; i < nLength; i++) {
      nGens[i] =     be.gens[firstNI + i];
      nExps[i] = be.exps[firstNI + i];
    }
    
    int[][] ret = new int[4][];
    ret[0] = pGens; ret[1] = pExps; ret[2] = nGens; ret[3] = nExps;
    return ret;
  }
  
  public GenExp toUniqueNormalForm() {
    // assume we are working from normal form
    if (!this.isNormalForm()) { throw new IllegalArgumentException(this.toString()); }
    
    int[][] ret = splitTerms(this);
    int[] pGens = ret[0];
    int[] pExps = ret[1];
    int[] nGens = ret[2];
    int[] nExps = ret[3];
    int pLength = pGens.length;
    int nLength = nGens.length;
    
    // uniqueify mirror terms as neccessary
    int pAt = 0;
    int nAt = 0;
    while ((pAt < pLength) && (nAt < nLength)) {
      if (pGens[pAt] == nGens[nAt]) {
        // {p,n}Gap is number of moves needed to get an x_i+1 adjacent
        int pGap = (pAt == 0) ? Integer.MAX_VALUE :
                                pGens[pAt-1] - (pGens[pAt] + 1);
        int nGap = (nAt == 0) ? Integer.MAX_VALUE :
                                nGens[nAt-1] - (nGens[nAt] + 1);
        if ((pGap == 0) || (nGap == 0)) {
          // unique condition met
          pAt++;
          nAt++;
        } else {
          // {p,n}Gap2 is number of moves needed to eliminate x_i on >=1 side
          int pGap2 = pExps[pAt];
          int nGap2 = -nExps[nAt];
          int minGap = Math.min(pGap, Math.min(nGap, Math.min(pGap2, nGap2)));
          // decrement middle exps regardless of whether eliminating or not
          for (int i = 0; i < pAt; i++) {
            pGens[i] -= minGap;
          }
          for (int i = 0; i < nAt; i++) {
            nGens[i] -= minGap;
          } 
          // consider elimination
          if (pGap2 == minGap) {
            for (int i = (pAt + 1); i < pLength; i++) {
              pGens[i-1] = pGens[i];
              pExps[i-1] = pExps[i];
            }
            pLength--;
          } else {
            pExps[pAt] -= minGap;
            pAt++;
          }
          if (nGap2 == minGap) {
            for (int i = (nAt + 1); i < nLength; i++) {
              nGens[i-1] = nGens[i];
              nExps[i-1] = nExps[i];
            }
            nLength--;
          } else {
            nExps[nAt] += minGap;
            nAt++;
          }
        }
      } else if (pGens[pAt] > nGens[nAt]) {
        pAt++;
      } else if (pGens[pAt] < nGens[nAt]) {
        nAt++;
      } else {
        throw new RuntimeException();
      }
    }
    
    // pack remaining terms into unified arrays
    int[] gens =     new int[pLength + nLength];
    int[] exps = new int[pLength + nLength];
    for (int i = 0; i < pLength; i++) {
      gens[i]     = pGens[pLength - 1 - i];
      exps[i] = pExps[pLength - 1 - i];
    }
    for (int i = 0; i < nLength; i++) {
      gens[pLength + i] = nGens[i];
      exps[pLength + i] = nExps[i];
    }
    return new GenExp(gens, exps).coalesce();
  }

  // Returns true of this instance is in normal form. We consider the general
  // normal form, not the unique normal form. See isUnique.
  public boolean isNormalForm() {
    for (int i = 0; i < (this.numTerms() - 1); i++) {
      int prevGen = this.gens[i];
      int prevExp = this.exps[i];
      int nextGen = this.gens[i+1];
      int nextExp = this.exps[i+1];
      if ((prevExp > 0) && (nextExp > 0)) {
        if (!(nextGen > prevGen)) { return false; }
      } else if ((prevExp < 0) && (nextExp < 0)) {
        if (!(nextGen < prevGen)) { return false; }
      } else if ((prevExp > 0) && (nextExp < 0)) {
        // ok
      } else {
        return false;
      }
      prevGen = nextGen;
      prevExp = nextExp;
    }
    return true;
  }
  
  // Returns true if this in unique normal form. See isNormalForm.
  public boolean isUniqueNormalForm() {
    if (!this.isNormalForm()) {
      return false;
    } else {
      return (this.toUniqueNormalForm().equals(this));
    }
  }

  // Returns a TreePair corresponding to this instance.
  // OPTIMIZE: linear tree construction
  public TreePair toTreePair() {
    TreePair[] factors = new TreePair[this.numTerms()];
    for (int i = 0; i < this.numTerms(); i++) {
      factors[i] = TreePair.fromTerm(this.gens[i], this.exps[i]);
    }
    return TreePair.product(factors);
  }
  
  // Returns the inverse of this element
  public GenExp invert() {
    int[] gens = new int[this.numTerms()];
    int[] exps = new int[this.numTerms()];
    for (int i = 0; i < this.numTerms(); i++) {
      gens[this.numTerms() - i - 1] = this.gens[i];
      exps[this.numTerms() - i - 1] = -this.exps[i];
    }
    return new GenExp(gens, exps);
  }

  // Returns fg in normal form
  public static GenExp multiply(GenExp f, GenExp g) {
    GenExp[] factors = {f, g};
    return product(factors);
  }
  
  // Returns the product of the given factors, in normal form.
  public static GenExp product(GenExp[] factors) {
    int totalTerms = 0;
    for (GenExp factor : factors) {
      totalTerms += factor.numTerms();
    }
    int[] gens = new int[totalTerms];
    int[] exps = new int[totalTerms];
    int i = 0;
    for (GenExp factor : factors) {
      System.arraycopy(factor.gens, 0, gens, i, factor.numTerms());
      System.arraycopy(factor.exps, 0, exps, i, factor.numTerms());
      i += factor.numTerms();
    }
    return new GenExp(gens, exps).toNormalForm().toUniqueNormalForm();
  }
}
