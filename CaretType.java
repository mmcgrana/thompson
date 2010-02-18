class CaretType {
  static final int R0  = 0;
  static final int RNI = 1;
  static final int RI  = 2;
  static final int LL  = 3;
  static final int I0  = 4;
  static final int IR  = 5;
  static final int L0  = 6;

  static final int[][] WEIGHTS = {{0,2,2,1,1,3},
                                  {2,2,2,1,1,3},
                                  {2,2,2,1,3,3},
                                  {1,1,1,2,2,2},
                                  {1,1,3,2,2,4},
                                  {3,3,3,2,4,4}};

  static int contribution(int minusType, int plusType) {
    if ((minusType == L0) || (plusType == L0)) {
      assert ((minusType == L0) && (plusType == L0));
      return 0;
    } else {
      return WEIGHTS[minusType][plusType];
    }
  }
}