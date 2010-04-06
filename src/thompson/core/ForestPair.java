package thompson.core;

public class ForestPair {
  ForestLabel[] upperLabels, lowerLabels;
  int upperNumLeft, lowerNumLeft;
  
  public ForestPair(ForestLabel[] upperLabels, ForestLabel[] lowerLabels, int upperNumLeft, int lowerNumLeft) {
    this.upperLabels = upperLabels;
    this.lowerLabels = lowerLabels;
    this.upperNumLeft = upperNumLeft;
    this.lowerNumLeft = lowerNumLeft;
  }
  
  public int numPairs() {
    return this.upperLabels.length;
  }

  public String toString() {
    StringBuffer topBuffer = new StringBuffer();
    StringBuffer upperBuffer = new StringBuffer();
    StringBuffer lowerBuffer = new StringBuffer();
    StringBuffer bottomBuffer = new StringBuffer();
    for (int i = 0; i <= this.numPairs(); i++) {
      topBuffer.append((i == this.upperNumLeft) ? "v " : "  ");
      upperBuffer.append(((i == 0) || (i == this.numPairs())) ? " " : ",");
      lowerBuffer.append(((i == 0) || (i == this.numPairs())) ? " " : ",");
      upperBuffer.append((i == this.numPairs()) ? " " : upperLabels[i]);
      lowerBuffer.append((i == this.numPairs()) ? " " : lowerLabels[i]);
      bottomBuffer.append((i == this.lowerNumLeft) ? "^ " : "  ");
    }
    return topBuffer.toString() + "\n" + upperBuffer.toString() + "\n" +
           lowerBuffer.toString() + "\n" + bottomBuffer.toString();
  }
  
  private static Node reifyForest(ForestLabel[] labels, int numLeft) {
    int numLabels = labels.length;
    int numInnerLeafs = numLabels + 1;
    Node[] innerLeafs = new Node[numInnerLeafs];
    for (int i = 0; i < numInnerLeafs; i++) {
      innerLeafs[i] = new Node();
    }
    for (int i = 0; i < numLabels; i++) {
      if (labels[i] == ForestLabel.I) {
        Node parent = new Node();
        parent.setLeft(innerLeafs[i].root());
        parent.setRight(innerLeafs[i+1].root());
      }
    }
    for (int i = numLabels-1; i >= 0; i--) {
      if (labels[i] == ForestLabel.N) {
        Node parent = new Node();
        parent.setLeft(innerLeafs[i].root());
        parent.setRight(innerLeafs[i+1].root());
      }
    }
    Node leftTree = new Node();
    for (int i = 0; i < numLeft; i++) {
      Node root = innerLeafs[i].root();
      if (root != leftTree) {
        Node parent = new Node();
        parent.setLeft(leftTree);
        parent.setRight(root);
        leftTree = parent;
      }
    }
    Node rightTree = new Node();
    for (int i = numInnerLeafs-1; i >= numLeft; i--) {
      Node root = innerLeafs[i].root();
      if (root != rightTree) {
        Node parent = new Node();
        parent.setRight(rightTree);
        parent.setLeft(root);
        rightTree = parent;
      }
    }
    Node tree = new Node();
    tree.setLeft(leftTree);
    tree.setRight(rightTree);
    return tree;
  }
  
  public TreePair toTreePair() {
    return new TreePair(reifyForest(this.lowerLabels, this.lowerNumLeft),
                        reifyForest(this.upperLabels, this.upperNumLeft));
  }
}
