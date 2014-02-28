package com.nickferraro.bayesian.model;

import java.util.List;
import java.util.Set;

import com.nickferraro.bayesian.IDataRow;

/**
 * This interface represents a model for Bayesian calculations.
 * @author Nick Ferraro
 *
 * @param <T> The category data type.
 */
public interface IBayesianModel<T> {
	/**
	 * Adds a category to the model. The total rows and counts will not be affected.
	 * @param category The category to add.
	 * @return TRUE if the category was successfully added. FALSE if the category already existed or was NULL.
	 */
	public boolean addCategory(T category);
	
	/**
	 * Adds a list of categories to the model. The total rows and counts will not be affected.
	 * @param categories The list of categories to add.
	 * @return The number of categories successfully added. Duplicate categories will not be counted.
	 */
	public int addCategories(List<T> categories);
	
	/**
	 * Remove a category from the model. Any rows or counts related to this category will also be removed.
	 * @param category The category to remove.
	 * @return The category that was removed. NULL if the category does not exist.
	 */
	public T removeCategory(T category);
	
	/**
	 * Remove a list of categories from the model. Any rows or counts related to each category will also be removed.
	 * @param categories The list of categories to remove.
	 * @return A list of removed categories. An empty list if not categories were removed. Will never return NULL.
	 */
	public List<T> removeCategories(List<T> categories);
	
	/**
	 * Add a word to the model. The total rows and counts will not be affected.
	 * @param word The word to add
	 * @return TRUE if the word was successfully added. FALSE if the word already exists or is NULL.
	 */
	public boolean addWord(String word);
	
	/**
	 * Add a list of words to the model. The total rows and counts will not be affected.
	 * @param words The list of words to add
	 * @return The number of words successfully added. Duplicate words are not counted.
	 */
	public int addWords(List<String> words);
	
	/**
	 * Add a row of data to the model. The total rows and counts will be updated.
	 * @param dataRow The row of data to add. Must have a non-null category.
	 * @return TRUE if the data row was successfully added.
	 */
	public boolean addDataRow(IDataRow<T> dataRow);
	
	/**
	 * Add a list of data rows to the model. The total rows and counts will be updated.
	 * @param dataRows The list of data rows to add.
	 * @return The number of rows successfully added to the model.
	 */
	public int addDataRows(List<IDataRow<T>> dataRows);
	
	/**
	 * Counts the number of rows in the model with the specified category.
	 * @param category The category to look for and count.
	 * @return The number of rows found to be associated with the specified category.
	 */
	public int countRowsWithCategory(T category);
	
	/**
	 * Counts the number of rows in the model with the specified word.
	 * @param word The word to look for and count.
	 * @return The number of rows found to be associated with the specified word.
	 */
	public int countRowsWithWord(String word);
	
	/**
	 * Count the number of rows in the model with the specified category and word.
	 * For a row to be counted, it must have the category AND the word.
	 * @param category The category to look for and count.
	 * @param word The word to look for and count.
	 * @return The number of rows found to have the specified category AND word.
	 */
	public int countRowsWithCategoryWithWord(T category, String word);
	
	/**
	 * Get the total number of rows in this model.
	 * @return The total number of rows. This will never be less than 0.
	 */
	public int getTotalRows();
	
	/**
	 * Get a set of the unique categories in this model.
	 * @return A set of unique categories. This will never be NULL.
	 */
	public Set<T> getUniqueCategories();
	
	/**
	 * Get a set of the unique words in this model.
	 * @return A set of unique words. This will never be NULL.
	 */
	public Set<String> getUniqueWords();
}
