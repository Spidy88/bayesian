package com.nickferraro.bayesian.model.hashed.core;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.security.InvalidParameterException;

import org.junit.Before;
import org.junit.Test;

public class WordNodeTest {
	private static final int testCategory = 5;
	private static final String testWord = "testWord";
	private WordNode<Integer> testNode;
	
	@Before
	public void setup() {
		testNode = new WordNode<Integer>(testWord);
	}
	
	@Test
	public void testCategoryNodeInitialization() {
		assertThat(testNode.getCount(), is(0));
		assertThat(testNode.getValue(), is(testWord));
		assertThat(testNode.getLinksCount(), is(0));
	}
	
	@Test
	public void testCategoryNodeConstructor() {
		WordNode<Integer> categoryNode = new WordNode<Integer>(testWord);
		assertThat(categoryNode.getValue(), is(testWord));
	}
	
	@Test
	public void testGetSetCount() {
		int expectedValue = 5;
		testNode.setCount(expectedValue);
		assertThat(testNode.getCount(), is(expectedValue));
	}

	@Test
	public void testGetSetValue() {
		String expectedValue = "fakeValue";
		testNode.setValue(expectedValue);
		assertThat(testNode.getValue(), is(expectedValue));
	}
	
	@Test(expected = InvalidParameterException.class)
	public void testSetNullValue() {
		testNode.setValue(null);
	}

	@Test
	public void testGetLink() {
		int expectedWeight = 5;
		
		@SuppressWarnings("unchecked")
		Link<Integer> mockLink = mock(Link.class);
		@SuppressWarnings("unchecked")
		CategoryNode<Integer> mockCategoryNode = mock(CategoryNode.class);
		
		when(mockCategoryNode.getValue()).thenReturn(testCategory);
		when(mockLink.getWordNode()).thenReturn(testNode);
		when(mockLink.getCategoryNode()).thenReturn(mockCategoryNode);
		when(mockLink.getWeight()).thenReturn(expectedWeight);
		
		testNode.addLink(mockLink);
		assertThat(testNode.getLink(testCategory), is(mockLink));
		assertThat(testNode.getLinkStrength(testCategory), is(expectedWeight));
	}
	
	@Test
	public void testGetLink_NoLink() {
		assertThat(testNode.getLink(0), is(nullValue()));
	}
	
	@Test
	public void testGetLinkStrength_NoLink() {
		assertThat(testNode.getLinkStrength(0), is(0));
	}
	
	@Test
	public void testAddLink() {
		@SuppressWarnings("unchecked")
		Link<Integer> mockLink = mock(Link.class);
		@SuppressWarnings("unchecked")
		CategoryNode<Integer> mockCategoryNode = mock(CategoryNode.class);
		
		when(mockCategoryNode.getValue()).thenReturn(testCategory);
		when(mockLink.getCategoryNode()).thenReturn(mockCategoryNode);
		when(mockLink.getWordNode()).thenReturn(testNode);
		
		testNode.addLink(mockLink);
		assertThat(testNode.getLink(testCategory), is(mockLink));
	}
	
	@Test(expected = InvalidParameterException.class)
	public void testAddLink_NullLink() {
		testNode.addLink(null);
	}
	
	@Test(expected = InvalidParameterException.class)
	public void testAddLink_NullWordNode() {
		@SuppressWarnings("unchecked")
		Link<Integer> mockLink = mock(Link.class);
		@SuppressWarnings("unchecked")
		CategoryNode<Integer> mockCategoryNode = mock(CategoryNode.class);
		
		when(mockCategoryNode.getValue()).thenReturn(0);
		when(mockLink.getWordNode()).thenReturn(null);
		when(mockLink.getCategoryNode()).thenReturn(mockCategoryNode);
		
		testNode.addLink(mockLink);
	}
	
	@Test(expected = InvalidParameterException.class)
	public void testAddLink_NullCategoryNode() {
		@SuppressWarnings("unchecked")
		Link<Integer> mockLink = mock(Link.class);
		
		when(mockLink.getWordNode()).thenReturn(testNode);
		when(mockLink.getCategoryNode()).thenReturn(null);
		
		testNode.addLink(mockLink);
	}
	
	@Test(expected = InvalidParameterException.class)
	public void testAddLink_InvalidWordNode() {
		@SuppressWarnings("unchecked")
		Link<Integer> mockLink = mock(Link.class);
		@SuppressWarnings("unchecked")
		WordNode<Integer> mockWordNode = mock(WordNode.class);
		@SuppressWarnings("unchecked")
		CategoryNode<Integer> mockCategoryNode = mock(CategoryNode.class);
		
		when(mockWordNode.getValue()).thenReturn(testWord);
		when(mockCategoryNode.getValue()).thenReturn(0);
		when(mockLink.getWordNode()).thenReturn(mockWordNode);
		when(mockLink.getCategoryNode()).thenReturn(mockCategoryNode);
		
		testNode.addLink(mockLink);
	}
}
