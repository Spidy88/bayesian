package com.nickferraro.bayesian.io.json.core;

import static com.nickferraro.bayesian.io.json.core.BayesianJsonKeys.KEY_CATEGORY;
import static com.nickferraro.bayesian.io.json.core.BayesianJsonKeys.KEY_COUNT;
import static com.nickferraro.bayesian.io.json.core.BayesianJsonKeys.KEY_LINKS;
import static com.nickferraro.bayesian.io.json.core.BayesianJsonKeys.KEY_ROWS_COUNT;
import static com.nickferraro.bayesian.io.json.core.BayesianJsonKeys.KEY_UNIQUE_CATEGORIES;
import static com.nickferraro.bayesian.io.json.core.BayesianJsonKeys.KEY_UNIQUE_WORDS;
import static com.nickferraro.bayesian.io.json.core.BayesianJsonKeys.KEY_WEIGHT;
import static com.nickferraro.bayesian.io.json.core.BayesianJsonKeys.KEY_WORD;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.security.InvalidParameterException;
import java.text.ParseException;

import org.json.JSONArray;
import org.json.JSONObject;

import com.nickferraro.bayesian.io.IBayesianReader;
import com.nickferraro.bayesian.io.ICategoryParser;
import com.nickferraro.bayesian.model.IBayesianModel;
import com.nickferraro.bayesian.model.hashed.BayesianModel;

/**
 * 
 * @author Nick Ferraro
 *
 */
public class BayesianJsonReader implements IBayesianReader {
	private final BufferedReader reader;
	
	/**
	 * 
	 * @param reader
	 * @throws InvalidParameterException
	 */
	public BayesianJsonReader(Reader reader) throws InvalidParameterException {
		if( reader == null ) {
			throw new InvalidParameterException("Cannot create a BayesianJsonReader with a NULL reader");
		}
		
		this.reader = new BufferedReader(reader);
	}
	
	@Override
	public synchronized <T> IBayesianModel<T> readModel(ICategoryParser<T> parser) throws IOException {
		try {
			if(parser == null) {
				throw new InvalidParameterException("Cannot use a NULL parser");
			}

			StringBuilder jsonStringBuilder = new StringBuilder();
			while(reader.ready()) {
				jsonStringBuilder.append(reader.readLine());
			}
			
			return parseModel(parser, jsonStringBuilder.toString());
		} catch(Exception e) {
			throw new IOException(e);
		}
	}

	@Override
	public synchronized void close() throws IOException {
		reader.close();
	}
	
	protected <T> IBayesianModel<T> parseModel(ICategoryParser<T> parser, String jsonString) throws ParseException {
		BayesianModel<T> model = new BayesianModel<T>();
		JSONObject bayesianObject = new JSONObject(jsonString);
		int totalRows = bayesianObject.getInt(KEY_ROWS_COUNT);
		
		model.setTotalRows(totalRows);
		readCategories(bayesianObject, model, parser);
		readWords(bayesianObject, model);
		readLinks(bayesianObject, model, parser);
		
		return model;
	}
	
	protected <T> void readCategories(JSONObject bayesianObject, BayesianModel<T> model, ICategoryParser<T> parser) throws ParseException {
		JSONArray categoriesArray = bayesianObject.getJSONArray(KEY_UNIQUE_CATEGORIES);
		for( int i = 0; i < categoriesArray.length(); ++i ) {
			JSONObject categoryObject = categoriesArray.getJSONObject(i);
			String category = categoryObject.getString(KEY_CATEGORY);
			int count = categoryObject.getInt(KEY_COUNT);

			model.setCategoryCount(parser.parseCategory(category), count);
		}
	}
	
	protected <T> void readWords(JSONObject bayesianObject, BayesianModel<T> model) throws ParseException {
		JSONArray wordsArray = bayesianObject.getJSONArray(KEY_UNIQUE_WORDS);
		for( int i = 0; i < wordsArray.length(); ++i ) {
			JSONObject wordObject = wordsArray.getJSONObject(i);
			String word = wordObject.getString(KEY_WORD);
			int count = wordObject.getInt(KEY_COUNT);
			
			model.setWordCount(word, count);
		}
	}
	
	protected <T> void readLinks(JSONObject bayesianObject, BayesianModel<T> model, ICategoryParser<T> parser) throws ParseException {
		JSONArray linksArray = bayesianObject.getJSONArray(KEY_LINKS);
		for( int i = 0; i < linksArray.length(); ++i ) {
			JSONObject linkObject = linksArray.getJSONObject(i);
			String category = linkObject.getString(KEY_CATEGORY);
			String word = linkObject.getString(KEY_WORD);
			int weight = linkObject.getInt(KEY_WEIGHT);
			
			model.setLinkWeight(parser.parseCategory(category), word, weight);
		}
	}
}
