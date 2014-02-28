package com.nickferraro.bayesian;

/**
 * This interface represents a classification output.
 * @author Nick Ferraro
 *
 * @param <T> The data type of the category.
 */
public interface IClassification<T> {
	/**
	 * Get the category of this classification.
	 * @return The category of this classification. Must not be NULL.
	 */
	public T getCategory();
	
	/**
	 * Get the probability of this classification.
	 * @return The probability of this category classification.
	 */
	public double getProbability();
}
