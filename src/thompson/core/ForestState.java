package thompson.core;

public class ForestState {
  public ForestLabel forestLabel;
  public OfPointer ofPointer;
  public int excess;
  
  public ForestState(ForestLabel forestLabel, OfPointer ofPointer, int excess) {
    this.forestLabel = forestLabel;
    this.ofPointer = ofPointer;
    this.excess = excess;
  }
  
  public int hashCode() {
    return Util.hashCombine(this.forestLabel.hashCode(),
             Util.hashCombine(this.ofPointer.hashCode(),
                              this.excess));
  }
  
  public boolean equals(Object obj) {
    if (!(obj instanceof ForestState)) {
      return false;
    } else {
      ForestState state = (ForestState) obj;
      return ((this.forestLabel == state.forestLabel) &&
              (this.ofPointer == state.ofPointer) &&
              (this.excess == state.excess));
    }
  }
  
  public String toString() {
    return "{" + forestLabel + ", " + ofPointer + ", " + excess + "}";
  }
}