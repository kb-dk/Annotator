package dk.kb.annotator.test;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * @author: Andreas B. Westh
 * Date: 11/2/11
 * Time: 15:40 PM
 */
public class SimpleTest {

    @Test
	public void testMultiply() {
		SimpleTest tester = new SimpleTest();
		assertEquals("Result", 50, tester.multiply(10, 5));
	}

   public int multiply(int x, int y) {
		return x / y;
	}
}
