package com.nickferraro.bayesian.core;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.security.InvalidParameterException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.nickferraro.bayesian.IClassification;
import com.nickferraro.bayesian.IDataRow;
import com.nickferraro.bayesian.ISentenceInput;
import com.nickferraro.bayesian.model.IBayesianModel;

public class BayesianSystemTest {
	private BayesianSystem<String> system;
	private IBayesianModel<String> mockModel;
	private ISentenceInput mockSentenceInput;
	
	@SuppressWarnings("unchecked")
	@Before
	public void setup() {
		mockModel = (IBayesianModel<String>)mock(IBayesianModel.class);
		system = new BayesianSystem<String>(mockModel);
		mockSentenceInput = mock(ISentenceInput.class);
	}
	
	@Test
	public void testTrainOnRow() {
		@SuppressWarnings("unchecked")
		IDataRow<String> mockDataRow = mock(IDataRow.class);
		
		system.trainOnRow(mockDataRow);
		
		verify(mockModel).addDataRow(mockDataRow);
	}
	
	@Test
	public void testTrainOnRow_NullRow() {
		system.trainOnRow(null);
		
		verifyZeroInteractions(mockModel);
	}
	
	@Test
	public void testTrainOnRows() {
		@SuppressWarnings("unchecked")
		List<IDataRow<String>> mockList = mock(List.class);
		@SuppressWarnings("unchecked")
		IDataRow<String> mockDataRow = mock(IDataRow.class);
		
		mockList.add(mockDataRow);
		mockList.add(mockDataRow);
		mockList.add(mockDataRow);
		
		system.trainOnRows(mockList);
		
		verify(mockModel).addDataRows(mockList);
	}
	
	@Test
	public void testTrainOnRows_NullRows() {
		@SuppressWarnings("unchecked")
		List<IDataRow<String>> mockList = mock(List.class);
		@SuppressWarnings("unchecked")
		IDataRow<String> mockDataRow = mock(IDataRow.class);
		mockList.add(mockDataRow);
		mockList.add(null);
		mockList.add(mockDataRow);
		
		system.trainOnRows(mockList);
		
		verify(mockModel).addDataRows(mockList);
	}
	
	@Test
	public void testTrainOnRows_NullList() {
		system.trainOnRows(null);
		
		verifyZeroInteractions(mockModel);
	}
	
	@Test
	public void testSetBayesianModel() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		@SuppressWarnings("unchecked")
		IBayesianModel<String> mockModel2 = (IBayesianModel<String>)mock(IBayesianModel.class);
		
		system.setBayesianModel(mockModel2);
		
		Field field = system.getClass().getDeclaredField("bayesianModel");
		field.setAccessible(true);
		assertEquals(field.get(system), mockModel2);
	}
	
	@Test(expected = InvalidParameterException.class)
	public void testSetBayesianModel_Null() {
		system.setBayesianModel(null);
	}
	
	@Test
	public void testIsWordAllowed() {
		assertThat(system.isWordAllowed("anyword"), is(true));
		assertThat(system.isWordAllowed(""), is(true));
		assertThat(system.isWordAllowed(null), is(true));
	}
	
	@Test
	public void testClassifyRow() {
		Set<String> categorySet = createCategorySet();
		Set<String> wordSet = createWordSet();
		when(mockModel.getUniqueCategories()).thenReturn(categorySet);
		when(mockModel.getTotalRows()).thenReturn(6);
		when(mockModel.countRowsWithCategory("a")).thenReturn(1);
		when(mockModel.countRowsWithCategory("b")).thenReturn(2);
		when(mockModel.countRowsWithCategory("c")).thenReturn(3);
		when(mockModel.countRowsWithCategoryWithWord("a", "one")).thenReturn(1);
		when(mockModel.countRowsWithCategoryWithWord("a", "two")).thenReturn(0);
		when(mockModel.countRowsWithCategoryWithWord("a", "tre")).thenReturn(0);
		when(mockModel.countRowsWithCategoryWithWord("b", "one")).thenReturn(2);
		when(mockModel.countRowsWithCategoryWithWord("b", "two")).thenReturn(0);
		when(mockModel.countRowsWithCategoryWithWord("b", "tre")).thenReturn(1);
		when(mockModel.countRowsWithCategoryWithWord("c", "one")).thenReturn(1);
		when(mockModel.countRowsWithCategoryWithWord("c", "two")).thenReturn(1);
		when(mockModel.countRowsWithCategoryWithWord("c", "tre")).thenReturn(3);
		when(mockSentenceInput.getUniqueSentenceWords()).thenReturn(wordSet);
		
		List<IClassification<String>> classifications = system.classifyRow(mockSentenceInput);
		
		assertThat(classifications, is(notNullValue()));
		assertThat(classifications.size(), is(3));
		assertClassificationProbability("a", 1.0 / 19.0, classifications);
		assertClassificationProbability("b", 6.0 / 19.0, classifications);
		assertClassificationProbability("c", 12.0 / 19.0, classifications);
	}
	
	@Test(expected = InvalidParameterException.class)
	public void testClassifyRow_Null() {
		system.classifyRow(null);
	}
	
	@Test
	public void testClassifyRow_NullUniqueCategories() {
		when(mockModel.getUniqueCategories()).thenReturn(null);
		
		List<IClassification<String>> classifications = system.classifyRow(mockSentenceInput);
		
		assertThat(classifications, is(notNullValue()));
		assertThat(classifications.isEmpty(), is(true));
	}
	
	@Test
	public void testClassifyRow_EmptyUniqueCategories() {
		Set<String> emptySet = Collections.emptySet();
		when(mockModel.getUniqueCategories()).thenReturn(emptySet);
		when(mockSentenceInput.getUniqueSentenceWords()).thenReturn(emptySet);
		
		List<IClassification<String>> classifications = system.classifyRow(mockSentenceInput);
		
		assertThat(classifications, is(notNullValue()));
		assertThat(classifications.isEmpty(), is(true));
	}
	
	@Test
	public void testClassifyRow_ZeroTrainedRows() {
		Set<String> categorySet = createCategorySet();
		when(mockModel.getUniqueCategories()).thenReturn(categorySet);
		when(mockModel.getTotalRows()).thenReturn(0);
		
		List<IClassification<String>> classifications = system.classifyRow(mockSentenceInput);
		
		assertThat(classifications, is(notNullValue()));
		assertThat(classifications.size(), is(3));
		assertClassificationProbability("a", 1.0/3.0, classifications);
		assertClassificationProbability("b", 1.0/3.0, classifications);
		assertClassificationProbability("c", 1.0/3.0, classifications);
	}
	
	@Test
	public void testClassifyRow_NullUniqueWords() {
		Set<String> categorySet = createCategorySet();
		when(mockModel.getUniqueCategories()).thenReturn(categorySet);
		when(mockModel.getTotalRows()).thenReturn(6);
		when(mockModel.countRowsWithCategory("a")).thenReturn(1);
		when(mockModel.countRowsWithCategory("b")).thenReturn(2);
		when(mockModel.countRowsWithCategory("c")).thenReturn(3);
		when(mockSentenceInput.getUniqueSentenceWords()).thenReturn(null);
		
		List<IClassification<String>> classifications = system.classifyRow(mockSentenceInput);
		
		assertThat(classifications, is(notNullValue()));
		assertThat(classifications.size(), is(3));
		assertClassificationProbability("a", 1.0 / 6.0, classifications);
		assertClassificationProbability("b", 2.0 / 6.0, classifications);
		assertClassificationProbability("c", 3.0 / 6.0, classifications);
	}
	
	@Test
	public void testClassifyRow_EmptyUniqueWords() {
		Set<String> categorySet = createCategorySet();
		Set<String> wordSet = Collections.emptySet();
		when(mockModel.getUniqueCategories()).thenReturn(categorySet);
		when(mockModel.getTotalRows()).thenReturn(6);
		when(mockModel.countRowsWithCategory("a")).thenReturn(1);
		when(mockModel.countRowsWithCategory("b")).thenReturn(2);
		when(mockModel.countRowsWithCategory("c")).thenReturn(3);
		when(mockSentenceInput.getUniqueSentenceWords()).thenReturn(wordSet);
		
		List<IClassification<String>> classifications = system.classifyRow(mockSentenceInput);
		
		assertThat(classifications, is(notNullValue()));
		assertThat(classifications.size(), is(3));
		assertClassificationProbability("a", 1.0 / 6.0, classifications);
		assertClassificationProbability("b", 2.0 / 6.0, classifications);
		assertClassificationProbability("c", 3.0 / 6.0, classifications);
	}
	
	@Test
	public void testClassifyRow_UntrainedUniqueWords() {
		Set<String> categorySet = createCategorySet();
		Set<String> wordSet = createWordSet();
		when(mockModel.getUniqueCategories()).thenReturn(categorySet);
		when(mockModel.getTotalRows()).thenReturn(6);
		when(mockModel.countRowsWithCategory("a")).thenReturn(1);
		when(mockModel.countRowsWithCategory("b")).thenReturn(2);
		when(mockModel.countRowsWithCategory("c")).thenReturn(3);
		when(mockModel.countRowsWithCategoryWithWord(Mockito.anyString(), Mockito.anyString())).thenReturn(0);
		when(mockSentenceInput.getUniqueSentenceWords()).thenReturn(wordSet);
		
		List<IClassification<String>> classifications = system.classifyRow(mockSentenceInput);
		
		assertThat(classifications, is(notNullValue()));
		assertThat(classifications.size(), is(3));
		assertClassificationProbability("a", 1.0 / 6.0, classifications);
		assertClassificationProbability("b", 2.0 / 6.0, classifications);
		assertClassificationProbability("c", 3.0 / 6.0, classifications);
	}
	
	public static void assertClassificationProbability(String category, double probability, List<IClassification<String>> classifications) {
		for(IClassification<String> classification : classifications ) {
			if( classification.getCategory().equals(category) ) {
				assertThat(classification.getProbability(), is(probability));
				return;
			}
		}
		
		fail("Could not find category in list of classifications");
	}
	
	private Set<String> createCategorySet() {
		HashSet<String> categorySet = new HashSet<String>();
		categorySet.add("a");
		categorySet.add("b");
		categorySet.add("c");
		
		return categorySet;
	}
	
	private Set<String> createWordSet() {
		HashSet<String> wordSet = new HashSet<String>();
		wordSet.add("one");
		wordSet.add("two");
		wordSet.add("tre");
		
		return wordSet;
	}
}
