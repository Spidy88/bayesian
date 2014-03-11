package com.nickferraro.bayesian.io;

import java.io.IOException;

import com.nickferraro.bayesian.model.IBayesianModel;

/**
 * An interface for reading a bayesian model from input/stream.
 * @author Nick Ferraro
 *
 */
public interface IBayesianReader {
	/**
	 * Read a BayesianModel that uses the specified category class type
	 * @param parser A parser that can convert a String back into its original data object
	 * @return The read and reassmebled bayesian model
	 * @throws IOException
	 */
	public <T> IBayesianModel<T> readModel(ICategoryParser<T> parser) throws IOException;
	
	/**
	 * Close the reader and free up its resources
	 * @throws IOException
	 */
	public void close() throws IOException;
}
