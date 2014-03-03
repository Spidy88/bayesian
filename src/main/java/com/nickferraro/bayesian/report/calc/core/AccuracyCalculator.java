package com.nickferraro.bayesian.report.calc.core;

import java.security.InvalidParameterException;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.nickferraro.bayesian.IBayesianSystem;
import com.nickferraro.bayesian.IClassification;
import com.nickferraro.bayesian.IDataRow;
import com.nickferraro.bayesian.report.calc.IAccuracyCalculator;

public class AccuracyCalculator implements IAccuracyCalculator {
	protected ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
	protected Lock readLock = readWriteLock.readLock();
	protected Lock writeLock = readWriteLock.writeLock();
	private IBayesianSystem<?> bayesianSystem;
	private int total = 0;
	private int correct = 0;
	
	public AccuracyCalculator(IBayesianSystem<?> bayesianSystem) throws InvalidParameterException {
		setBayesianSystem(bayesianSystem);
		resetCounts();
	}
	
	public void setBayesianSystem(IBayesianSystem<?> bayesianSystem) throws InvalidParameterException {
		if( bayesianSystem == null ) {
			throw new InvalidParameterException("AccuracyCalculator cannot set a NULL IBayesianSystem");
		}
		
		writeLock.lock();
		
		try {
			this.bayesianSystem = bayesianSystem;
		} finally {
			writeLock.unlock();
		}
	}
	
	@Override
	public double calculateAccuracy(List<IDataRow<?>> dataRows) {
		return calculateAccuracy(dataRows, true);
	}

	@Override
	public double calculateAccuracy(List<IDataRow<?>> dataRows, boolean cleanSlate) {
		writeLock.lock();
		
		try {
			return _calculateAccuracy(dataRows, cleanSlate);
		} finally {
			writeLock.unlock();
		}
	}
	
	public double getAccuracy() {
		readLock.lock();
		
		try {
			return _getAccuracy();
		} finally {
			readLock.unlock();
		}
	}

	@Override
	public int getCorrectCount() {
		readLock.lock();
		
		try {
			return correct;
		} finally {
			readLock.unlock();
		}
	}

	@Override
	public int getIncorrectCount() {
		readLock.lock();
		
		try {
			return total - correct;
		} finally {
			readLock.unlock();
		}
	}

	@Override
	public int getTotalCount() {
		readLock.unlock();
		
		try {
			return total;
		} finally {
			readLock.unlock();
		}
	}
	
	public void resetCounts() {
		writeLock.lock();
		
		try {
			_resetCounts();
		} finally {
			writeLock.unlock();
		}
	}
	
	private double _calculateAccuracy(List<IDataRow<?>> dataRows, boolean cleanSlate) {
		if( cleanSlate ) {
			_resetCounts();
		}
		
		if( dataRows == null ) {
			return 0;
		}
		
		for(IDataRow<?> dataRow : dataRows) {
			if( dataRow == null ) {
				continue;
			}
			
			IClassification<?> classification = helperGetClassification(bayesianSystem.classifyRow(dataRow));
			if( classification != null ) {
				++total;
				
				if( classification.getCategory().equals(dataRow.getCategory()) ) {
					++correct;
				}
			}
		}
		
		return _getAccuracy();
	}
	
	private double _getAccuracy() {
		return (double)correct / (double)total;
	}
	
	private void _resetCounts() {
		total = 0;
		correct = 0;
	}
	
	private <T> IClassification<T> helperGetClassification(List<IClassification<T>> l) {
		return l == null || l.size() < 1 ? null : l.get(0);
	}
}
