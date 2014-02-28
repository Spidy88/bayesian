package com.nickferraro.bayesian;

import java.security.InvalidParameterException;
import java.util.List;

/**
 * A Bayesian System hosts a Bayesian Model and handles training and classifying data.
 * @author Nick Ferraro
 *
 * @param <T> The data type of the category this system will be classifying
 */
public interface IBayesianSystem<T> {
	/**
	 * Get a list of ordered classifications from greatest to least probability for the specified sentence.
	 * @param sentenceInput The sentence to classify. Must not be NULL.
	 * @return A list of ordered classifications from greatest to least probability. Will never be NULL.
	 * @throws InvalidParameterException Thrown when sentenceInput is NULL.
	 */
	public List<IClassification<T>> classifyRow(ISentenceInput sentenceInput) throws InvalidParameterException;
	
	/**
	 * Get a limited size list of ordered classifications from greatest to least probability for the specified sentence.
	 * @param sentenceInput The sentence to classify. Must not be NULL.
	 * @param maxResults The max results to return in the list.
	 * @return A list of ordered classifications from greatest to least probability. Will never be NULL.
	 * @throws InvalidParameterException Thrown when sentenceInput is NULL.
	 */
	public List<IClassification<T>> classifyRow(ISentenceInput sentenceInput, int maxResults) throws InvalidParameterException;
	
	/**
	 * Train the bayesian system's model on the specified data row. 
	 * This updates the model and any previously trained data will remain in the model.
	 * @param dataRow The data row to train the model with.
	 */
	public void trainOnRow(IDataRow<T> dataRow);
	
	/**
	 * Train the bayesian system's model on a list of data rows.
	 * This updates the model and any previously trained data will remain in the model.
	 * @param dataRows A list of data rows to train the model with.
	 */
	public void trainOnRows(List<IDataRow<T>> dataRows);
}
