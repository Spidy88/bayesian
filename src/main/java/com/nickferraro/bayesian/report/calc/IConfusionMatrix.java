package com.nickferraro.bayesian.report.calc;

/**
 * An interface for calculating a confusion matrix.
 * @author Nick Ferraro
 *
 * @param <T> The category data type of the bayesian model
 */
public interface IConfusionMatrix<T> extends IAccuracyCalculator<T> {
	/**
	 * Get the count value of a matrix cell.
	 * @param actualCategory The actual category (row)
	 * @param classifiedCategory The classified category (column)
	 * @return The count value of the cell. Will never be less than 0.
	 */
	public int getCellCount(T actualCategory, T classifiedCategory);
}
