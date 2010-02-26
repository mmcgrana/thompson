package thompson.core;

import java.util.*;

public class WordSample {
  static class ForestLabel {
    static final int I = 0;
    static final int N = 1;
    static final int L = 2;
    static final int R = 3;
    static final int X = 4;
  }

  static class ForestState {
    private int forestLabel;
    private boolean leftOfPointer;
    private int excess;
    
    ForestState(int forestLabel, boolean leftOfPointer, int excess) {
      this.forestLabel = forestLabel;
      this.leftOfPointer = leftOfPointer;
      this.excess = excess;
    }
    
    public int hashCode() {
      return Util.hashCombine(this.forestLabel, this.excess) +
             (this.leftOfPointer ? 0 : 1);
    }
    
    public boolean equals(Object obj) {
      if (!(obj instanceof ForestState)) {
        return false;
      } else {
        ForestState state = (ForestState) obj;
        return ((this.forestLabel == state.forestLabel) &&
                (this.leftOfPointer == state.leftOfPointer) &&
                (this.excess == state.excess));
      }
    }
    
    public String toString() {
      return "{" + forestLabel + ", " + leftOfPointer + ", " + excess + "}";
    }
  }

  static class TableElem {
    int weight;
    ForestState upperState, lowerState;
    
    TableElem(int weight, ForestState upperState, ForestState lowerState) {
      this.weight = weight;
      this.upperState = upperState;
      this.lowerState = lowerState;
    }
    
    public int hashCode() {
      return Util.hashCombine(upperState.hashCode(),
               Util.hashCombine(lowerState.hashCode(), this.weight));
    }
    
    public boolean equals(Object obj) {
      if (!(obj instanceof TableElem)) {
        return false;
      } else {
        TableElem elem = (TableElem) obj;
        return ((this.weight == elem.weight) &&
                this.upperState.equals(elem.upperState) &&
                this.lowerState.equals(elem.lowerState));
      }
    }
    
    public String toString() {
      return "<" + weight + "," +
                   upperState.toString() + "," +
                   lowerState.toString() + ">";
    }
  }
  
  private static ForestState[] updateLeft(ForestState state) {    
    ForestState[] nextStates;
    if (state.forestLabel == ForestLabel.L) {
      nextStates = new ForestState[7];
      nextStates[0] = new ForestState(ForestLabel.L, true,  0);
      nextStates[1] = new ForestState(ForestLabel.N, true,  1);
      nextStates[2] = new ForestState(ForestLabel.I, true,  0);
      nextStates[3] = new ForestState(ForestLabel.N, false, 1);
      nextStates[4] = new ForestState(ForestLabel.I, false, 0);
      nextStates[5] = new ForestState(ForestLabel.R, false, 0);
      nextStates[6] = new ForestState(ForestLabel.X, false, 0);
    } else if ((state.forestLabel == ForestLabel.N) ||
               ((state.forestLabel == ForestLabel.I) && (state.excess > 0))) {
      nextStates = new ForestState[4];
      nextStates[0] = new ForestState(ForestLabel.N, true, state.excess + 1);
      nextStates[1] = new ForestState(ForestLabel.N, true, state.excess);
      nextStates[2] = new ForestState(ForestLabel.I, true, state.excess);
      nextStates[3] = new ForestState(ForestLabel.I, true, state.excess - 1);
    } else if ((state.forestLabel == ForestLabel.I) && (state.excess == 0)) {
      nextStates = new ForestState[3];
      nextStates[0] = new ForestState(ForestLabel.N, true, 1);
      nextStates[1] = new ForestState(ForestLabel.I, true, 0);
      nextStates[2] = new ForestState(ForestLabel.L, true, 0);
    } else {
      nextStates = new ForestState[0];
    }
    return nextStates;
  }
  
  private static ForestState[] updateRight(ForestState state) {    
    ForestState[] nextStates;
    if (state.forestLabel == ForestLabel.R) {
      nextStates = new ForestState[2];
      nextStates[0] = new ForestState(ForestLabel.R, false, 0);
      nextStates[1] = new ForestState(ForestLabel.X, false, 0);
    } else if (state.forestLabel == ForestLabel.X) {
      nextStates = new ForestState[2];
      nextStates[0] = new ForestState(ForestLabel.N, false, 1);
      nextStates[1] = new ForestState(ForestLabel.I, false, 0);
    } else if ((state.forestLabel == ForestLabel.N) ||
              ((state.forestLabel == ForestLabel.I) && (state.excess > 0))) {
      nextStates = new ForestState[4];
      nextStates[0] = new ForestState(ForestLabel.N, false, state.excess + 1);
      nextStates[1] = new ForestState(ForestLabel.N, false, state.excess);
      nextStates[2] = new ForestState(ForestLabel.I, false, state.excess);
      nextStates[3] = new ForestState(ForestLabel.I, false, state.excess - 1);
    } else if ((state.forestLabel == ForestLabel.I) && (state.excess == 0)) {
      nextStates = new ForestState[4];
      nextStates[0] = new ForestState(ForestLabel.N, false, 1);
      nextStates[1] = new ForestState(ForestLabel.I, false, 0);
      nextStates[2] = new ForestState(ForestLabel.R, false, 0);
      nextStates[3] = new ForestState(ForestLabel.X, false, 0);
    } else {
      nextStates = new ForestState[0];
    }
    return nextStates;
  }

  private static final int[][] WEIGHTS = {{2,4,2,1,3},
                                          {4,4,2,3,3},
                                          {2,2,2,1,1},
                                          {1,3,1,2,2},
                                          {3,3,1,2,2}};

  public static int weight(int labelA, int labelB) {
    return WEIGHTS[labelA][labelB];
  }
    
  public static int[] numForestDiagrams(int maxWeight) {
    int[] counts = new int[maxWeight - 3];
    HashMap<TableElem,Integer> totals = new HashMap<TableElem,Integer>();
    totals.put(
      new TableElem(2, new ForestState(ForestLabel.L, true, 0),
                       new ForestState(ForestLabel.L, true, 0)),
      1);
    for (int n = 2; n < maxWeight; n++) {
      TableElem[] iterElems = totals.keySet().toArray(new TableElem[0]);
      for (TableElem tableElem : iterElems) {
        if (tableElem.weight == n) {
          if (totals.get(tableElem) != 0) {
            ForestState upperState = tableElem.upperState;
            ForestState lowerState = tableElem.lowerState;
            ForestState[] upperSet = upperState.leftOfPointer ? updateLeft(upperState) : updateRight(upperState);
            ForestState[] lowerSet = lowerState.leftOfPointer ? updateLeft(lowerState) : updateRight(lowerState);
            for (int u = 0; u < upperSet.length; u++) {
              ForestState upperStateP = upperSet[u];
              for (int l = 0; l < lowerSet.length; l++) {
                ForestState lowerStateP = lowerSet[l];
                if (!((upperStateP.forestLabel == lowerStateP.forestLabel) &&
                      (lowerStateP.forestLabel == ForestLabel.I) &&
                      (upperState.forestLabel != ForestLabel.I) &&
                      (lowerState.forestLabel != ForestLabel.I))) {
                  int weightP = weight(upperStateP.forestLabel, lowerStateP.forestLabel);
                  TableElem updateElem = new TableElem(n + weightP, upperStateP, lowerStateP);
                  Integer updateTotal = totals.get(updateElem);
                  Integer increment = totals.get(new TableElem(n, upperState, lowerState));
                  if (updateTotal == null) { updateTotal = 0; }
                  if (increment == null) { increment = 0; }
                  Integer newTotal = updateTotal + increment;
                  totals.put(updateElem, newTotal);
                }
              }
            }
          }
        }
      }
      if (n >= 3) {
        counts[n-3] = totals.get(new TableElem(n+1,
                                               new ForestState(ForestLabel.R, false, 0),
                                               new ForestState(ForestLabel.R, false, 0)));
      }
    }
    return counts;
  }
}
