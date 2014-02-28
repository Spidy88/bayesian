package com.nickferraro.bayesian.core;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;
import static org.hamcrest.CoreMatchers.*;

import com.nickferraro.bayesian.IClassification;

public class ClassificationComparatorTest {
	private ClassificationComparator comparator;
	private IClassification<String> mockClassification1;
	private IClassification<String> mockClassification2;
	
	@SuppressWarnings("unchecked")
	@Before
	public void setup() {
		comparator = new ClassificationComparator();
		mockClassification1 = mock(IClassification.class);
		mockClassification2 = mock(IClassification.class);
	}
	
	@Test
	public void testCompare() {
		when(mockClassification1.getProbability()).thenReturn(10.0d);
		when(mockClassification2.getProbability()).thenReturn(50.0d);
		
		assertThat(comparator.compare(mockClassification1, mockClassification2), is(-1));
		assertThat(comparator.compare(mockClassification2, mockClassification1), is(1));
	}
	
	@Test
	public void testCompare_SameObject() {
		assertThat(comparator.compare(mockClassification1, mockClassification1), is(0));
	}
	
	@Test
	public void testCompare_Equal() {
		when(mockClassification1.getProbability()).thenReturn(50.0d);
		when(mockClassification2.getProbability()).thenReturn(50.0d);
		
		assertThat(comparator.compare(mockClassification1, mockClassification2), is(0));
		assertThat(comparator.compare(mockClassification2, mockClassification1), is(0));
	}
	
	@Test
	public void testCompare_BothNull() {
		assertThat(comparator.compare(null, null), is(0));
	}
	
	@Test
	public void testCompare_OneNull() {
		when(mockClassification1.getProbability()).thenReturn(10.0d);
		
		assertThat(comparator.compare(mockClassification1, null), is(1));
		assertThat(comparator.compare(null, mockClassification1), is(-1));
	}
}
