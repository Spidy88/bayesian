package com.nickferraro.bayesian.model.hashed;

/**
 * Represents a link between a category and a word.
 * @author Nick Ferraro
 *
 * @param <T> The data type of the link category
 */
public interface ILink<T> {
	/**
	 * Get the category for this link
	 * @return The link category
	 */
	public T getCategory();
	
	/**
	 * Get the word for this link
	 * @return The link word
	 */
	public String getWord();
	
	/**
	 * Get the weight associated with this link.
	 * @return The weight of this link. Will always be a positive number (including 0).
	 */
	public int getWeight();
}
