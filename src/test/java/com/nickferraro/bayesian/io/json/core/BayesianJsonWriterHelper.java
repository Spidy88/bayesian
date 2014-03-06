package com.nickferraro.bayesian.io.json.core;

import java.io.Writer;
import java.security.InvalidParameterException;

import org.json.JSONArray;

import com.nickferraro.bayesian.model.IBayesianModel;

public class BayesianJsonWriterHelper extends BayesianJsonWriter {
	public BayesianJsonWriterHelper(Writer writer) throws InvalidParameterException {
		super(writer);
	}
	
	@Override
	public <T> JSONArray writeCategoriesArray(IBayesianModel<T> model) {
		return super.writeCategoriesArray(model);
	}
	
	@Override
	public <T> JSONArray writeWordsArray(IBayesianModel<T> model) {
		return super.writeWordsArray(model);
	}
	
	@Override
	public <T> JSONArray writeLinksArray(IBayesianModel<T> model) {
		return super.writeLinksArray(model);
	}
}
