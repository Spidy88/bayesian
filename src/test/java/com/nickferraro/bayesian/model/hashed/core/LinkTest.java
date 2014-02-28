package com.nickferraro.bayesian.model.hashed.core;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.security.InvalidParameterException;

import org.junit.Before;
import org.junit.Test;

import com.nickferraro.bayesian.model.hashed.core.Link;
import com.nickferraro.bayesian.model.hashed.core.WordNode;

public class LinkTest {
	private Link<Integer> testLink;
	private CategoryNode<Integer> mockCategoryNode;
	private WordNode<Integer> mockWordNode;
	
	@SuppressWarnings("unchecked")
	@Before
	public void setup() {
		this.mockCategoryNode = mock(CategoryNode.class);
		this.mockWordNode = mock(WordNode.class);
		
		testLink = new Link<Integer>(mockCategoryNode, mockWordNode);
	}
	
	@Test
	public void testLinkConstructor() {
		assertThat(testLink.getWeight(), is(0));
	}
	
	@Test(expected = InvalidParameterException.class)
	public void testLinkConstructor_NullCategoryNode() {
		new Link<Integer>(null, mockWordNode);
	}
	
	@Test(expected = InvalidParameterException.class)
	public void testLinkConstructor_NullWordNode() {
		new Link<Integer>(mockCategoryNode, null);
	}
	
	@Test
	public void testGetCategoryNode() {
		assertThat(testLink.getCategoryNode(), is(mockCategoryNode));
	}
	
	@Test
	public void testGetWordNode() {
		assertThat(testLink.getWordNode(), is(mockWordNode));
	}
	
	@Test
	public void testWeight() {
		int expectedWeight = 5;
		testLink.setWeight(expectedWeight);
		assertThat(testLink.getWeight(), is(expectedWeight));
	}
	
	@Test(expected = InvalidParameterException.class)
	public void testInvalidWeight() {
		testLink.setWeight(-1);
	}
}
