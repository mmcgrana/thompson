package thompson.core;

import java.util.*;

public class Sample {
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

  static class ForestKey {
    int weight;
    ForestState upperState, lowerState;
    
    ForestKey(int weight, ForestState upperState, ForestState lowerState) {
      this.weight = weight;
      this.upperState = upperState;
      this.lowerState = lowerState;
    }
    
    public int hashCode() {
      return Util.hashCombine(upperState.hashCode(),
               Util.hashCombine(lowerState.hashCode(), this.weight));
    }
    
    public boolean equals(Object obj) {
      if (!(obj instanceof ForestKey)) {
        return false;
      } else {
        ForestKey elem = (ForestKey) obj;
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
    
  private static ArrayList<ForestKey> weightNKeys(HashMap<ForestKey,?> web, int n) {
    ArrayList<ForestKey> keys = new ArrayList<ForestKey>();
    for (ForestKey key : web.keySet()) {
      if (key.weight == n) {
        keys.add(key);
      }
    }
    return keys;
  }
  
  public static ArrayList<ForestKey> successorKeys(ForestKey fromKey) {
    ArrayList<ForestKey> toKeys = new ArrayList<ForestKey>();
    ForestState upperState = fromKey.upperState;
    ForestState lowerState = fromKey.lowerState;
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
          ForestKey toKey = new ForestKey(fromKey.weight + weightP, upperStateP, lowerStateP); 
          toKeys.add(toKey);
        }
      }
    }
    return toKeys;
  }

  public static int[] countForestDiagrams(int maxWeight) {
    HashMap<ForestKey,Integer> countWeb = new HashMap<ForestKey,Integer>();
    countWeb.put(
      new ForestKey(2, new ForestState(ForestLabel.L, true, 0),
                       new ForestState(ForestLabel.L, true, 0)),
      1);
    for (int n = 2; n < maxWeight; n++) {
      for (ForestKey fromKey : weightNKeys(countWeb, n)) {
        Integer fromCount = countWeb.get(fromKey);
        for (ForestKey toKey : successorKeys(fromKey)) {
          Integer toCount = countWeb.get(toKey);
          if (toCount == null) { toCount = 0; }
          Integer newCount = toCount + fromCount;
          countWeb.put(toKey, newCount);
        }
      }
    }
    int[] counts = new int[maxWeight-3];
    for (int i = 0; i < maxWeight-3; i++) {
      counts[i] = countWeb.get(new ForestKey(i+4,
                                             new ForestState(ForestLabel.R, false, 0),
                                             new ForestState(ForestLabel.R, false, 0)));
    }
    return counts;
  }
  
  static class BackPointer {
    private ForestKey backKey;
    private int backCount;
    
    BackPointer(ForestKey backKey, int backCount) {
      this.backKey = backKey;
      this.backCount = backCount;
    }
  }
  
  static class BackPointers {
    private ArrayList<BackPointer> backPointers;
    private int totalBackCount;
    
    BackPointers(int totalBackCount) {
      this.backPointers = new ArrayList<BackPointer>();
      this.totalBackCount = totalBackCount;
    }
  }
  
  private static void addBackPointer(BackPointers backPointers, ForestKey backKey, int backCount) {
    backPointers.backPointers.add(new BackPointer(backKey, backCount));
    backPointers.totalBackCount += backCount;
  }
  
  public static HashMap<ForestKey,BackPointers> modelForestDiagrams(int maxWeight) {
    HashMap<ForestKey,BackPointers> modelWeb = new HashMap<ForestKey,BackPointers>();
    modelWeb.put(
      new ForestKey(2, new ForestState(ForestLabel.L, true, 0),
                       new ForestState(ForestLabel.L, true, 0)),
      new BackPointers(1));
    for (int n = 2; n < maxWeight; n++) {
      for (ForestKey fromKey : weightNKeys(modelWeb, n)) {
        BackPointers fromPointers = modelWeb.get(fromKey);
        int fromCount = fromPointers.totalBackCount;
        for (ForestKey toKey : successorKeys(fromKey)) {
          BackPointers toPointers = modelWeb.get(toKey);
          if (toPointers == null) { toPointers = new BackPointers(0); }
          addBackPointer(toPointers, fromKey, fromCount);
          modelWeb.put(toKey, toPointers);
        }
      }
    }
    return modelWeb;
  }
  
  private static ForestKey chooseBackKey(BackPointers backPointers, Random rand) {
    BackPointer chosen = null;
    int finger = rand.nextInt(backPointers.totalBackCount);
    int at = 0;
    for (BackPointer backPointer : backPointers.backPointers) {
      at += backPointer.backCount;
      if (at > finger) {
        chosen = backPointer;
        break;
      }
    }
    if (chosen == null) { throw new RuntimeException("unreachable"); }
    return chosen.backKey;
  }
  
  public static LinkedList<ForestKey> chooseRandomWord(HashMap<ForestKey, BackPointers> modelWeb, int weight) {
    ForestKey atKey = new ForestKey(weight, new ForestState(ForestLabel.R, false, 0),
                                            new ForestState(ForestLabel.R, false, 0));
    if (atKey == null) {
      throw new IllegalArgumentException("Insufficiently deep model");
    }
    ForestKey rootKey = new ForestKey(2, new ForestState(ForestLabel.L, true, 0),
                                         new ForestState(ForestLabel.L, true, 0));
    Random rand = new Random();
    LinkedList<ForestKey> wordKeys = new LinkedList<ForestKey>();
    while (!atKey.equals(rootKey)) {
      wordKeys.addFirst(atKey);
      atKey = chooseBackKey(modelWeb.get(atKey), rand);
    }
    wordKeys.addFirst(rootKey);
    return wordKeys;  
  }
}
