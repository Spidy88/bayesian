package com.nickferraro.bayesian.report.calc.core;

import java.security.InvalidParameterException;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.nickferraro.bayesian.IBayesianSystem;
import com.nickferraro.bayesian.IClassification;
import com.nickferraro.bayesian.IDataRow;
import com.nickferraro.bayesian.report.calc.IAccuracyCalculator;

/**
 * A thread-safe implementation of IAccuracyCalculator. This class will classify data rows
 * and return the accuracy rate calculated. Multiple sets of data rows can be classified and aggregated
 * into a single accuracy calculation. This implementation does not use the IDataRow id, nor does
 * it verify that duplicate ids are only counted once.
 * @author Nick Ferraro
 * 
 * @param <T> The category data type the bayesian system will be using
 */
public class AccuracyCalculator<T> implements IAccuracyCalculator<T> {
	protected final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
	protected final Lock readLock = readWriteLock.readLock();
	protected final Lock writeLock = readWriteLock.writeLock();
	protected IBayesianSystem<T> bayesianSystem;
	protected int total = 0;
	protected int correct = 0;
	
	/**
	 * Constructor for AccuracyCalculator. Requires a non-null IBayesianSystem parameter.
	 * @param bayesianSystem The IBayesianSystem to use for classification. Must not be NULL.
	 * @throws InvalidParameterException Thrown when IBayesianSystem is NULL.
	 */
	public AccuracyCalculator(IBayesianSystem<T> bayesianSystem) throws InvalidParameterException {
		// DRY: Set the bayesian system and reset counts
		setBayesianSystem(bayesianSystem);
		resetCounts();
	}
	
	/**
	 * Set the IBayesianSystem used for classification.
	 * @param bayesianSystem The IBayesianSystem to use for classification. Must not be NULL.
	 * @throws InvalidParameterException Thrown when IBayesianSystem is NULL.
	 */
	public void setBayesianSystem(IBayesianSystem<T> bayesianSystem) throws InvalidParameterException {
		// Validate the bayesianSystem parameter
		if( bayesianSystem == null ) {
			throw new InvalidParameterException("AccuracyCalculator cannot set a NULL IBayesianSystem");
		}
		
		// Lock
		writeLock.lock();
		
		try {
			this.bayesianSystem = bayesianSystem;
		} finally {
			// Unlock
			writeLock.unlock();
		}
	}
	
	@Override
	public double calculateAccuracy(List<IDataRow<T>> dataRows) {
		// DRY: Call calculateAccuracy with cleanSlate = TRUE
		return calculateAccuracy(dataRows, true);
	}

	@Override
	public double calculateAccuracy(List<IDataRow<T>> dataRows, boolean cleanSlate) {
		// Lock
		writeLock.lock();
		
		try {
			// Call thread-unsafe private method
			return _calculateAccuracy(dataRows, cleanSlate);
		} finally {
			// Unlock
			writeLock.unlock();
		}
	}
	
	@Override
	public double getAccuracy() {
		// Lock
		readLock.lock();
		
		try {
			// Call thead-unsafe private method
			return _getAccuracy();
		} finally {
			// Unlock
			readLock.unlock();
		}
	}

	@Override
	public int getCorrectCount() {
		// Lock
		readLock.lock();
		
		try {
			return correct;
		} finally {
			// Unlock
			readLock.unlock();
		}
	}

	@Override
	public int getIncorrectCount() {
		// Lock
		readLock.lock();
		
		try {
			// Calculate and return incorrect
			return total - correct;
		} finally {
			// Unlock
			readLock.unlock();
		}
	}

	@Override
	public int getTotalCount() {
		// Lock
		readLock.lock();
		
		try {
			return total;
		} finally {
			// Unlock
			readLock.unlock();
		}
	}
	
	/**
	 * Reset the current calculations and counts
	 */
	public void resetCounts() {
		// Lock
		writeLock.lock();
		
		try {
			// Call thread-unsafe private method
			_resetCounts();
		} finally {
			writeLock.unlock();
		}
	}
	
	/**
	 * Private thread-unsafe method for calculating the accuracy of a bayesian system.
	 * @param dataRows The data rows to classify and use in accuracy calculations
	 * @param cleanSlate Whether or not to aggregate calculations or start from a clean slate
	 * @return The current accuracy of the bayesian system
	 */
	protected double _calculateAccuracy(List<IDataRow<T>> dataRows, boolean cleanSlate) {
		// Reset our counts if cleanSlate is TRUE
		if( cleanSlate ) {
			_resetCounts();
		}
		
		// Return 0% accuracy for null dataRows
		if( dataRows == null ) {
			return _getAccuracy();
		}
		
		// Iterate over data rows
		for(IDataRow<T> dataRow : dataRows) {
			// If a row is null, skip it
			if( dataRow == null ) {
				continue;
			}
			
			// Get the first classification and check if it is correct. NULL classifications are ignored.
			List<IClassification<T>> classifications = bayesianSystem.classifyRow(dataRow);
			if( classifications != null && classifications.size() > 0 ) {
				IClassification<T> classification = classifications.get(0);
				// Increase the total rows calculated
				++total;
				
				// If the classification category matches the data row category, increase the correct count
				if( classification != null && classification.getCategory().equals(dataRow.getCategory()) ) {
					++correct;
				}
			}
		}
		
		// Return the current accuracy using the thread-unsafe private method
		return _getAccuracy();
	}
	
	/**
	 * Private thread-unsafe method for calculating the accuracy
	 * @return The last calculated accuracy
	 */
	protected double _getAccuracy() {
		return total == 0 ? 0.0 : ((double)correct / (double)total);
	}
	
	/**
	 * Private thread-unsafe method for reseting the calculations and counts
	 */
	protected void _resetCounts() {
		total = 0;
		correct = 0;
	}
}
