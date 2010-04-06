package thompson.core;

import java.util.regex.*;

// A simple parser helper for use with reading GenExp string representations.
public class Parser {
  private String input;
  private int pointer;
  
  public Parser(String input) {
    this.pointer = 0;
    this.input = input;
  }
  
  public boolean isEnd() {
    return this.pointer == input.length();
  }

  public String rest() {
    return this.input.substring(this.pointer);
  }

  public String next(Pattern pattern) {
    Matcher matcher = pattern.matcher(this.rest());
    if (matcher.lookingAt()) {
      this.pointer += matcher.end();
      return matcher.group();
    } else {
      return null;
    }
  }
}
