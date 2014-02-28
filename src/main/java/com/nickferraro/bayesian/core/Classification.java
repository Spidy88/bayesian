package com.nickferraro.bayesian.core;

import java.security.InvalidParameterException;

import com.nickferraro.bayesian.IClassification;

/**
 * A bayesian classification POJO. This class has the classified category and the probability of the classification.
 * @author Nick Ferraro
 *
 * @param <T> The data type of the category.
 */
public class Classification<T> implements IClassification<T> {
	private final T category;
	private final double probability;
	
	/**
	 * Create a classification object. The category cannot be NULL and the probability must be between 0 and 1 inclusive.
	 * @param category The category of this classification.
	 * @param probability The probability of this classification being accurate.
	 * @throws InvalidParameterException Thrown when the category is NULL or the probability is not between 0 and 1 inclusive.
	 */
	public Classification(T category, double probability) throws InvalidParameterException {
		if( category == null ) {
			throw new InvalidParameterException("Cannot create a Classification with a NULL category");
		}
		if( probability < 0 || probability > 1 ) {
			throw new InvalidParameterException("Cannot create a Classification with a probability outside of [0.0,1.0]");
		}
		
		this.category = category;
		this.probability = probability;
	}
	
	@Override
	public T getCategory() {
		return this.category;
	}

	@Override
	public double getProbability() {
		return this.probability;
	}
}
