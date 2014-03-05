package com.nickferraro.bayesian.model.hashed;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeThat;
import static org.junit.Assume.assumeTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.util.collections.Sets;

import com.nickferraro.bayesian.IDataRow;

public class BayesianModelTest {
	private enum TestCategory {
		CATEGORY1,
		CATEGORY2,
		CATEGORY3
	}
	
	private static final TestCategory testCategory = TestCategory.CATEGORY1;
	private static final String testWord = "test";
	
	private BayesianModel<TestCategory> bayesianModel = null;
	
	@Before
	public void setup() {
		bayesianModel = new BayesianModel<TestCategory>();
	}
	
	@Test
	public void testBayesianInitialization() {
		assertBayesianModelUnchanged(bayesianModel);
	}
	
	@Test
	public void testAddingCategory() {	
		assertTrue(bayesianModel.addCategory(testCategory));
		assertThat(bayesianModel.getTotalRows(), is(0));
		assertBayesianModelCategoriesUpdated(bayesianModel);
	}
	
	@Test
	public void testAddingNullCategory() {
		assertFalse(bayesianModel.addCategory(null));
		assertBayesianModelUnchanged(bayesianModel);
	}
	
	@Test
	public void testAddingExistingCategory() {
		assumeTrue(bayesianModel.addCategory(testCategory));
		assertFalse(bayesianModel.addCategory(testCategory));
		assertBayesianModelCategoriesUpdated(bayesianModel);
	}
	
	@Test
	public void testAddingBatchCategories() {
		List<TestCategory> categories = Arrays.asList(TestCategory.CATEGORY1, TestCategory.CATEGORY2, TestCategory.CATEGORY3);
		assertThat(categories.size(), is(3));
		assertThat(bayesianModel.addCategories(categories), is(3));
		assertThat(bayesianModel.getUniqueCategories().size(), is(3));
	}
	
	@Test
	public void testAddingBatchCategories_NullCategories() {
		assertThat(bayesianModel.addCategories(null), is(0));
	}
	
	@Test
	public void testAddingBatchCategories_EmptyCategories() {
		List<TestCategory> categories = Collections.emptyList();
		assertThat(bayesianModel.addCategories(categories), is(0));
	}
	
	@Test
	public void testAddingBatchCategories_WithDuplicate() {
		List<TestCategory> categories = Arrays.asList(TestCategory.CATEGORY1, TestCategory.CATEGORY2, TestCategory.CATEGORY3, TestCategory.CATEGORY1);
		assertThat(categories.size(), is(4));
		assertThat(bayesianModel.addCategories(categories), is(3));
		assertThat(bayesianModel.getUniqueCategories().size(), is(3));
	}
	
	@Test
	public void testAddingBatchCategories_AllDuplicates() {
		List<TestCategory> categories = Arrays.asList(TestCategory.CATEGORY1);
		assumeThat(bayesianModel.addCategory(TestCategory.CATEGORY1), is(true));
		assertThat(bayesianModel.addCategories(categories), is(0));
	}
	
	@Test
	public void testAddingBatchCategories_WithNull() {
		List<TestCategory> categories = Arrays.asList(TestCategory.CATEGORY1, TestCategory.CATEGORY2, TestCategory.CATEGORY3, null);
		assertThat(categories.size(), is(4));
		assertThat(bayesianModel.addCategories(categories), is(3));
		assertThat(bayesianModel.getUniqueCategories().size(), is(3));
	}
	
	@Test
	public void testAddingWord() {	
		assertTrue(bayesianModel.addWord(testWord));
		
		assertThat(bayesianModel.getTotalRows(), is(0));
		assertBayesianModelWordsUpdated(bayesianModel);
	}
	
	@Test
	public void testAddingNullWord() {
		assertFalse(bayesianModel.addWord(null));
		assertBayesianModelUnchanged(bayesianModel);
	}
	
	@Test
	public void testAddingExistingWord() {
		assumeTrue(bayesianModel.addWord("test"));
		assertFalse(bayesianModel.addWord("test"));
		assertBayesianModelWordsUpdated(bayesianModel);
	}
	
	@Test
	public void testAddingBatchWords() {
		List<String> words = Arrays.asList("a", "b", "c");
		assertThat(words.size(), is(3));
		assertThat(bayesianModel.addWords(words), is(3));
		assertThat(bayesianModel.getUniqueWords().size(), is(3));
	}
	
	@Test
	public void testAddingBatchWords_NullWords() {
		assertThat(bayesianModel.addWords(null), is(0));
	}
	
	@Test
	public void testAddingBatchWords_EmptyWords() {
		List<String> emptyWords = Collections.emptyList();
		assertThat(bayesianModel.addWords(emptyWords), is(0));
	}
	
	@Test
	public void testAddingBatchWords_WithDuplicate() {
		List<String> words = Arrays.asList("a", "b", "c", "a");
		assertThat(words.size(), is(4));
		assertThat(bayesianModel.addWords(words), is(3));
		assertThat(bayesianModel.getUniqueWords().size(), is(3));
	}
	
	@Test
	public void testAddingBatchWords_AllDuplicates() {
		List<String> words = Arrays.asList("a");
		assumeThat(bayesianModel.addWord("a"), is(true));
		assertThat(bayesianModel.addWords(words), is(0));
	}
	
	@Test
	public void testAddingBatchWords_WithNull() {
		List<String> words = Arrays.asList("a", "b", "c", null);
		assertThat(words.size(), is(4));
		assertThat(bayesianModel.addWords(words), is(3));
		assertThat(bayesianModel.getUniqueWords().size(), is(3));
	}
	
	@Test
	public void testAddingNullDataRow() {
		assertFalse(bayesianModel.addDataRow(null));
		assertBayesianModelUnchanged(bayesianModel);
	}
	
	@Test
	public void testAddingDataRow() {
		IDataRow<TestCategory> mockRow = createMockRow();
		assertTrue(bayesianModel.addDataRow(mockRow));
		assertBayesianModelUpdated(bayesianModel);
	}
	
	@Test
	public void testAddingDataRow_NullCategory() {
		IDataRow<TestCategory> mockRow = createMockRow();
		when(mockRow.getCategory()).thenReturn(null);
		assertFalse(bayesianModel.addDataRow(mockRow));
		assertBayesianModelUnchanged(bayesianModel);
	}
	
	@Test
	public void testAddingDataRow_NullSentence() {
		IDataRow<TestCategory> mockRow = createMockRow();
		when(mockRow.getSentence()).thenReturn(null);
		assertTrue(bayesianModel.addDataRow(mockRow));
		assertBayesianModelUpdated(bayesianModel);
	}
	
	@Test
	public void testAddingDataRow_NullWords() {
		IDataRow<TestCategory> mockRow = createMockRow();
		when(mockRow.getSentenceWords()).thenReturn(null);
		assertTrue(bayesianModel.addDataRow(mockRow));
		assertBayesianModelUpdated(bayesianModel);
	}
	
	@Test
	public void testAddingDataRow_NullUniqueWords() {
		IDataRow<TestCategory> mockRow = createMockRow();
		when(mockRow.getUniqueSentenceWords()).thenReturn(null);
		assertFalse(bayesianModel.addDataRow(mockRow));
		assertBayesianModelUnchanged(bayesianModel);
	}
	
	@Test
	public void testAddingBatchDataRows() {
		@SuppressWarnings("unchecked")
		List<IDataRow<TestCategory>> dataRows = Arrays.asList(
				createMockRow(TestCategory.CATEGORY1, "a", "b", "c"),
				createMockRow(TestCategory.CATEGORY2, "a", "d"),
				createMockRow(TestCategory.CATEGORY1, "b", "c", "e"));
		assertThat(bayesianModel.addDataRows(dataRows), is(3));
		assertThat(bayesianModel.getTotalRows(), is(3));
		assertThat(bayesianModel.getUniqueCategories().size(), is(2));
		assertThat(bayesianModel.getUniqueWords().size(), is(5));
	}
	
	@Test
	public void testAddingBatchDataRows_NullRows() {
		assertThat(bayesianModel.addDataRows(null), is(0));
	}
	
	@Test
	public void testAddingBatchDataRows_EmptyRows() {
		List<IDataRow<TestCategory>> dataRows = Collections.emptyList();
		assertThat(bayesianModel.addDataRows(dataRows), is(0));
	}
	
	@Test
	public void testAddingBatchDataRows_WithNullRow() {
		@SuppressWarnings("unchecked")
		List<IDataRow<TestCategory>> dataRows = Arrays.asList(
				createMockRow(TestCategory.CATEGORY1, "a", "b", "c"),
				createMockRow(TestCategory.CATEGORY2, "a", "d"),
				createMockRow(TestCategory.CATEGORY1, "b", "c", "e"),
				null);
		assertThat(dataRows.size(), is(4));
		assertThat(bayesianModel.addDataRows(dataRows), is(3));
		assertThat(bayesianModel.getTotalRows(), is(3));
		assertThat(bayesianModel.getUniqueCategories().size(), is(2));
		assertThat(bayesianModel.getUniqueWords().size(), is(5));
	}
	
	@Test
	public void testAddingBatchDataRows_WithNullCategoryRow() {
		IDataRow<TestCategory> mockRow = createMockRow(TestCategory.CATEGORY3, "r", "g", "b");
		when(mockRow.getCategory()).thenReturn(null);
		
		@SuppressWarnings("unchecked")
		List<IDataRow<TestCategory>> dataRows = Arrays.asList(
				createMockRow(TestCategory.CATEGORY1, "a", "b", "c"),
				createMockRow(TestCategory.CATEGORY2, "a", "d"),
				createMockRow(TestCategory.CATEGORY1, "b", "c", "e"),
				mockRow);
		assertThat(dataRows.size(), is(4));
		assertThat(bayesianModel.addDataRows(dataRows), is(3));
		assertThat(bayesianModel.getTotalRows(), is(3));
		assertThat(bayesianModel.getUniqueCategories().size(), is(2));
		assertThat(bayesianModel.getUniqueWords().size(), is(5));
	}
	
	@Test
	public void testAddingBatchDataRows_WithNullUniqueWordsRow() {
		IDataRow<TestCategory> mockRow = createMockRow(TestCategory.CATEGORY3, "r", "g", "b");
		when(mockRow.getUniqueSentenceWords()).thenReturn(null);
		
		@SuppressWarnings("unchecked")
		List<IDataRow<TestCategory>> dataRows = Arrays.asList(
				createMockRow(TestCategory.CATEGORY1, "a", "b", "c"),
				createMockRow(TestCategory.CATEGORY2, "a", "d"),
				createMockRow(TestCategory.CATEGORY1, "b", "c", "e"),
				mockRow);
		assertThat(dataRows.size(), is(4));
		assertThat(bayesianModel.addDataRows(dataRows), is(3));
		assertThat(bayesianModel.getTotalRows(), is(3));
		assertThat(bayesianModel.getUniqueCategories().size(), is(2));
		assertThat(bayesianModel.getUniqueWords().size(), is(5));
	}
	
	@Test
	public void testAddingBatchDataRows_WithEmptyUniqueWordsRow() {
		Set<String> emptyWords = Collections.emptySet();
		IDataRow<TestCategory> mockRow = createMockRow(TestCategory.CATEGORY3, "r", "g", "b");
		when(mockRow.getUniqueSentenceWords()).thenReturn(emptyWords);
		@SuppressWarnings("unchecked")
		List<IDataRow<TestCategory>> dataRows = Arrays.asList(mockRow);
		assertThat(bayesianModel.addDataRows(dataRows), is(1));
		assertThat(bayesianModel.getUniqueCategories().size(), is(1));
		assertThat(bayesianModel.getUniqueWords().size(), is(0));
	}
	
	@Test
	public void testAddingBatchDataRows_WithAllBadRows() {
		@SuppressWarnings("unchecked")
		IDataRow<TestCategory> mockDataRow = (IDataRow<TestCategory>)mock(IDataRow.class);
		@SuppressWarnings("unchecked")
		List<IDataRow<TestCategory>> dataRows = Arrays.asList(mockDataRow);
		assertThat(bayesianModel.addDataRows(dataRows), is(0));
	}
	
	@Test
	public void testCountRowsWithCategory() {
		@SuppressWarnings("unchecked")
		List<IDataRow<TestCategory>> dataRows = Arrays.asList(
				createMockRow(TestCategory.CATEGORY1, "a", "b", "c"),
				createMockRow(TestCategory.CATEGORY2, "a", "d"),
				createMockRow(TestCategory.CATEGORY1, "b", "c", "e"));
		assumeThat(bayesianModel.addDataRows(dataRows), is(3));
		assertThat(bayesianModel.countRowsWithCategory(TestCategory.CATEGORY1), is(2));
	}
	
	@Test
	public void testCountRowsWithCategory_NullCategory() {
		@SuppressWarnings("unchecked")
		List<IDataRow<TestCategory>> dataRows = Arrays.asList(
				createMockRow(TestCategory.CATEGORY1, "a", "b", "c"),
				createMockRow(TestCategory.CATEGORY2, "a", "d"),
				createMockRow(TestCategory.CATEGORY1, "b", "c", "e"));
		assumeThat(bayesianModel.addDataRows(dataRows), is(3));
		assertThat(bayesianModel.countRowsWithCategory(null), is(0));
	}
	
	@Test
	public void testCountRowsWithCategory_UnusedCategory() {
		@SuppressWarnings("unchecked")
		List<IDataRow<TestCategory>> dataRows = Arrays.asList(
				createMockRow(TestCategory.CATEGORY1, "a", "b", "c"),
				createMockRow(TestCategory.CATEGORY2, "a", "d"),
				createMockRow(TestCategory.CATEGORY1, "b", "c", "e"));
		assumeThat(bayesianModel.addDataRows(dataRows), is(3));
		assertThat(bayesianModel.countRowsWithCategory(TestCategory.CATEGORY3), is(0));
	}
	
	@Test
	public void testCountRowsWithWord() {
		@SuppressWarnings("unchecked")
		List<IDataRow<TestCategory>> dataRows = Arrays.asList(
				createMockRow(TestCategory.CATEGORY1, "a", "b", "c"),
				createMockRow(TestCategory.CATEGORY2, "a", "d"),
				createMockRow(TestCategory.CATEGORY1, "b", "c", "e"));
		assumeThat(bayesianModel.addDataRows(dataRows), is(3));
		assertThat(bayesianModel.countRowsWithWord("a"), is(2));
	}
	
	@Test
	public void testCountRowsWithWord_NullWord() {
		@SuppressWarnings("unchecked")
		List<IDataRow<TestCategory>> dataRows = Arrays.asList(
				createMockRow(TestCategory.CATEGORY1, "a", "b", "c"),
				createMockRow(TestCategory.CATEGORY2, "a", "d"),
				createMockRow(TestCategory.CATEGORY1, "b", "c", "e"));
		assumeThat(bayesianModel.addDataRows(dataRows), is(3));
		assertThat(bayesianModel.countRowsWithWord(null), is(0));
	}
	
	@Test
	public void testCountRowsWithWord_UnusedWord() {
		@SuppressWarnings("unchecked")
		List<IDataRow<TestCategory>> dataRows = Arrays.asList(
				createMockRow(TestCategory.CATEGORY1, "a", "b", "c"),
				createMockRow(TestCategory.CATEGORY2, "a", "d"),
				createMockRow(TestCategory.CATEGORY1, "b", "c", "e"));
		assumeThat(bayesianModel.addDataRows(dataRows), is(3));
		assertThat(bayesianModel.countRowsWithWord("z"), is(0));
	}
	
	@Test
	public void testCountRowsWithCategoryWithWord() {
		@SuppressWarnings("unchecked")
		List<IDataRow<TestCategory>> dataRows = Arrays.asList(
				createMockRow(TestCategory.CATEGORY1, "a", "b", "c"),
				createMockRow(TestCategory.CATEGORY2, "a", "d"),
				createMockRow(TestCategory.CATEGORY1, "b", "c", "e"));
		assumeThat(bayesianModel.addDataRows(dataRows), is(3));
		assertThat(bayesianModel.countRowsWithCategoryWithWord(TestCategory.CATEGORY1, "a"), is(1));
		assertThat(bayesianModel.countRowsWithCategoryWithWord(TestCategory.CATEGORY1, "b"), is(2));
		assertThat(bayesianModel.countRowsWithCategoryWithWord(TestCategory.CATEGORY2, "a"), is(1));
	}
	
	@Test
	public void testCountRowsWithCategoryWithWord_NullCategory() {
		@SuppressWarnings("unchecked")
		List<IDataRow<TestCategory>> dataRows = Arrays.asList(
				createMockRow(TestCategory.CATEGORY1, "a", "b", "c"),
				createMockRow(TestCategory.CATEGORY2, "a", "d"),
				createMockRow(TestCategory.CATEGORY1, "b", "c", "e"));
		assumeThat(bayesianModel.addDataRows(dataRows), is(3));
		assertThat(bayesianModel.countRowsWithCategoryWithWord(null, "a"), is(0));
	}
	
	@Test
	public void testCountRowsWithCategoryWithWord_NullWord() {
		@SuppressWarnings("unchecked")
		List<IDataRow<TestCategory>> dataRows = Arrays.asList(
				createMockRow(TestCategory.CATEGORY1, "a", "b", "c"),
				createMockRow(TestCategory.CATEGORY2, "a", "d"),
				createMockRow(TestCategory.CATEGORY1, "b", "c", "e"));
		assumeThat(bayesianModel.addDataRows(dataRows), is(3));
		assertThat(bayesianModel.countRowsWithCategoryWithWord(TestCategory.CATEGORY1, null), is(0));
	}
	
	@Test
	public void testCountRowsWithCategoryWithWord_UnusedCategory() {
		@SuppressWarnings("unchecked")
		List<IDataRow<TestCategory>> dataRows = Arrays.asList(
				createMockRow(TestCategory.CATEGORY1, "a", "b", "c"),
				createMockRow(TestCategory.CATEGORY2, "a", "d"),
				createMockRow(TestCategory.CATEGORY1, "b", "c", "e"));
		assumeThat(bayesianModel.addDataRows(dataRows), is(3));
		assertThat(bayesianModel.countRowsWithCategoryWithWord(TestCategory.CATEGORY3, "a"), is(0));
	}
	
	@Test
	public void testCountRowsWithCategoryWithWord_UnusedWord() {
		@SuppressWarnings("unchecked")
		List<IDataRow<TestCategory>> dataRows = Arrays.asList(
				createMockRow(TestCategory.CATEGORY1, "a", "b", "c"),
				createMockRow(TestCategory.CATEGORY2, "a", "d"),
				createMockRow(TestCategory.CATEGORY1, "b", "c", "e"));
		assumeThat(bayesianModel.addDataRows(dataRows), is(3));
		assertThat(bayesianModel.countRowsWithCategoryWithWord(TestCategory.CATEGORY1, "z"), is(0));
	}
	
	@Test
	public void testCountRowsWithCategoryWithWord_UnusedCategoryAndWord() {
		@SuppressWarnings("unchecked")
		List<IDataRow<TestCategory>> dataRows = Arrays.asList(
				createMockRow(TestCategory.CATEGORY1, "a", "b", "c"),
				createMockRow(TestCategory.CATEGORY2, "a", "d"),
				createMockRow(TestCategory.CATEGORY1, "b", "c", "e"));
		assumeThat(bayesianModel.addDataRows(dataRows), is(3));
		assertThat(bayesianModel.countRowsWithCategoryWithWord(TestCategory.CATEGORY3, "z"), is(0));
	}
	
	@Test
	public void testRemoveCategory() {
		@SuppressWarnings("unchecked")
		List<IDataRow<TestCategory>> dataRows = Arrays.asList(
				createMockRow(TestCategory.CATEGORY1, "a", "b", "c"),
				createMockRow(TestCategory.CATEGORY2, "a", "d"),
				createMockRow(TestCategory.CATEGORY1, "b", "c", "e"));
		assumeThat(bayesianModel.addDataRows(dataRows), is(3));
		assertThat(bayesianModel.removeCategory(TestCategory.CATEGORY1), is(TestCategory.CATEGORY1));
		assertThat(bayesianModel.getTotalRows(), is(1));
		assertThat(bayesianModel.getUniqueCategories().size(), is(1));
		assertThat(bayesianModel.getUniqueWords().size(), is(2));
	}
	
	@Test
	public void testRemoveCategory_NullCategory() {
		assertThat(bayesianModel.removeCategory(null), is(nullValue()));
	}
	
	@Test
	public void testRemoveCategory_UnusedCategory() {
		assertThat(bayesianModel.removeCategory(TestCategory.CATEGORY1), is(nullValue()));
	}
	
	@Test
	public void testRemoveCategories() {
		@SuppressWarnings("unchecked")
		List<IDataRow<TestCategory>> dataRows = Arrays.asList(
				createMockRow(TestCategory.CATEGORY1, "a", "b", "c"),
				createMockRow(TestCategory.CATEGORY2, "a", "d"),
				createMockRow(TestCategory.CATEGORY1, "b", "c", "e"));
		List<TestCategory> categoriesToRemove = new ArrayList<TestCategory>();
		categoriesToRemove.add(TestCategory.CATEGORY1);
		categoriesToRemove.add(null);
		categoriesToRemove.add(TestCategory.CATEGORY3);
		
		assumeThat(bayesianModel.addDataRows(dataRows), is(3));
		List<TestCategory> removedCategories = bayesianModel.removeCategories(categoriesToRemove);
		assertThat(removedCategories, is(notNullValue()));
		assertThat(removedCategories.size(), is(1));
		assertThat(bayesianModel.getTotalRows(), is(1));
		assertThat(bayesianModel.getUniqueCategories().size(), is(1));
		assertThat(bayesianModel.getUniqueWords().size(), is(2));
	}
	
	@Test
	public void testRemoveCategories_NullList() {
		List<TestCategory> removedCategories = bayesianModel.removeCategories(null);
		assertThat(removedCategories, is(notNullValue()));
		assertThat(removedCategories.isEmpty(), is(true));
	}
	
	@Test
	public void testRemoveCategories_EmptyList() {
		List<TestCategory> emptyCategories = Collections.emptyList();
		List<TestCategory> removedCategories = bayesianModel.removeCategories(emptyCategories);
		assertThat(removedCategories, is(notNullValue()));
		assertThat(removedCategories.isEmpty(), is(true));
	}
	
	@Test
	public void testGetLinks() {
		@SuppressWarnings("unchecked")
		List<IDataRow<TestCategory>> dataRows = Arrays.asList(
				createMockRow(TestCategory.CATEGORY1, "a", "b", "c"),
				createMockRow(TestCategory.CATEGORY2, "a", "d"),
				createMockRow(TestCategory.CATEGORY1, "b", "c", "e"));
		assumeThat(bayesianModel.addDataRows(dataRows), is(3));
		List<ILink<TestCategory>> links = bayesianModel.getLinks();
		assertThat(links, is(notNullValue()));
		assertThat(links.size(), is(6));
		assertTrue(hasLink(TestCategory.CATEGORY1, "a", 1, links));
		assertTrue(hasLink(TestCategory.CATEGORY1, "b", 2, links));
		assertTrue(hasLink(TestCategory.CATEGORY1, "c", 2, links));
		assertTrue(hasLink(TestCategory.CATEGORY1, "e", 1, links));
		assertTrue(hasLink(TestCategory.CATEGORY2, "a", 1, links));
		assertTrue(hasLink(TestCategory.CATEGORY2, "d", 1, links));
	}
	
	private static void assertBayesianModelUnchanged(BayesianModel<TestCategory> bayesianModel) {
		assertThat(bayesianModel.getTotalRows(), is(0));
		assertThat(bayesianModel.getUniqueCategories().size(), is(0));
		assertThat(bayesianModel.getUniqueWords().size(), is(0));
	}
	private static void assertBayesianModelCategoriesUpdated(BayesianModel<TestCategory> bayesianModel) {		
		Set<TestCategory> uniqueCategories = bayesianModel.getUniqueCategories();
		assertThat(uniqueCategories.size(), is(1));
		assertThat(uniqueCategories, hasItem(testCategory));
	}
	private static void assertBayesianModelWordsUpdated(BayesianModel<TestCategory> bayesianModel) {
		Set<String> uniqueWords = bayesianModel.getUniqueWords();
		assertThat(uniqueWords.size(), is(1));
		assertThat(uniqueWords, hasItem(testWord));
	}
	private static void assertBayesianModelUpdated(BayesianModel<TestCategory> bayesianModel) {
		assertThat(bayesianModel.getTotalRows(), is(1));
		assertBayesianModelCategoriesUpdated(bayesianModel);
		assertBayesianModelWordsUpdated(bayesianModel);
	}
	private static boolean hasLink(TestCategory category, String word, int weight, List<ILink<TestCategory>> links) {
		for(ILink<TestCategory> link : links) {
			if( category.equals(link.getCategory()) && word.equals(link.getWord()) && weight == link.getWeight() ) {
				return true;
			}
		}
		return false;
	}
	
	private IDataRow<TestCategory> createMockRow() {
		@SuppressWarnings("unchecked")
		IDataRow<TestCategory> mockRow = (IDataRow<TestCategory>)mock(IDataRow.class);
		when(mockRow.getId()).thenReturn(1L);
		when(mockRow.getCategory()).thenReturn(testCategory);
		when(mockRow.getSentence()).thenReturn(testWord);
		when(mockRow.getSentenceWords()).thenReturn(Arrays.asList(testWord));
		when(mockRow.getUniqueSentenceWords()).thenReturn(Sets.newSet(testWord));
		
		return mockRow;
	}
	private IDataRow<TestCategory> createMockRow(TestCategory category, String...words) {
		@SuppressWarnings("unchecked")
		IDataRow<TestCategory> mockRow = (IDataRow<TestCategory>)mock(IDataRow.class);
		when(mockRow.getCategory()).thenReturn(category);
		when(mockRow.getUniqueSentenceWords()).thenReturn(Sets.newSet(words));
		
		return mockRow;
	}
}
