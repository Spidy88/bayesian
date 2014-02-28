package com.nickferraro.bayesian.core;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.security.InvalidParameterException;

import org.junit.Test;

public class ClassificationTest {
	private final Integer testCategory = 5;
	private final double testProbability = 0.456;
	
	@Test
	public void testClassificationConstructor() {
		Classification<Integer> c = new Classification<Integer>(testCategory, testProbability);
		assertThat(c.getCategory(), is(testCategory));
		assertThat(c.getProbability(), is(testProbability));
	}
	
	@Test
	public void testClassificationConstructor_ProbabilityInLowerBound() {
		double lowerBound = 0.0;
		Classification<Integer> c = new Classification<Integer>(testCategory, lowerBound);
		assertThat(c.getCategory(), is(testCategory));
		assertThat(c.getProbability(), is(lowerBound));
	}
	
	@Test
	public void testClassificationConstructor_ProbabilityInUpperBound() {
		double upperBound = 1.0;
		Classification<Integer> c = new Classification<Integer>(testCategory, upperBound);
		assertThat(c.getCategory(), is(testCategory));
		assertThat(c.getProbability(), is(upperBound));
	}
	
	@Test(expected = InvalidParameterException.class)
	public void testClassificationConstructor_NullCategory() {
		new Classification<Integer>(null, testProbability);
	}
	
	@Test(expected = InvalidParameterException.class)
	public void testClassificationConstructor_ProbabilityOutLowerBound() {
		new Classification<Integer>(testCategory, -0.00001);
	}
	
	@Test(expected = InvalidParameterException.class)
	public void testClassificationConstructor_ProbabilityOutUpperBound() {
		new Classification<Integer>(testCategory, 1.00001);
	}
}
