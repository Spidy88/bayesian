package com.nickferraro.bayesian;


/**
 * An IDataRow represents a row of data used by a classification system.
 * The row must have an id, a category and a sentence associated with it.
 * For ease of use, the data row must also be able to give a list of sentence words (full list or unique set). 
 * @author Nick Ferraro
 *
 * @param <T> The data type of the category
 */
public interface IDataRow<T> extends ISentenceInput {
	/**
	 * Get the id of this data row
	 * @return The data row id
	 */
	public long getId();
	
	/**
	 * Get the category of this data row.
	 * @return The data row category. Must not be NULL.
	 */
	public T getCategory();
}
