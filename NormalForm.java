import java.util.*;

class NormalForm {
  ArrayList<Integer> bases, exponents;
  
  NormalForm() {
    this.bases = new ArrayList<Integer>();
    this.exponents = new ArrayList<Integer>();
  } 
  
  void addTerm(int base, int exponent) {
    this.bases.add(base);
    this.exponents.add(exponent);
  }
  
  public String toString() {
    StringBuilder buf = new StringBuilder();
    for (int i = 0; i < this.bases.size(); i++) {
      int base = this.bases.get(i);
      int exponent = this.exponents.get(i);
      buf.append("(x_" + base + "^" + exponent + ")");
    }
    return buf.toString();
  }
}
