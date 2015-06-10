package com.nickferraro.bayesian.report.calc;

import java.util.List;

import com.nickferraro.bayesian.IDataRow;

/**
 * The IAccuracyCalculator interface is a simple contract for calculating the accuracy of
 * a bayesian system in classifying a list of data rows.
 * @author Nick Ferraro
 *
 * @param <T> The category data type the bayesian system will be using
 */
public interface IAccuracyCalculator<T> {
	/**
	 * Classify the list of data rows and calculate the accuracy of classifying the row category.
	 * @param dataRows A list of data rows to classify and use in a clean set of calculations.
	 * @return The accuracy of classifying this data set
	 */
	public double calculateAccuracy(List<IDataRow<T>> dataRows);
	
	/**
	 * Classify the list of data rows and calculate the accuracy of classifying the row category.
	 * @param dataRows A list of data rows to classify and use in a clean or aggregated set of calculations.
	 * @param cleanSlate Whether or not to aggregate previous calculations with this new data set or start from a clean slate. Defaults to TRUE.
	 * @return The accuracy of classifying this data set
	 */
	public double calculateAccuracy(List<IDataRow<T>> dataRows, boolean cleanSlate);
	
	/**
	 * Get the last accuracy calculated.
	 * @return The last accuracy calculated.
	 */
	public double getAccuracy();
	
	/**
	 * Get the number of correct classifications from the last calculation.
	 * @return The number of correct classifications
	 */
	public int getCorrectCount();
	
	/**
	 * Get the number of incorrect classifications from the last calculation.
	 * @return The number of incorrect classifications
	 */
	public int getIncorrectCount();
	
	/**
	 * Get the number of total classifications calculated
	 * @return The number of total classifications
	 */
	public int getTotalCount();
}
