package com.nickferraro.bayesian.io;

import java.io.IOException;

import com.nickferraro.bayesian.model.IBayesianModel;

/**
 * The interface for a Bayesian Model writer.
 * @author Nick Ferraro
 *
 */
public interface IBayesianWriter {
	/**
	 * Write the bayesian model to specified writer/stream.
	 * @param model The model to write
	 * @throws IOException Throws an IOException when the model is NULL, or writing fails.
	 */
	public <T> void writeModel(IBayesianModel<T> model) throws IOException;
	
	/**
	 * Close the writer and free up its resources
	 * @throws IOException
	 */
	public void close() throws IOException;
}
