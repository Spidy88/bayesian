package com.nickferraro.bayesian.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.security.InvalidParameterException;
import java.util.List;
import java.util.Set;

import com.nickferraro.bayesian.model.IBayesianModel;
import com.nickferraro.bayesian.model.hashed.ILink;

public class BayesianWriter {
	private final BufferedWriter writer;
	
	public BayesianWriter(Writer writer) throws InvalidParameterException {
		if( writer == null ) {
			throw new InvalidParameterException();
		}
		
		this.writer = new BufferedWriter(writer);
	}
	
	public synchronized void writeModel(IBayesianModel<?> model) throws IOException {
		writer.write(model.getTotalRows());
		writer.newLine();
		
		_writeModelCategoriesHelper(model);
		
		Set<String> uniqueWords = model.getUniqueWords();
		writer.write(uniqueWords.size());
		writer.newLine();
		for(String word : uniqueWords) {
			writer.write(validateOutput(word));
			writer.newLine();
			writer.write(model.countRowsWithWord(word));
			writer.newLine();
		}
		
		_writeModelLinksHelper(model);
	}
	
	public synchronized void close() throws IOException {
		writer.close();
	}
	
	private <T> void _writeModelCategoriesHelper(IBayesianModel<T> model) throws IOException {
		Set<T> uniqueCategories = model.getUniqueCategories();
		writer.write(uniqueCategories.size());
		writer.newLine();
		for(T category : uniqueCategories) {
			writer.write(validateOutput(category.toString()));
			writer.newLine();
			writer.write(model.countRowsWithCategory(category));
			writer.newLine();
		}
	}
	
	private <T> void _writeModelLinksHelper(IBayesianModel<T> model) throws IOException {
		List<ILink<T>> links = model.getLinks();
		writer.write(links.size());
		writer.newLine();
		for(ILink<T> link : links) {
			writer.write(validateOutput(link.getCategory().toString()));
			writer.newLine();
			writer.write(validateOutput(link.getWord()));
			writer.newLine();
			writer.write(link.getWeight());
			writer.newLine();
		}
	}
	
	private String validateOutput(String output) throws IOException {
		if( output.contains("\n") ) {
			throw new IOException("Cannot write a model with string categories or words that contain a newline");
		}
		
		return output;
	}
}
