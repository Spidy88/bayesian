package com.nickferraro.bayesian.io.json.core;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.StringWriter;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.nickferraro.bayesian.model.IBayesianModel;
import com.nickferraro.bayesian.model.hashed.ILink;

public class BayesianJsonWriterTest {
	private IBayesianModel<String> mockModel;
	private StringWriter stringWriter;
	private BayesianJsonWriter writer;
	
	@SuppressWarnings("unchecked")
	@Before
	public void setup() {
		mockModel = mock(IBayesianModel.class); 
		stringWriter = new StringWriter();
		writer = new BayesianJsonWriter(stringWriter);
	}
	
	@After
	public void teardown() {
		try {
			writer.close();
		} catch (IOException e) {
		}
	}
	
	@Test(expected = InvalidParameterException.class)
	public void testConstructor_Null() {
		new BayesianJsonWriter(null);
	}
	
	@Test
	public void testWriteModel() throws IOException {
		Set<String> categories = createCategories();
		Set<String> words = createWords();
		List<ILink<String>> links = createLinks();
		
		when(mockModel.getUniqueCategories()).thenReturn(categories);
		when(mockModel.getUniqueWords()).thenReturn(words);
		when(mockModel.getLinks()).thenReturn(links);
		when(mockModel.getTotalRows()).thenReturn(6);
		when(mockModel.countRowsWithCategory("A")).thenReturn(2);
		when(mockModel.countRowsWithCategory("B")).thenReturn(2);
		when(mockModel.countRowsWithCategory("C")).thenReturn(2);
		when(mockModel.countRowsWithWord("x")).thenReturn(3);
		when(mockModel.countRowsWithWord("y")).thenReturn(4);
		when(mockModel.countRowsWithWord("z")).thenReturn(3);
		
		writer.writeModel(mockModel);
		
		String jsonString = stringWriter.toString();
		JSONObject jsonObject = new JSONObject(jsonString);
		assertBayesianModelJson(jsonObject);
		assertBayesianModelJson(writer.getJsonObject());
	}
	
	@Test
	public void testWriteModel_EmptyModel() throws IOException {
		Set<String> categories = Collections.emptySet();
		Set<String> words = Collections.emptySet();
		List<ILink<String>> links = Collections.emptyList();
		
		when(mockModel.getUniqueCategories()).thenReturn(categories);
		when(mockModel.getUniqueWords()).thenReturn(words);
		when(mockModel.getLinks()).thenReturn(links);
		
		writer.writeModel(mockModel);
		
		JSONObject jsonObject = writer.getJsonObject();
		assertThat(jsonObject.getInt(BayesianJsonKeys.KEY_ROWS_COUNT), is(0));
		assertThat(jsonObject.getInt(BayesianJsonKeys.KEY_UNIQUE_CATEGORIES_COUNT), is(0));
		assertThat(jsonObject.getInt(BayesianJsonKeys.KEY_UNIQUE_WORDS_COUNT), is(0));
		assertThat(jsonObject.getInt(BayesianJsonKeys.KEY_LINKS_COUNT), is(0));
		assertThat(jsonObject.getJSONArray(BayesianJsonKeys.KEY_UNIQUE_CATEGORIES).length(), is(0));
		assertThat(jsonObject.getJSONArray(BayesianJsonKeys.KEY_UNIQUE_WORDS).length(), is(0));
		assertThat(jsonObject.getJSONArray(BayesianJsonKeys.KEY_LINKS).length(), is(0));
	}
	
	@Test(expected = IOException.class)
	public void testWriteModel_WriteTwice() throws IOException {
		Set<String> categories = createCategories();
		Set<String> words = createWords();
		List<ILink<String>> links = createLinks();
		
		when(mockModel.getUniqueCategories()).thenReturn(categories);
		when(mockModel.getUniqueWords()).thenReturn(words);
		when(mockModel.getLinks()).thenReturn(links);
		when(mockModel.getTotalRows()).thenReturn(6);
		when(mockModel.countRowsWithCategory("A")).thenReturn(2);
		when(mockModel.countRowsWithCategory("B")).thenReturn(2);
		when(mockModel.countRowsWithCategory("C")).thenReturn(2);
		when(mockModel.countRowsWithWord("x")).thenReturn(3);
		when(mockModel.countRowsWithWord("y")).thenReturn(4);
		when(mockModel.countRowsWithWord("z")).thenReturn(3);
		
		writer.writeModel(mockModel);
		writer.writeModel(mockModel);
	}
	
	@Test(expected = IOException.class)
	public void testWriteModel_NullModel() throws IOException {
		writer.writeModel(null);
	}
	
	@Test
	public void testWriteModel_BadModel() throws IOException {
		writer.writeModel(mockModel);
		
		String jsonString = stringWriter.toString();
		JSONObject jsonObject = new JSONObject(jsonString);
		
		assertThat(jsonObject.getInt(BayesianJsonKeys.KEY_ROWS_COUNT), is(0));
		assertThat(jsonObject.getInt(BayesianJsonKeys.KEY_UNIQUE_CATEGORIES_COUNT), is(0));
		assertThat(jsonObject.getInt(BayesianJsonKeys.KEY_UNIQUE_WORDS_COUNT), is(0));
		assertThat(jsonObject.getInt(BayesianJsonKeys.KEY_LINKS_COUNT), is(0));
		
		JSONArray categoriesArray = jsonObject.getJSONArray(BayesianJsonKeys.KEY_UNIQUE_CATEGORIES);
		assertThat(categoriesArray.length(), is(0));
		
		JSONArray wordsArray = jsonObject.getJSONArray(BayesianJsonKeys.KEY_UNIQUE_WORDS);
		assertThat(wordsArray.length(), is(0));
		
		JSONArray linksArray = jsonObject.getJSONArray(BayesianJsonKeys.KEY_LINKS);
		assertThat(linksArray.length(), is(0));
	}
	
	@Test
	public void testWriteCategories_Null() {
		when(mockModel.getUniqueCategories()).thenReturn(null);
		JSONArray categoriesArray = writer.writeCategoriesArray(mockModel);
		assertThat(categoriesArray, is(notNullValue()));
		assertThat(categoriesArray.length(), is(0));
	}
	
	@Test
	public void testWriteCategories_Empty() {
		Set<String> emptySet = Collections.emptySet();
		when(mockModel.getUniqueCategories()).thenReturn(emptySet);
		JSONArray categoriesArray = writer.writeCategoriesArray(mockModel);
		assertThat(categoriesArray, is(notNullValue()));
		assertThat(categoriesArray.length(), is(0));
	}
	
	@Test
	public void testWriteWords_Null() {
		when(mockModel.getUniqueWords()).thenReturn(null);
		JSONArray wordsArray = writer.writeWordsArray(mockModel);
		assertThat(wordsArray, is(notNullValue()));
		assertThat(wordsArray.length(), is(0));
	}
	
	@Test
	public void testWriteWords_Empty() {
		Set<String> emptySet = Collections.emptySet();
		when(mockModel.getUniqueWords()).thenReturn(emptySet);
		JSONArray wordsArray = writer.writeWordsArray(mockModel);
		assertThat(wordsArray, is(notNullValue()));
		assertThat(wordsArray.length(), is(0));
	}
	
	@Test
	public void testWriteLinks_Null() {
		when(mockModel.getLinks()).thenReturn(null);
		JSONArray linksArray = writer.writeLinksArray(mockModel);
		assertThat(linksArray, is(notNullValue()));
		assertThat(linksArray.length(), is(0));
	}
	
	@Test
	public void testWriteLinks_Empty() {
		List<ILink<String>> emptySet = Collections.emptyList();
		when(mockModel.getLinks()).thenReturn(emptySet);
		JSONArray linksArray = writer.writeLinksArray(mockModel);
		assertThat(linksArray, is(notNullValue()));
		assertThat(linksArray.length(), is(0));
	}
	
	private static boolean hasCategory(String category, int count, JSONArray categoryArray) {
		for(int i = 0; i < categoryArray.length(); ++i ) {
			JSONObject categoryObject = categoryArray.getJSONObject(i);
			if( categoryObject.getString(BayesianJsonKeys.KEY_CATEGORY).equals(category) &&
					categoryObject.getInt(BayesianJsonKeys.KEY_COUNT) == count ) {
				return true;
			}
		}
		
		return false;
	}
	
	private static boolean hasWord(String word, int count, JSONArray wordArray) {
		for(int i = 0; i < wordArray.length(); ++i ) {
			JSONObject wordObject = wordArray.getJSONObject(i);
			if( wordObject.getString(BayesianJsonKeys.KEY_WORD).equals(word) &&
					wordObject.getInt(BayesianJsonKeys.KEY_COUNT) == count ) {
				return true;
			}
		}
		
		return false;
	}
	
	private static boolean hasLink(String category, String word, int weight, JSONArray linkArray) {
		for(int i = 0; i < linkArray.length(); ++i ) {
			JSONObject linkObject = linkArray.getJSONObject(i);
			if( linkObject.getString(BayesianJsonKeys.KEY_CATEGORY).equals(category) &&
					linkObject.getString(BayesianJsonKeys.KEY_WORD).equals(word) &&
					linkObject.getInt(BayesianJsonKeys.KEY_WEIGHT) == weight ) {
				return true;
			}
		}
		
		return false;
	}
	
	private static void assertBayesianModelJson(JSONObject jsonObject) {
		assertThat(jsonObject.getInt(BayesianJsonKeys.KEY_ROWS_COUNT), is(6));
		assertThat(jsonObject.getInt(BayesianJsonKeys.KEY_UNIQUE_CATEGORIES_COUNT), is(3));
		assertThat(jsonObject.getInt(BayesianJsonKeys.KEY_UNIQUE_WORDS_COUNT), is(3));
		assertThat(jsonObject.getInt(BayesianJsonKeys.KEY_LINKS_COUNT), is(7));
		
		JSONArray categoriesArray = jsonObject.getJSONArray(BayesianJsonKeys.KEY_UNIQUE_CATEGORIES);
		assertThat(categoriesArray.length(), is(3));
		assertThat(hasCategory("A", 2, categoriesArray), is(true));
		assertThat(hasCategory("B", 2, categoriesArray), is(true));
		assertThat(hasCategory("C", 2, categoriesArray), is(true));
		
		JSONArray wordsArray = jsonObject.getJSONArray(BayesianJsonKeys.KEY_UNIQUE_WORDS);
		assertThat(wordsArray.length(), is(3));
		assertThat(hasWord("x", 3, wordsArray), is(true));
		assertThat(hasWord("y", 4, wordsArray), is(true));
		assertThat(hasWord("z", 3, wordsArray), is(true));
		
		JSONArray linksArray = jsonObject.getJSONArray(BayesianJsonKeys.KEY_LINKS);
		assertThat(linksArray.length(), is(7));
		assertThat(hasLink("A", "x", 2, linksArray), is(true));
		assertThat(hasLink("A", "y", 1, linksArray), is(true));
		assertThat(hasLink("B", "x", 1, linksArray), is(true));
		assertThat(hasLink("B", "y", 2, linksArray), is(true));
		assertThat(hasLink("B", "z", 1, linksArray), is(true));
		assertThat(hasLink("C", "y", 1, linksArray), is(true));
		assertThat(hasLink("C", "z", 2, linksArray), is(true));
	}
	
	private static Set<String> createCategories() {
		Set<String> categorySet = new HashSet<String>();
		categorySet.add("A");
		categorySet.add("B");
		categorySet.add("C");
		
		return categorySet;
	}
	
	private static Set<String> createWords() {
		Set<String> wordSet = new HashSet<String>();
		wordSet.add("x");
		wordSet.add("y");
		wordSet.add("z");
		
		return wordSet;
	}
	
	private static List<ILink<String>> createLinks() {
		List<ILink<String>> linkList = new ArrayList<ILink<String>>();
		
		linkList.add(createMockLink("A", "x", 2));
		linkList.add(createMockLink("A", "y", 1));
		linkList.add(createMockLink("B", "x", 1));
		linkList.add(createMockLink("B", "y", 2));
		linkList.add(createMockLink("B", "z", 1));
		linkList.add(createMockLink("C", "y", 1));
		linkList.add(createMockLink("C", "z", 2));
		
		return linkList;
	}
	
	private static ILink<String> createMockLink(String category, String word, int weight) {
		@SuppressWarnings("unchecked")
		ILink<String> mockLink = mock(ILink.class);
		when(mockLink.getCategory()).thenReturn(category);
		when(mockLink.getWord()).thenReturn(word);
		when(mockLink.getWeight()).thenReturn(weight);
		
		return mockLink;
	}
}