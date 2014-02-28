package com.nickferraro.bayesian.model.hashed.core;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.security.InvalidParameterException;

import org.junit.Before;
import org.junit.Test;

public class CategoryNodeTest {
	private static final String testWord = "test";
	private CategoryNode<Integer> testNode;
	
	@Before
	public void setup() {
		testNode = new CategoryNode<Integer>(0);
	}
	
	@Test
	public void testCategoryNodeInitialization() {
		assertThat(testNode.getCount(), is(0));
		assertThat(testNode.getValue(), is(0));
		assertThat(testNode.getLinksCount(), is(0));
	}
	
	@Test
	public void testCategoryNodeConstructor() {
		int expectedValue = 5;
		CategoryNode<Integer> categoryNode = new CategoryNode<Integer>(expectedValue);
		assertThat(categoryNode.getValue(), is(expectedValue));
	}
	
	@Test
	public void testGetSetCount() {
		int expectedValue = 5;
		testNode.setCount(expectedValue);
		assertThat(testNode.getCount(), is(expectedValue));
	}

	@Test
	public void testGetSetValue() {
		int expectedValue = 5;
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
		WordNode<Integer> mockWordNode = mock(WordNode.class);
		
		when(mockWordNode.getValue()).thenReturn(testWord);
		when(mockLink.getCategoryNode()).thenReturn(testNode);
		when(mockLink.getWordNode()).thenReturn(mockWordNode);
		when(mockLink.getWeight()).thenReturn(expectedWeight);
		
		testNode.addLink(mockLink);
		assertThat(testNode.getLink(testWord), is(mockLink));
		assertThat(testNode.getLinkStrength(testWord), is(expectedWeight));
	}
	
	@Test
	public void testGetLink_NoLink() {
		assertThat(testNode.getLink("no-link"), is(nullValue()));
	}
	
	@Test
	public void testGetLinkStrength_NoLink() {
		assertThat(testNode.getLinkStrength("no-link"), is(0));
	}
	
	@Test
	public void testAddLink() {
		@SuppressWarnings("unchecked")
		Link<Integer> mockLink = mock(Link.class);
		@SuppressWarnings("unchecked")
		WordNode<Integer> mockWordNode = mock(WordNode.class);
		
		when(mockWordNode.getValue()).thenReturn(testWord);
		when(mockLink.getCategoryNode()).thenReturn(testNode);
		when(mockLink.getWordNode()).thenReturn(mockWordNode);
		
		testNode.addLink(mockLink);
		assertThat(testNode.getLink(testWord), is(mockLink));
	}
	
	@Test(expected = InvalidParameterException.class)
	public void testAddLink_NullLink() {
		testNode.addLink(null);
	}
	
	@Test(expected = InvalidParameterException.class)
	public void testAddLink_NullWordNode() {
		@SuppressWarnings("unchecked")
		Link<Integer> mockLink = mock(Link.class);
		
		when(mockLink.getWordNode()).thenReturn(null);
		when(mockLink.getCategoryNode()).thenReturn(testNode);
		
		testNode.addLink(mockLink);
	}
	
	@Test(expected = InvalidParameterException.class)
	public void testAddLink_NullCategoryNode() {
		@SuppressWarnings("unchecked")
		Link<Integer> mockLink = mock(Link.class);
		@SuppressWarnings("unchecked")
		WordNode<Integer> mockWordNode = mock(WordNode.class);
		
		when(mockWordNode.getValue()).thenReturn(testWord);
		when(mockLink.getWordNode()).thenReturn(mockWordNode);
		when(mockLink.getCategoryNode()).thenReturn(null);
		
		testNode.addLink(mockLink);
	}
	
	@Test(expected = InvalidParameterException.class)
	public void testAddLink_InvalidCategoryNode() {
		@SuppressWarnings("unchecked")
		Link<Integer> mockLink = mock(Link.class);
		@SuppressWarnings("unchecked")
		WordNode<Integer> mockWordNode = mock(WordNode.class);
		@SuppressWarnings("unchecked")
		CategoryNode<Integer> mockCategoryNode = mock(CategoryNode.class);
		
		when(mockLink.getWordNode()).thenReturn(mockWordNode);
		when(mockLink.getCategoryNode()).thenReturn(mockCategoryNode);
		
		testNode.addLink(mockLink);
	}
}
