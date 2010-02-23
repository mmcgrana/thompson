package thompson.core;

public class CaretType {
  public static final int R0  = 0;
  public static final int RNI = 1;
  public static final int RI  = 2;
  public static final int LL  = 3;
  public static final int I0  = 4;
  public static final int IR  = 5;
  public static final int L0  = 6;

  private static final int[][] WEIGHTS = {{0,2,2,1,1,3},
                                          {2,2,2,1,1,3},
                                          {2,2,2,1,3,3},
                                          {1,1,1,2,2,2},
                                          {1,1,3,2,2,4},
                                          {3,3,3,2,4,4}};

  public static int contribution(int minusType, int plusType) {
    if ((minusType == L0) || (plusType == L0)) {
      if (!((minusType == L0) && (plusType == L0))) {
        throw new IllegalArgumentException();
      }
      return 0;
    } else {
      return WEIGHTS[minusType][plusType];
    }
  }
}