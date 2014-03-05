package com.nickferraro.bayesian.report.calc.core;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.List;

import com.nickferraro.bayesian.IBayesianSystem;
import com.nickferraro.bayesian.IClassification;
import com.nickferraro.bayesian.IDataRow;
import com.nickferraro.bayesian.report.calc.IConfusionMatrix;

/**
 * A class for calculating the accuracy of a bayesian system and creating a confusion matrix.
 * This class is thread-safe.
 * @author Nick Ferraro
 *
 * @param <T> The category data type used by the bayesian system
 */
public class ConfusionMatrix<T> extends AccuracyCalculator<T> implements IConfusionMatrix<T> {
	private final HashMap<T,HashMap<T, Integer>> matrix = new HashMap<T, HashMap<T, Integer>>();
	
	/**
	 * Constructor of a ConfusionMatrix. Requires a non-null bayesian system.
	 * @param bayesianSystem The bayesian system to use for calculating a confusion matrix
	 * @throws InvalidParameterException Thrown when the bayesian system is null
	 */
	public ConfusionMatrix(IBayesianSystem<T> bayesianSystem) throws InvalidParameterException {
		super(bayesianSystem);
	}
	
	@Override
	public int getCellCount(T actualCategory, T classifiedCategory) {
		// Lock
		readLock.lock();
		
		try {
			// Call the thread-unsafe private method
			return _getCellCount(actualCategory, classifiedCategory);
		} finally {
			// Unlock
			readLock.unlock();
		}
	}
	
	/**
	 * Get the current count for a matrix cell. Not thread-safe.
	 * @param actualCategory The actual category (row)
	 * @param classifiedCategory The classified category (column)
	 * @return The current count of the cell
	 */
	protected int _getCellCount(T actualCategory, T classifiedCategory) {
		// Get the matrix row
		HashMap<T, Integer> row = matrix.get(actualCategory);
		if( row == null ) {
			// If the row doesn't exist, the count is 0
			return 0;
		}
		
		// Get the cell count, if it doesn't exist the count is 0
		Integer cellCount = row.get(classifiedCategory);
		return cellCount == null ? 0 : cellCount;
	}

	@Override
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
				
				// Add classification result to matrix
				_updateMatrix(dataRow.getCategory(), classification.getCategory());
			}
		}
		
		// Return the current accuracy using the thread-unsafe private method
		return _getAccuracy();
	}
	
	/**
	 * Increase a cell's count on the matrix
	 * @param actualCategory The actual category (row)
	 * @param classifiedCategory The classified category (column)
	 */
	protected void _updateMatrix(T actualCategory, T classifiedCategory) {
		// Get the row for the actual category
		HashMap<T, Integer> actualRow = matrix.get(actualCategory);
		if( actualRow == null ) {
			// If the row doesn't exist yet, create it
			actualRow = new HashMap<T, Integer>();
			matrix.put(actualCategory, actualRow);
		}
		
		// Get the row cell for the classified category
		Integer classifiedCell = actualRow.get(classifiedCategory);
		if( classifiedCell == null ) {
			// If it doesn't exist, create it (set it to 0)
			classifiedCell = 0;
		}
		
		// Increase the count for this cell
		actualRow.put(classifiedCategory, classifiedCell + 1);
	}
	
	@Override
	protected void _resetCounts() {
		super._resetCounts();
		// During construction, matrix is NULL
		if( matrix != null ) {
			matrix.clear();
		}
	}
}
