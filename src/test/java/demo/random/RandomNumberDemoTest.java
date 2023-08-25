package demo.random;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

public class RandomNumberDemoTest {

  @Random
  private int randomNumber1;

  @Test
  void test() {
    assertNotEquals(0, randomNumber1);
  }
}
