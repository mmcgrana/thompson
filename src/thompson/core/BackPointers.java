package thompson.core;

import java.util.ArrayList;
import java.math.BigInteger;

public class BackPointers {
  public ArrayList<BackPointer> backPointers;
  public BigInteger totalBackCount;
  
  BackPointers(BigInteger totalBackCount) {
    this.backPointers = new ArrayList<BackPointer>();
    this.totalBackCount = totalBackCount;
  }
  
  public String toString() {
    return totalBackCount.toString();
  }
}