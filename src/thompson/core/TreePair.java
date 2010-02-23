package thompson.core;

import java.util.*;

public class TreePair {
  private Node minusRoot, plusRoot;

  public TreePair(Node minusRoot, Node plusRoot) {
    if ((minusRoot == null) || (plusRoot == null)) {
      throw new IllegalArgumentException();
    }
    this.minusRoot = minusRoot;
    this.plusRoot = plusRoot;
  }

  public TreePair copy() {
    return new TreePair(this.minusRoot.copy(), this.plusRoot.copy());
  }
  
  public String toString() {
    return "[" + minusRoot.toString() + "|" + plusRoot.toString() + "]"; 
  }

  public BaseExponent toNormalForm() {
    // size the product
    int numTerms = 0;
    for (Node leaf : this.plusRoot.leaves()) {
      if (leaf.exponent() > 0) { numTerms++; }
    }
    for (Node leaf : this.minusRoot.leaves()) {
      if (leaf.exponent() > 0 ) { numTerms++; }
    }
    int[] bases = new int[numTerms];
    int[] exponents = new int[numTerms];
    
    // fill the product
    int i = 0;
    ArrayList<Node> leaves = this.plusRoot.leaves();
    int numLeaves = leaves.size();
    for (int index = 0; index < numLeaves; index++) {
      int exponent = leaves.get(index).exponent();
      if (exponent > 0) {
        bases[i] = index;
        exponents[i] = exponent;
        i++;
      }
    }
    leaves = this.minusRoot.leaves();
    for (int index = numLeaves - 1; index >= 0; index--) {
      int exponent = leaves.get(index).exponent();
      if (exponent > 0) {
        bases[i] = index;
        exponents[i] = -exponent;
        i++;
      }
    }
    return new BaseExponent(bases, exponents);
  }

  private static Node fromTermStraight(int base, int exponent) {
    Node root = new Node();
    Node tail = root;
    for (int i = 0; i < base + exponent + 2; i++) {
      tail.setLeft(new Node());
      tail.setRight(new Node());
      tail = tail.right;
    }
    return root;
  }

  private static Node fromTermBent(int base, int exponent) {
    Node root = new Node();
    root.setLeft(new Node());
    root.setRight(new Node());
    Node tail = root;    
    for (int i = 0; i < base; i++) {
      tail = tail.right;
      tail.setLeft(new Node());
      tail.setRight(new Node());
    }
    for (int i = 0; i < exponent + 1; i++) {
      tail = tail.left;
      tail.setLeft(new Node());
      tail.setRight(new Node());
    }
    return root;
  }

  public static TreePair fromTerm(int base, int exponent) {
    if ((exponent == 0) || (base < 0)) {
      throw new IllegalArgumentException();
    }
    if (exponent < 0) {
      return new TreePair(fromTermBent(base, -exponent),
                          fromTermStraight(base, -exponent));
    } else {
      return new TreePair(fromTermStraight(base, exponent),
                          fromTermBent(base, exponent));
    }
  }

  // Returns the world length with respect to the {x_0,x_1} generating set
  public int wordLength() {
    int[] minusTypes = minusRoot.caretTypes();
    int[] plusTypes  = plusRoot.caretTypes();
    int numCarets = minusTypes.length;
    int length = 0;
    for (int i = 0; i < numCarets; i++) {
      length += CaretType.contribution(minusTypes[i], plusTypes[i]);
    }
    return length;
  }

  // Eliminates common carrots between the tree pairs
  // Mutates the instance
  // OPTIMIZE: more efficient reduction approach
  private void reduce() {
    boolean passNeeded = true;
    while (passNeeded) {
      passNeeded = false;
      ArrayList<Node> minusLeaves = this.minusRoot.leaves();
      ArrayList<Node> plusLeaves  = this.plusRoot.leaves();
      int numLeaves = minusLeaves.size();
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
  
  // Helper for unify
  private static void unifyFrom(Node plus, ArrayList<Node> plusComplements,
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
      minus.replace(plus.copy());
      minusComplements.get(minus.index).replace(plus.copy());
    }
  }

  // Modifies the tree pairs so that the plus tree of the left is the same
  // as the minus tree of the right.
  // Mutates both instances
  private static void unify(TreePair treePairLeft, TreePair treePairRight) {
    Node plus = treePairLeft.plusRoot;
    Node minus = treePairRight.minusRoot;
    plus.indexLeaves();
    minus.indexLeaves();
    ArrayList<Node> plusComplements = treePairLeft.minusRoot.leaves();
    ArrayList<Node> minusComplements = treePairRight.plusRoot.leaves();
    unifyFrom(plus, plusComplements, minus, minusComplements);
  }
  
  // Returns fg
  public static TreePair multiply(TreePair f, TreePair g) {
    TreePair fCopy = f.copy();
    TreePair gCopy = g.copy();
    unify(gCopy, fCopy);
    TreePair product = new TreePair(gCopy.minusRoot, fCopy.plusRoot);
    product.reduce();
    return product;
  }

  // Returns the product of the given factors
  public static TreePair product(TreePair[] factors) {
    int numFactors = factors.length;
    TreePair accum = factors[0];
    for (int i = 1; i < numFactors; i++) {
      accum = multiply(accum, factors[i]);
    }
    return accum;
  }
}
