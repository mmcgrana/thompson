package thompson.core;

import java.util.*;

class TreePair {
  Node minusRoot, plusRoot;

  TreePair(Node minusRoot, Node plusRoot) {
    this.minusRoot = minusRoot;
    this.plusRoot = plusRoot;
  }

  TreePair copy() {
    return new TreePair(this.minusRoot.copy(), this.plusRoot.copy());
  }
  
  public String toString() {
    return "[" + minusRoot.toString() + "|" + plusRoot.toString() + "]"; 
  }

  BaseExponent toNormalForm() {
    BaseExponent normalForm = new BaseExponent();
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
        normalForm.addTerm(index, -exponent);
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

  // mutates self
  void reduce() {
    boolean passNeeded = true;
    while (passNeeded) {
      passNeeded = false;
      ArrayList<Node> minusLeaves = this.minusRoot.leaves();
      ArrayList<Node> plusLeaves  = this.plusRoot.leaves();
      int numLeaves = minusLeaves.size();
      assert numLeaves == plusLeaves.size();
      for (int i = 0; i < numLeaves - 1; i++) {
        Node minusA = minusLeaves.get(i);
        Node minusB = minusLeaves.get(i+1);
        if (minusA.parent == minusB.parent) {
          Node plusA = plusLeaves.get(i);
          Node plusB = plusLeaves.get(i+1);
          if (plusA.parent == plusB.parent) {
            minusA.parent.prune();
            plusA.parent.prune();
            passNeeded = true;
          }
        }
      }
    }
  }
  
  void unifyFrom(Node plus,  ArrayList<Node> plusComplements,
                 Node minus, ArrayList<Node> minusComplements) {
    if (plus.isLeaf() && minus.isLeaf()) {
      return;
    } else if (plus.isCaret() && minus.isCaret()) {
      unifyFrom(plus.left, plusComplements, minus.left, minusComplements);
      unifyFrom(plus.right, plusComplements, minus.right, minusComplements);
    } else if (plus.isLeaf() && minus.isCaret()) {
      plus.replace(minus.copy());
      plusComplements.get(plus.index).replace(minus.copy());
    } else {
      assert plus.isCaret() && minus.isLeaf();
      minus.replace(plus.copy());
      minusComplements.get(minus.index).replace(plus.copy());
    }
  }

  // mutates both
  void unify(TreePair treePairLeft, TreePair treePairRight) {
    Node plus = treePairLeft.plusRoot;
    Node minus = treePairRight.minusRoot;
    plus.indexLeaves();
    minus.indexLeaves();
    ArrayList<Node> plusComplements = treePairLeft.minusRoot.leaves();
    ArrayList<Node> minusComplements = treePairRight.plusRoot.leaves();
    unifyFrom(plus, plusComplements, minus, minusComplements);
  }
  
  // mutates self, arg
  void rightMultiplyBy(TreePair treePair) {
    unify(treePair, this);
    this.minusRoot = treePair.minusRoot;
    this.reduce();
  }

  static TreePair X0 =
    new TreePair(new Node(new Node(), new Node(new Node(), new Node())),
                 new Node(new Node(new Node(), new Node()), new Node()));
  static TreePair X0_INVERSE =
    new TreePair(new Node(new Node(new Node(), new Node()), new Node()),
                 new Node(new Node(), new Node(new Node(), new Node())));
  static TreePair X1 =
    new TreePair(new Node(new Node(), new Node(new Node(), new Node(new Node(), new Node()))),
                 new Node(new Node(), new Node(new Node(new Node(), new Node()), new Node())));
  static TreePair X1_INVERSE =
    new TreePair(new Node(new Node(), new Node(new Node(new Node(), new Node()), new Node())),
                 new Node(new Node(), new Node(new Node(), new Node(new Node(), new Node()))));

  static TreePair product(ArrayList<TreePair> factors) {
    int numFactors = factors.size();
    TreePair accum = factors.get(0).copy();
    for (int i = 1; i < numFactors; i++) {
      accum.rightMultiplyBy(factors.get(i).copy());
    }
    return accum;
  }
}
