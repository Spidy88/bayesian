package com.nickferraro.bayesian.io.json.core;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.security.InvalidParameterException;
import java.text.ParseException;
import java.util.Set;

import org.junit.After;
import org.junit.Test;

import com.nickferraro.bayesian.io.ICategoryParser;
import com.nickferraro.bayesian.model.IBayesianModel;

public class BayesianJsonReaderTest {
	private BayesianJsonReader reader;
	private final String goodModel = "{'unique-words-count':3,'unique-categories':[{'category':'A','count':2},{'category':'B','count':2},{'category':'C','count':2}],'rows-count':6,'unique-categories-count':3,'links-count':7,'links':[{'category':'A','weight':2,'word':'x'},{'category':'A','weight':1,'word':'y'},{'category':'B','weight':1,'word':'x'},{'category':'B','weight':2,'word':'y'},{'category':'B','weight':1,'word':'z'},{'category':'C','weight':1,'word':'y'},{'category':'C','weight':2,'word':'z'}],'unique-words':[{'count':3,'word':'z'},{'count':4,'word':'y'},{'count':3,'word':'x'}]}";
	private final String emptyModel = "{'unique-words-count':0,'unique-categories':[],'rows-count':0,'unique-categories-count':0,'links-count':0,'links':[],'unique-words':[]}";
	private final String badModel = "{}";
	private final String badJson = ">badjson<";
	private final ICategoryParser<String> stringParser = new ICategoryParser<String>() {
		@Override
		public String parseCategory(String category) throws ParseException {
			return category;
		}
	};
	
	@After
	public void teardown() {
		try {
			reader.close();
		} catch(Exception e) { }
	}
	
	@Test(expected = InvalidParameterException.class)
	public void testConstructor_Null() {
		new BayesianJsonReader(null);
	}
	
	@Test
	public void testReadModel() throws IOException {
		ByteArrayInputStream inputStream = new ByteArrayInputStream(goodModel.getBytes());
		InputStreamReader inputReader = new InputStreamReader(inputStream);
		BufferedReader bufferedReader = new BufferedReader(inputReader);
		reader = new BayesianJsonReader(bufferedReader);
		
		IBayesianModel<String> model = reader.readModel(stringParser);
		
		assertThat(model, is(notNullValue()));
		assertThat(model.getTotalRows(), is(6));
		Set<String> uniqueCategories = model.getUniqueCategories();
		assertThat(uniqueCategories, is(notNullValue()));
		assertThat(uniqueCategories.size(), is(3));
		Set<String> uniqueWords = model.getUniqueWords();
		assertThat(uniqueWords, is(notNullValue()));
		assertThat(uniqueWords.size(), is(3));
		assertThat(model.countRowsWithCategory("A"), is(2));
		assertThat(model.countRowsWithCategory("B"), is(2));
		assertThat(model.countRowsWithCategory("C"), is(2));
		assertThat(model.countRowsWithWord("x"), is(3));
		assertThat(model.countRowsWithWord("y"), is(4));
		assertThat(model.countRowsWithWord("z"), is(3));
		assertThat(model.countRowsWithCategoryWithWord("A", "x"), is(2));
		assertThat(model.countRowsWithCategoryWithWord("A", "y"), is(1));
		assertThat(model.countRowsWithCategoryWithWord("A", "z"), is(0));
		assertThat(model.countRowsWithCategoryWithWord("B", "x"), is(1));
		assertThat(model.countRowsWithCategoryWithWord("B", "y"), is(2));
		assertThat(model.countRowsWithCategoryWithWord("B", "z"), is(1));
		assertThat(model.countRowsWithCategoryWithWord("C", "x"), is(0));
		assertThat(model.countRowsWithCategoryWithWord("C", "y"), is(1));
		assertThat(model.countRowsWithCategoryWithWord("C", "z"), is(2));
	}
	
	@Test(expected = IOException.class)
	public void testReadModel_BadModel() throws IOException {
		ByteArrayInputStream inputStream = new ByteArrayInputStream(badModel.getBytes());
		InputStreamReader inputReader = new InputStreamReader(inputStream);
		BufferedReader bufferedReader = new BufferedReader(inputReader);
		reader = new BayesianJsonReader(bufferedReader);
		
		reader.readModel(stringParser);
	}
	
	@Test(expected = IOException.class)
	public void testReadModel_EmptyReader() throws IOException {
		Reader mockReader = mock(Reader.class);
		when(mockReader.ready()).thenReturn(false);
		BufferedReader bufferedReader = new BufferedReader(mockReader);
		reader = new BayesianJsonReader(bufferedReader);
		
		reader.readModel(stringParser);
	}
	
	@Test(expected = IOException.class)
	public void testReadModel_BadJson() throws IOException {
		ByteArrayInputStream inputStream = new ByteArrayInputStream(badJson.getBytes());
		InputStreamReader inputReader = new InputStreamReader(inputStream);
		BufferedReader bufferedReader = new BufferedReader(inputReader);
		reader = new BayesianJsonReader(bufferedReader);
		
		reader.readModel(stringParser);
	}
	
	@Test(expected = IOException.class)
	public void testReadModel_ReadTwice() throws IOException {
		ByteArrayInputStream inputStream = new ByteArrayInputStream(goodModel.getBytes());
		InputStreamReader inputReader = new InputStreamReader(inputStream);
		BufferedReader bufferedReader = new BufferedReader(inputReader);
		reader = new BayesianJsonReader(bufferedReader);
		
		reader.readModel(stringParser);
		reader.readModel(stringParser);
	}
	
	@Test(expected = IOException.class)
	public void testReadModel_NullParser() throws IOException {
		ByteArrayInputStream inputStream = new ByteArrayInputStream(goodModel.getBytes());
		InputStreamReader inputReader = new InputStreamReader(inputStream);
		BufferedReader bufferedReader = new BufferedReader(inputReader);
		reader = new BayesianJsonReader(bufferedReader);
		
		reader.readModel(null);
	}
	
	@Test
	public void testReadModel_EmptyModel() throws IOException {
		ByteArrayInputStream inputStream = new ByteArrayInputStream(emptyModel.getBytes());
		InputStreamReader inputReader = new InputStreamReader(inputStream);
		BufferedReader bufferedReader = new BufferedReader(inputReader);
		reader = new BayesianJsonReader(bufferedReader);
		
		reader.readModel(stringParser);
	}
}
