package thompson.core;

import java.util.*;

// Represents nodes and their corresponding subtrees in the tree-pair
// representation of elements of F.
public class Node {
  protected Node left, right, parent;
  protected int index;
  
  protected Node() {}
  
  protected Node(Node left, Node right) {
    this.setLeft(left);
    this.setRight(right);
  }
  
  protected void setLeft(Node left) {
    this.left = left;
    if (left != null) {
      left.parent = this;
    }
  }
  
  protected void setRight(Node right) {
    this.right = right;
    if (right != null) {
      right.parent = this;
    }
  }

  protected void setParent(Node parent) {
    this.parent = parent;
  }
  
  public String toString() {
    return this.isLeaf() ? "*" : ("(" + left.toString() + right.toString() + ")");
  }
  
  public boolean equals(Object obj) {
    if (!(obj instanceof Node)) {
      return false;
    } else {
      Node node = (Node) obj;
      if (this.isLeaf()) {
        return node.isLeaf();
      } else {
        return (this.left.equals(node.left) &&
                this.right.equals(node.right));
      }
    }
  }

  public Node root() {
    return isRoot() ? this : this.parent.root();
  }
  
  public boolean isRoot() {
    return this.parent == null;
  }
  
  public boolean isLeaf() {
    return this.left == null;
  }
  
  public boolean isCaret() {
    return this.left != null;
  }
  
  public boolean isLeftChild() {
    return this.parent.left == this;
  }
  
  public boolean isRightChild() {
    return this.parent.right == this;
  }
  
  public boolean isLeftEdge() {
    return (this.isRoot()) ||
           (this.isLeftChild() && this.parent.isLeftEdge());
  }
  
  public boolean isRightEdge() {
    return (this.isRoot()) ||
           (this.isRightChild() && this.parent.isRightEdge());
  }
  
  public boolean isInterior() {
    return !(this.isLeftEdge() || this.isRightEdge());
  }
  
  public int exponent() {
    if (this.isRoot() || this.isRightChild() || this.parent.isRightEdge()) {
      return 0;
    } else {
      return this.parent.exponent() + 1;
    }
  }
  
  public int numCarets() {
    if (this.isLeaf()) {
      return 0;
    } else {
      return 1 + this.left.numCarets() + this.right.numCarets();
    }
  }
  
  public int numLeaves() {
    return numCarets() + 1;
  }
  
  // Returns an inorder List of leaf Nodes
  public ArrayList<Node> leaves() {
    ArrayList<Node> leaves = new ArrayList<Node>();
    addLeaves(leaves, this);
    return leaves;
  }
  
  private void addLeaves(ArrayList<Node> leaves, Node node) {
    if (node.isLeaf()) {
      leaves.add(node);
      // if (!node.isRoot()) {
      //   leaves.add(node);
      // }
    } else {
      addLeaves(leaves, node.left);
      addLeaves(leaves, node.right);
    }
  }
  
  protected void indexLeaves() {
    this.indexLeavesFrom(0);
  }
  
  private int indexLeavesFrom(int from) {
    if (this.isLeaf()) {
      this.index = from;
      return from + 1;
    } else {
      int newFrom = this.left.indexLeavesFrom(from);
      return this.right.indexLeavesFrom(newFrom);
    }
  }
  
  // Returns an inorder List of caret Nodes
  public ArrayList<Node> carets() {
    ArrayList<Node> carets = new ArrayList<Node>();
    addCarets(carets, this);
    return carets;
  }
  
  private void addCarets(ArrayList<Node> carets, Node node) {
    if (node.isCaret()) {
      addCarets(carets, node.left);
      carets.add(node);
      addCarets(carets, node.right);
    }
  }
  
  // Returns an array of CaretTypes corresponding to the inorder carets
  public CaretType[] caretTypes() {
    ArrayList<Node> carets = this.carets();
    int numCarets = this.numCarets();
    CaretType[] caretTypes = new CaretType[numCarets];
    int prevInteriorIdx = -1;
    for (int i = (numCarets - 1); i >= 0; i--) {
      Node caret = carets.get(i);
      if (caret.isLeftEdge()) {
        caretTypes[i] = (i == 0) ? CaretType.L0 : CaretType.LL;
      } else if (caret.isInterior()) {
        prevInteriorIdx = i;
        caretTypes[i] = caret.right.isLeaf() ? CaretType.I0: CaretType.IR;
      } else {
        if (!caret.isRightEdge()) { throw new RuntimeException(); }
        if (prevInteriorIdx == -1) {
          caretTypes[i] = CaretType.R0;
        } else {
          caretTypes[i] = prevInteriorIdx == (i + 1) ? CaretType.RI : CaretType.RNI;
        }
      }
    }
    return caretTypes;
  }
  
  // Destructively removes this instance's left and right children.
  protected void prune() {
    this.setLeft(null);
    this.setRight(null);
  }
  
  // Destructively adds single-node left and right children
  protected void grow() {
    this.setLeft(new Node());
    this.setRight(new Node());
  }
  
  // Destructively replaces this node
  protected void replace(Node with) {
    if (this.isRoot()) {
      this.setLeft(with.left);
      this.setRight(with.right);
    } else if (this.isLeftChild()) {
      this.parent.setLeft(with);
    } else {
      this.parent.setRight(with);
    }
  }
  
  public Node copy() {
    if (this.isLeaf()) {
      return new Node();
    } else {
      Node left = this.left.copy();
      Node right = this.right.copy();
      Node copy = new Node();
      copy.setLeft(left);
      copy.setRight(right);
      return copy;
    }
  }
}