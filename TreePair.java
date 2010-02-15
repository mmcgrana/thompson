import java.util.*;

class TreePair {
  static class CaretType {
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

  class Node {
    Node left, right, parent;

    boolean isRoot() {
      return parent == null;
    }

    boolean isLeaf() {
      return this.left == null;
    }

    boolean isCaret() {
      return this.left != null;
    }

    boolean isLeftChild() {
      return this.parent.left == this;
    }

    boolean isRightChild() {
      return this.parent.right == this;
    }

    boolean isLeftEdge() {
      return (this.isRoot()) ||
             (this.isLeftChild() && this.parent.isLeftEdge());
    }

    boolean isRightEdge() {
      return (this.isRoot()) ||
             (this.isRightChild() && this.parent.isRightEdge());
    }

    boolean isInterior() {
      return !(this.isLeftEdge() || this.isRightEdge());
    }

    int exponent() {
      if (this.isRightChild() || this.parent.isRightEdge()) {
        return 0;
      } else {
        return this.parent.exponent() + 1;
      }
    }

    int numCarets() {
      if (this.isLeaf()) {
        return 0;
      } else {
        return 1 + this.left.numCarets() + this.right.numCarets();
      }
    }

    int numLeafs() {
      return numCarets() + 1;
    }

    ArrayList<Node> leaves() {
      ArrayList<Node> leaves = new ArrayList<Node>();
      addLeaves(leaves, this);
      return leaves;
    }

    void addLeaves(ArrayList<Node> leaves, Node node) {
      if (node.isLeaf()) {
        leaves.add(node);
      } else {
        addLeaves(leaves, node.left);
        addLeaves(leaves, node.right);
      }
    }

    ArrayList<Node> carets() {
      ArrayList<Node> carets = new ArrayList<Node>();
      addCarets(carets, this);
      return carets;
    }

    void addCarets(ArrayList<Node> carets, Node node) {
      if (node.isCaret()) {
        addCarets(carets, node.left);
        carets.add(node);
        addCarets(carets, node.right);
      }
    }

    int[] caretTypes() {
      ArrayList<Node> carets = this.carets();
      int numCarets = this.numCarets();
      int[] caretTypes = new int[numCarets];
      int prevInteriorIdx = -1;
      for (int i = (numCarets - 1); i >= 0; i++) {
        Node caret = carets.get(i);
        if (caret.isLeftEdge()) {
          caretTypes[i] = caret.isRoot() ? CaretType.L0 : CaretType.LL;
        } else if (caret.isInterior()) {
          prevInteriorIdx = i;
          caretTypes[i] = caret.right.isLeaf() ? CaretType.I0: CaretType.IR;
        } else {
          assert caret.isRightEdge();
          if (prevInteriorIdx == -1) {
            caretTypes[i] = CaretType.R0;
          } else {
            caretTypes[i] = prevInteriorIdx == (i + 1) ? CaretType.RI : CaretType.RNI;
          }
        }
      }
      return caretTypes;
    }
  }

  Node minusRoot, plusRoot;

  NormalForm toNormalForm() {
    NormalForm normalForm = new NormalForm();
    ArrayList<Node> leaves = this.plusRoot.leaves();
    int numLeaves = leaves.size();
    for (int index = 0; index < numLeaves; index++) {
      int exponent = leaves.get(index).exponent();
      if (exponent > 0) {
        normalForm.addTerm(index, exponent);
      }
    }
    leaves = this.minusRoot.leaves();
    for (int index = numLeaves - 1; index >= 0; index--) {
      int exponent = leaves.get(index).exponent();
      if (exponent > 0) {
        normalForm.addTerm(index, exponent);
      }
    }
    return normalForm;
  }

  int wordLength() {
    int[] minusTypes = minusRoot.caretTypes();
    int[] plusTypes  = plusRoot.caretTypes();
    int numCarets = minusTypes.length;
    int length = 0;
    for (int i = 0; i < numCarets; i++) {
      length += CaretType.contribution(minusTypes[i], plusTypes[i]);
    }
    return length;
  }
}
