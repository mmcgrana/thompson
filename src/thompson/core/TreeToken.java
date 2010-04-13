package thompson.core;

public class TreeToken {
  public Node node;
  public ForestLabel bigLabel;
  public boolean nextLittleLabelIsN;
  public TreeToken nextToken;
  public int excess;
  
  public TreeToken() {
    this(ForestLabel.N, 0);
  }
  
  public TreeToken(ForestLabel bigLabel, int excess) {
    this.node = new Node();
    this.bigLabel = bigLabel;
    this.nextLittleLabelIsN = false;
    this.nextToken = null;
    this.excess = excess;
  }
}
