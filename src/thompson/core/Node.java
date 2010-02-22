package thompson.core;

import java.util.*;

class Node {
  Node left, right, parent;
  int index;
  
  Node() {}
  
  Node(Node left, Node right) {
    this.setLeft(left);
    this.setRight(right);
    this.left.setParent(this);
    this.right.setParent(this);
  }
  
  void setLeft(Node left) {
    assert this != left;
    this.left = left;
  }
  
  void setRight(Node right) {
    assert this != right;
    this.right = right;
  }

  void setParent(Node parent) {
    assert this != parent;
    this.parent = parent;
  }
  
  public String toString() {
    if (this.isLeaf()) {
      return "*";
    } else {
      return "(" + left.toString() + right.toString() + ")";
    }
  }

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
  
  void indexLeaves() {
    this.indexLeavesFrom(0);
  }
  
  int indexLeavesFrom(int from) {
    if (this.isLeaf()) {
      this.index = from;
      return from + 1;
    } else {
      int newFrom = this.left.indexLeavesFrom(from);
      return this.right.indexLeavesFrom(newFrom);
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
    for (int i = (numCarets - 1); i >= 0; i--) {
      Node caret = carets.get(i);
      if (caret.isLeftEdge()) {
        caretTypes[i] = (i == 0) ? CaretType.L0 : CaretType.LL;
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
  
  // mutates self
  void rotateRight() {
    assert this.left.isCaret();
    Node a = this.left.left;
    Node b = this.left.right;
    Node c = this.right;
    Node d = new Node();
    this.setLeft(a);
    a.setParent(this);
    this.setRight(d);
    d.setParent(this);
    d.setLeft(b);
    b.setParent(d);
    d.setRight(c);
    c.setParent(d);
  }
  
  // mutates self
  void rotateLeft() {
    assert this.right.isCaret();
    Node a = this.left;
    Node b = this.right.left;
    Node c = this.right.right;
    Node d = new Node();
    this.setRight(c);
    c.setParent(this);
    this.setLeft(d);
    d.setParent(this);
    d.setLeft(a);
    a.setParent(d);
    d.setRight(b);
    b.setParent(d);
  }
  
  // mutates self
  void prune() {
    this.setLeft(null);
    this.setRight(null);
  }
  
  // mutates self
  void grow() {
    this.setLeft(new Node());
    this.setRight(new Node());
    this.left.setParent(this);
    this.right.setParent(this);
  }
  
  void replace(Node with) {
    if (this.isLeftChild()) {
      this.parent.setLeft(with);
      with.setParent(this.parent);
    } else {
      assert this.isRightChild();
      this.parent.setRight(with);
      with.setParent(this.parent);
    }
  }
  
  Node copy() {
    if (this.isLeaf()) {
      return new Node();
    } else {
      Node left = this.left.copy();
      Node right = this.right.copy();
      Node copy = new Node();
      copy.setLeft(left);
      copy.left.setParent(copy);
      copy.setRight(right);
      copy.right.setParent(copy);
      return copy;
    }
  }
}