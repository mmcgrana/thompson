package thompson.core;

import java.math.BigInteger;

public class BackPointer {
  public ForestKey backKey;
  public BigInteger backCount;
  
  BackPointer(ForestKey backKey, BigInteger backCount) {
    this.backKey = backKey;
    this.backCount = backCount;
  }
}
