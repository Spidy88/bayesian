package com.nickferraro.bayesian.io.json;

import org.json.JSONObject;

import com.nickferraro.bayesian.io.IBayesianWriter;

/**
 * A writer interface for writing a model in a JSON format
 * @author Nick Ferraro
 *
 */
public interface IBayesianJsonWriter extends IBayesianWriter {
	/**
	 * Get the json object created when writing a model.
	 * @return The JSONObject created or NULL if the model has yet to be written or failed
	 */
	public JSONObject getJsonObject();
}
