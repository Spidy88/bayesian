package com.nickferraro.bayesian.core;

import java.util.Comparator;

import com.nickferraro.bayesian.IClassification;

/**
 * A comparison class for the IClassification interface. Compares classifications based on probability alone.
 * @author Nick Ferraro
 */
public class ClassificationComparator implements Comparator<IClassification<?>> {
	@Override
	public int compare(IClassification<?> o1, IClassification<?> o2) {
		// Check if objects are the same or if either are null
		if(o1 == o2) {
			return 0;
		} else if(o1 == null) {
			return -1;
		} else if(o2 == null) {
			return 1;
		}
		
		// Two valid objects, compare probabilities
		return Double.compare(o1.getProbability(), o2.getProbability());
	}
}
