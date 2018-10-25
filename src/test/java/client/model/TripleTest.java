package client.model;

import org.junit.Assert;
import org.junit.Test;

/**
 * This class tests all the methods of {@link Triple} class
 * @author Andrea Cimmino
 * @version 0.6.3
 *
 */
public class TripleTest {

	/*
	 	Triple methods:
	 		- test*: constructor			->covered
	 		- test1: getters				->covered
	 		- test3: setters				->covered
	 		- test4: getters & setters   ->covered
	 		- test5: toString   			->covered
	 */
	
	@Test
	public void test1() {
		Triple<String,String,Integer> triple = new Triple<String,String,Integer>("A","B",3);
		Boolean testCondition = triple.getFirstElement().equals("A");
		testCondition &= triple.getSecondElement().equals("B");
		testCondition &= triple.getThirdElement().equals(3);
		Assert.assertTrue(testCondition);
	}
	
	@SuppressWarnings("unlikely-arg-type")
	@Test
	public void test2() {
		Triple<String,String,Integer> triple = new Triple<String,String,Integer>("A","B",3);
		Boolean testCondition = triple.getFirstElement().equals("A");
		testCondition &= triple.getSecondElement().equals("B");
		testCondition &= triple.getThirdElement().equals("3");
		Assert.assertTrue(!testCondition);
	}
	
	@Test
	public void test3() {
		Triple<String,String,Integer> triple = new Triple<String,String,Integer>("A","B",3);
		triple.setFirstElement("B");
		triple.setSecondElement("A");
		triple.setThirdElement(10);
		Boolean testCondition = triple.getFirstElement().equals("B");
		testCondition &= triple.getSecondElement().equals("A");
		testCondition &= triple.getThirdElement().equals(10);
		Assert.assertTrue(testCondition);
	}
	
	@Test
	public void test4() {
		Triple<String,String,Integer> triple = new Triple<String,String,Integer>("A","B",3);
		Boolean testCondition = triple.getFirstElement().equals("A");
		testCondition &= triple.getSecondElement().equals("B");
		testCondition &= triple.getThirdElement().equals(3);
		triple.setFirstElement("B");
		triple.setSecondElement("A");
		triple.setThirdElement(10);
		testCondition &= triple.getFirstElement().equals("B");
		testCondition &= triple.getSecondElement().equals("A");
		testCondition &= triple.getThirdElement().equals(10);
		Assert.assertTrue(testCondition);
	}
	
	@Test
	public void test5() {
		Triple<String,String,Integer> triple = new Triple<String,String,Integer>("A","B",10);
		String actualString = triple.toString();
		String expectedString = "[A, B, 10]";
		Assert.assertEquals(expectedString, actualString);
	}
}
