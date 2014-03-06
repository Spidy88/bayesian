package com.nickferraro.bayesian.io.json;

import org.json.JSONObject;

import com.nickferraro.bayesian.io.IBayesianWriter;

public interface IBayesianJsonWriter extends IBayesianWriter {
	public JSONObject getJsonObject();
}
