package com.nickferraro.bayesian.io;

import java.text.ParseException;

/**
 * 
 * @author Nick Ferraro
 * 
 * @param <T>
 */
public interface ICategoryParser<T> {
	public T parseCategory(String category) throws ParseException;
}
