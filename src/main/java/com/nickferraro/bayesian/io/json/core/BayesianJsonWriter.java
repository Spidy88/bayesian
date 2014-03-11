package com.nickferraro.bayesian.io.json.core;

import java.io.IOException;
import java.io.Writer;
import java.security.InvalidParameterException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.nickferraro.bayesian.io.json.IBayesianJsonWriter;
import com.nickferraro.bayesian.model.IBayesianModel;
import com.nickferraro.bayesian.model.hashed.ILink;
import static com.nickferraro.bayesian.io.json.core.BayesianJsonKeys.*;
/**
 * A class for writing a bayesian model to output in json format. This class is thread-safe and can only
 * write a single bayesian model to output.
 * @author Nick Ferraro
 *
 */
public class BayesianJsonWriter implements IBayesianJsonWriter {
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

			// Create the unique categories json array
			JSONArray uniqueCategoriesArray = writeCategoriesArray(model);
			
			// Add the unique categories count attribute
			bayesianObject.put(KEY_UNIQUE_CATEGORIES_COUNT, uniqueCategoriesArray.length());
			
			// Add the unique categories json array
			bayesianObject.put(KEY_UNIQUE_CATEGORIES, uniqueCategoriesArray);

			// Create the unique words json array
			JSONArray uniqueWordsArray = writeWordsArray(model);
			
			// Add the unique words count attribute
			bayesianObject.put(KEY_UNIQUE_WORDS_COUNT, uniqueWordsArray.length());
			
			// Add the unique words json array
			bayesianObject.put(KEY_UNIQUE_WORDS, uniqueWordsArray);
			
			// Create the links json array
			JSONArray linksArray = writeLinksArray(model);
			
			// Add the links count attribute
			bayesianObject.put(KEY_LINKS_COUNT, linksArray.length());
			
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
	
	@Override
	public synchronized JSONObject getJsonObject() {
		return this.bayesianObject;
	}
	
	protected <T> JSONArray writeCategoriesArray(IBayesianModel<T> model) {
		// Get the set of unique categories, should never be null but check for safety
		Set<T> uniqueCategories = model.getUniqueCategories();
		if( uniqueCategories == null ) {
			uniqueCategories = Collections.emptySet();
		}

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
		
		return uniqueCategoriesArray;
	}
	protected <T> JSONArray writeWordsArray(IBayesianModel<T> model) {
		// Get the set of unique words, should never be null but check for safety
		Set<String> uniqueWords = model.getUniqueWords();
		if( uniqueWords == null ) {
			uniqueWords = Collections.emptySet();
		}
					
		// Create the unique words json array
		JSONArray uniqueWordsArray = new JSONArray();
		for(String uniqueWord : uniqueWords) {
			// Create a single unique word json object
			JSONObject wordObject = new JSONObject();
			
			// Add the word name and row count attributes
			wordObject.put(KEY_WORD, uniqueWord.toString());
			wordObject.put(KEY_COUNT, model.countRowsWithWord(uniqueWord));
			
			// Add the word object to the word array
			uniqueWordsArray.put(wordObject);
		}
		
		return uniqueWordsArray;
	}
	
	protected <T> JSONArray writeLinksArray(IBayesianModel<T> model) {
		// Get the list of links, should never be null but check for safety
		List<ILink<T>> links = model.getLinks();
		if( links == null ) {
			links = Collections.emptyList();
		}
		
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
		
		return linksArray;
	}
}
