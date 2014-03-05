package com.nickferraro.bayesian.report.calc.core;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.nickferraro.bayesian.IBayesianSystem;
import com.nickferraro.bayesian.IClassification;
import com.nickferraro.bayesian.IDataRow;

public class ConfusionMatrixTest extends AccuracyCalculatorTest {
	private ConfusionMatrix<String> matrix;
	
	@Before
	public void setup() {
		super.setup();
		matrix = new ConfusionMatrix<String>(mockSystem);
		calculator = matrix;
	}
	
	@Test
	public void testGetCellCount() {
		List<IDataRow<String>> mockDataRows = createMockDataRows();
		
		List<IClassification<String>> classificationList1 = createClassificationList(CATEGORY_1);
		List<IClassification<String>> classificationList2 = createClassificationList(CATEGORY_1);		
		List<IClassification<String>> classificationList3 = createClassificationList(CATEGORY_3);
		
		when(mockSystem.classifyRow(mockRow1)).thenReturn(classificationList1);
		when(mockSystem.classifyRow(mockRow2)).thenReturn(classificationList2);
		when(mockSystem.classifyRow(mockRow3)).thenReturn(classificationList3);
		
		double accuracy = calculator.calculateAccuracy(mockDataRows);
		assertEquals(2.0/3.0, accuracy, 0.0001);
		assertThat(matrix.getCellCount(CATEGORY_1, CATEGORY_1), is(1));
		assertThat(matrix.getCellCount(CATEGORY_1, CATEGORY_2), is(0));
		assertThat(matrix.getCellCount(CATEGORY_1, CATEGORY_3), is(0));
		assertThat(matrix.getCellCount(CATEGORY_2, CATEGORY_1), is(1));
		assertThat(matrix.getCellCount(CATEGORY_2, CATEGORY_2), is(0));
		assertThat(matrix.getCellCount(CATEGORY_2, CATEGORY_3), is(0));
		assertThat(matrix.getCellCount(CATEGORY_3, CATEGORY_1), is(0));
		assertThat(matrix.getCellCount(CATEGORY_3, CATEGORY_2), is(0));
		assertThat(matrix.getCellCount(CATEGORY_3, CATEGORY_3), is(1));
	}
}
