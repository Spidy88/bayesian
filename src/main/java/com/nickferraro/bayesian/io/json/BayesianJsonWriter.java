package com.nickferraro.bayesian.io.json;

import java.io.IOException;
import java.io.Writer;
import java.security.InvalidParameterException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.nickferraro.bayesian.io.IBayesianWriter;
import com.nickferraro.bayesian.model.IBayesianModel;
import com.nickferraro.bayesian.model.hashed.ILink;

/**
 * A class for writing a bayesian model to output in json format. This class is thread-safe and can only
 * write a single bayesian model to output.
 * @author Nick Ferraro
 *
 */
public class BayesianJsonWriter implements IBayesianWriter {
	public static final String KEY_ROWS_COUNT = "rows-count";
	public static final String KEY_UNIQUE_CATEGORIES_COUNT = "unique-categories-count";
	public static final String KEY_UNIQUE_WORDS_COUNT = "unique-words-count";
	public static final String KEY_LINKS_COUNT = "links-count";
	public static final String KEY_UNIQUE_CATEGORIES = "unique-categories";
	public static final String KEY_UNIQUE_WORDS = "unique-words";
	public static final String KEY_CATEGORY = "category";
	public static final String KEY_WORD = "word";
	public static final String KEY_LINKS = "links";
	public static final String KEY_WEIGHT = "weight";
	public static final String KEY_COUNT = "count";
	
	private final Writer writer;
	private JSONObject bayesianObject;
	
	/**
	 * Creates an instance of a BayesianJsonWriter.
	 * @param writer The writer to use for output. Cannot be NULL.
	 * @throws InvalidParameterException Thrown when writer is NULL.
	 */
	public BayesianJsonWriter(Writer writer) throws InvalidParameterException {
		// Validate the writer parameter
		if( writer == null ) {
			throw new InvalidParameterException();
		}
		
		// Create a buffered instance of 
		this.writer = writer;
	}
	
	/**
	 * {@inheritDoc}
	 * This method can only write a single bayesian model to output.
	 * @throws IOException Thrown when the model is NULL, a bayesian object has already been written, or writing failed for any other reason.
	 */
	@Override
	public synchronized <T> void writeModel(IBayesianModel<T> model) throws IOException {
		// Verify model has not already been written
		if( this.bayesianObject != null ) {
			throw new IOException("BayesianJsonWriter can only write a single bayesian model to output");
		}
		
		// Validate the input
		if(model == null) {
			throw new IOException("Cannot write a NULL model");
		}
		
		try {
			// Create our root bayesian json object
			JSONObject bayesianObject = new JSONObject();
			
			// Add the rows count attribute
			bayesianObject.put(KEY_ROWS_COUNT, model.getTotalRows());

			// Get the set of unique categories, should never be null but check for safety
			Set<T> uniqueCategories = model.getUniqueCategories();
			if( uniqueCategories == null ) {
				uniqueCategories = Collections.emptySet();
			}
			
			// Add the unique categories count attribute
			bayesianObject.put(KEY_UNIQUE_CATEGORIES_COUNT, uniqueCategories.size());

			// Create the unique categories json array
			JSONArray uniqueCategoriesArray = new JSONArray();
			for(T uniqueCategory : uniqueCategories) {
				// Create a single unique categorie json object
				JSONObject categoryObject = new JSONObject();
				
				// Add the category name and row count attributes
				categoryObject.put(KEY_CATEGORY, uniqueCategory.toString());
				categoryObject.put(KEY_COUNT, model.countRowsWithCategory(uniqueCategory));
				
				// Add the category object to the word array
				uniqueCategoriesArray.put(categoryObject);
			}
			
			// Add the unique categories json array
			bayesianObject.put(KEY_UNIQUE_CATEGORIES, uniqueCategoriesArray);
			
			// Get the set of unique words, should never be null but check for safety
			Set<String> uniqueWords = model.getUniqueWords();
			if( uniqueWords == null ) {
				uniqueWords = Collections.emptySet();
			}
			
			// Add the unique words count attribute
			bayesianObject.put(KEY_UNIQUE_WORDS_COUNT, uniqueWords.size());

			// Create the unique words json array
			JSONArray uniqueWordsArray = new JSONArray();
			for(String uniqueWord : uniqueWords) {
				// Create a single unique word json object
				JSONObject wordObject = new JSONObject();
				
				// Add the word name and row count attributes
				wordObject.put(KEY_CATEGORY, uniqueWord.toString());
				wordObject.put(KEY_COUNT, model.countRowsWithWord(uniqueWord));
				
				// Add the word object to the word array
				uniqueWordsArray.put(wordObject);
			}
			
			// Add the unique words json array
			bayesianObject.put(KEY_UNIQUE_WORDS, uniqueWordsArray);
			
			// Get the list of links, should never be null but check for safety
			List<ILink<T>> links = model.getLinks();
			if( links == null ) {
				links = Collections.emptyList();
			}
			
			// Add the links count attribute
			bayesianObject.put(KEY_LINKS_COUNT, links.size());
			
			// Create the links json array
			JSONArray linksArray = new JSONArray();
			for(ILink<T> link : links) {
				// Create a link json object
				JSONObject linkObject = new JSONObject();
				
				// Add the category, word, and weight attributes
				linkObject.put(KEY_CATEGORY, link.getCategory().toString());
				linkObject.put(KEY_WORD, link.getWord());
				linkObject.put(KEY_WEIGHT, link.getWeight());
				
				// Add the link object to the array
				linksArray.put(linkObject);
			}
			
			// Add the links array attribute
			bayesianObject.put(KEY_LINKS, linksArray);
			
			// Write the json object to output
			writer.write(bayesianObject.toString());
			
			// Store this json object
			this.bayesianObject = bayesianObject;
		} catch(JSONException e) {
			throw new IOException("Failed to create a JSON representation of an IBayesianModel", e);
		}
	}
	
	@Override
	public synchronized void close() throws IOException {
		writer.close();
	}
}
