package thompson.core;

public class ForestKey {
  public int weight;
  public ForestState upperState, lowerState;
  
  public ForestKey(int weight, ForestState upperState, ForestState lowerState) {
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
