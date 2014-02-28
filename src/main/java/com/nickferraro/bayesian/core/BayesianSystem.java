package com.nickferraro.bayesian.core;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.nickferraro.bayesian.IBayesianSystem;
import com.nickferraro.bayesian.IClassification;
import com.nickferraro.bayesian.IDataRow;
import com.nickferraro.bayesian.ISentenceInput;
import com.nickferraro.bayesian.model.IBayesianModel;
import com.nickferraro.bayesian.model.hashed.BayesianModel;

public class BayesianSystem<T> implements IBayesianSystem<T> {
	protected ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
	protected Lock readLock = readWriteLock.readLock();
	protected Lock writeLock = readWriteLock.writeLock();
	private IBayesianModel<T> bayesianModel = new BayesianModel<T>();
	
	public BayesianSystem() {}
	public BayesianSystem(IBayesianModel<T> bayesianModel) throws InvalidParameterException {
		setBayesianModel(bayesianModel);
	}
	
	public final void setBayesianModel(IBayesianModel<T> bayesianModel) throws InvalidParameterException {
		if( bayesianModel == null ) {
			throw new InvalidParameterException("Cannot set a NULL bayesian model");
		}
		
		writeLock.lock();
		
		this.bayesianModel = bayesianModel;
		
		writeLock.unlock();
		
	}
	
	@Override
	public final List<IClassification<T>> classifyRow(ISentenceInput sentenceInput) throws InvalidParameterException {
		return classifyRow(sentenceInput, -1);
	}
	
	@Override
	public final List<IClassification<T>> classifyRow(ISentenceInput sentenceInput, int maxResults) throws InvalidParameterException {
		// Validate sentence input
		if( sentenceInput == null ) {
			throw new InvalidParameterException();
		}
		
		// Lock
		readLock.lock();
		
		try {
			// Classify row with private thread unsafe method
			return _classifyRow(sentenceInput.getUniqueSentenceWords(), maxResults);
		} finally {
			// Unlock
			readLock.unlock();
		}
	}
	
	protected List<IClassification<T>> _classifyRow(Set<String> uniqueWords, int maxResults) {
		List<IClassification<T>> classifications = new ArrayList<IClassification<T>>();
		
		// Validate the unique categories set
		Set<T> uniqueCategories = bayesianModel.getUniqueCategories();
		if( uniqueCategories == null ) {
			return classifications;
		}
		
		// Makes sure uniqueWords is not NULL
		if( uniqueWords == null ) {
			uniqueWords = Collections.emptySet();
		}
		
		// Remove a NULL entry if it exists and validate if the model has any categories
		uniqueCategories.remove(null);
		if( uniqueCategories.size() == 0 ) {
			return classifications;
		}
		
		// Get total training rows. 
		// If the model has 0 training rows, evenly distribute probability over categories.
		int totalRows = bayesianModel.getTotalRows();
		if( totalRows == 0 ) {
			double totalUniqueCategories = uniqueCategories.size();
			for(T category : uniqueCategories) {
				classifications.add(new Classification<T>(category, 1.0d / totalUniqueCategories));
			}
			
			return classifications;
		}
		
		// Calculate the probability of each category
		HashMap<T, Double> categoryProbabilityMap = new HashMap<T, Double>();
		double sumOfCategoryProbabilities = 0.0d;
		for(T category : uniqueCategories) {
			int totalCategoryRows = bayesianModel.countRowsWithCategory(category);
			double probabilityOfCategory = (double)totalCategoryRows / (double)totalRows;
			
			// Calculate the probability of each word for the current category
			boolean hasOneWord = false;
			double probabilityOfWordsGivenCategory = 1.0d;
			for(String word : uniqueWords) {
				// Check that the current word is accepted by the system
				if( isWordAllowed(word) ) {
					// Calculate the probability of the current word for the current category
					int totalCategoryAndWordRows = bayesianModel.countRowsWithCategoryWithWord(category, word);
					double probabilityOfWordGivenCategory = (totalCategoryAndWordRows == 0 ? (1.0d / totalRows) : ((double)totalCategoryAndWordRows / (double)totalCategoryRows));
					
					// Update the probability of all words
					probabilityOfWordsGivenCategory *= probabilityOfWordGivenCategory;
					
					// Update flag for finding at least one word
					hasOneWord = true;
				}
			}
			
			// If no acceptable words were found, set the probability to 0%
			if( !hasOneWord ) {
				probabilityOfWordsGivenCategory = 0.0d;
			}
			
			// Calculate the probability of the current category given the set of words
			double probabilityOfCategoryGivenWords = probabilityOfCategory * probabilityOfWordsGivenCategory;
			categoryProbabilityMap.put(category, probabilityOfCategoryGivenWords);
			
			// Update the normalization sum of all category probabilities
			sumOfCategoryProbabilities += probabilityOfCategoryGivenWords;
		}
		
		// If all category probabilities are 0%, use probability of category only (not considering words)
		if( sumOfCategoryProbabilities == 0 ) {
			// Create classifications from probabilityOfCategory
			for(T category : uniqueCategories) {
				int totalCategoryRows = bayesianModel.countRowsWithCategory(category);
				double probabilityOfCategory = (double)totalCategoryRows / (double)totalRows;
				
				classifications.add(new Classification<T>(category, probabilityOfCategory));
			}
		// Not all category probabilities are 0%, use mapped probability calculations
		} else {
			// Create classifications from probabilityOfCategoryGivenWords
			for(T category : uniqueCategories) {
				double categoryProbability = categoryProbabilityMap.get(category) / sumOfCategoryProbabilities;
				classifications.add(new Classification<T>(category, categoryProbability));
			}
		}
		
		// Sort from highest to lowest probability
		Collections.sort(classifications, Collections.reverseOrder(new ClassificationComparator()));
		
		// If maxResults is set, reduce list size
		if( maxResults > 0 ) {
			classifications.subList(0, maxResults + 1);
		}
		
		return classifications;
	}
	
	public boolean isWordAllowed(String word) {
		return true;
	}

	@Override
	public final void trainOnRow(IDataRow<T> dataRow) {
		if( dataRow != null ) {
			bayesianModel.addDataRow(dataRow);
		}
	}

	@Override
	public final void trainOnRows(List<IDataRow<T>> dataRows) {
		if( dataRows != null ) {
			bayesianModel.addDataRows(dataRows);
		}
	}
}
