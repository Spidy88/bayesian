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
 * A bayesian model reader that uses JSON to parse a bayesian model.
 * @author Nick Ferraro
 *
 */
public class BayesianJsonReader implements IBayesianReader {
	private final BufferedReader reader;
	
	/**
	 * Create a bayesian model json reader
	 * @param reader The reader to use for reading a bayesian model
	 * @throws InvalidParameterException Thrown when the reader is NULL.
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
	
	/**
	 * Creates a hashed bayesian model from a json string. Uses the category parser to parse
	 * categories back into their original class, enum, or primitive state.
	 * @param parser A parser for turning categories from strings back into their origin class, enum, or primitive state
	 * @param jsonString A string representing a hashed bayesian model
	 * @return The rebuilt hashed bayesian model
	 * @throws ParseException Thrown when the json string is invalid or attributes are missing
	 */
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
	
	/**
	 * Read the categories section from the bayesian object and update the model
	 * @param bayesianObject The bayesian object to use for reading categories
	 * @param model The model to update with what has been read from the bayesian object
	 * @param parser The parser to use for categories
	 * @throws ParseException Thrown when the category parser encounters an issue
	 */
	protected <T> void readCategories(JSONObject bayesianObject, BayesianModel<T> model, ICategoryParser<T> parser) throws ParseException {
		JSONArray categoriesArray = bayesianObject.getJSONArray(KEY_UNIQUE_CATEGORIES);
		for( int i = 0; i < categoriesArray.length(); ++i ) {
			JSONObject categoryObject = categoriesArray.getJSONObject(i);
			String category = categoryObject.getString(KEY_CATEGORY);
			int count = categoryObject.getInt(KEY_COUNT);

			model.setCategoryCount(parser.parseCategory(category), count);
		}
	}
	
	/**
	 * Read the words section from the bayesian object and update the model
	 * @param bayesianObject The bayesian object to use for reading words
	 * @param model The model to update with what has been read from the bayesian object
	 */
	protected <T> void readWords(JSONObject bayesianObject, BayesianModel<T> model) {
		JSONArray wordsArray = bayesianObject.getJSONArray(KEY_UNIQUE_WORDS);
		for( int i = 0; i < wordsArray.length(); ++i ) {
			JSONObject wordObject = wordsArray.getJSONObject(i);
			String word = wordObject.getString(KEY_WORD);
			int count = wordObject.getInt(KEY_COUNT);
			
			model.setWordCount(word, count);
		}
	}
	
	/**
	 * Read the links section from the bayesian object and update the model
	 * @param bayesianObject The bayesian object to use for reading links
	 * @param model The model to update with what has been read from the bayesian object
	 * @param parser The parser to use for categories
	 * @throws ParseException Thrown when the category parser encounters an issue
	 */
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
