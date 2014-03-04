package com.nickferraro.bayesian.report.calc.core;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.nickferraro.bayesian.IBayesianSystem;
import com.nickferraro.bayesian.IClassification;
import com.nickferraro.bayesian.IDataRow;

public class AccuracyCalculatorTest {
	private static final String CATEGORY_1 = "cat1";
	private static final String CATEGORY_2 = "cat2";
	private static final String CATEGORY_3 = "cat3";
	
	private AccuracyCalculator<String> calculator;
	private IBayesianSystem<String> mockSystem;
	private IDataRow<String> mockRow1;
	private IDataRow<String> mockRow2;
	private IDataRow<String> mockRow3;
	
	@SuppressWarnings("unchecked")
	@Before
	public void setup() {
		mockSystem = mock(IBayesianSystem.class);
		calculator = new AccuracyCalculator<String>(mockSystem);
	}
	
	@Test(expected = InvalidParameterException.class)
	public void testConstructor_NullSystem() {
		new AccuracyCalculator<String>(null);
	}
	
	@Test
	public void testInitialState() {
		assertEquals(calculator.getAccuracy(), 0.0, 0.0001);
		assertThat(calculator.getCorrectCount(), is(0));
		assertThat(calculator.getIncorrectCount(), is(0));
		assertThat(calculator.getTotalCount(), is(0));
	}
	
	@Test
	public void testCalculateAccuracy() {
		List<IDataRow<String>> mockDataRows = createMockDataRows();
		
		List<IClassification<String>> classificationList1 = createClassificationList(CATEGORY_1);
		List<IClassification<String>> classificationList2 = createClassificationList(CATEGORY_1);		
		List<IClassification<String>> classificationList3 = createClassificationList(CATEGORY_3);
		
		when(mockSystem.classifyRow(mockRow1)).thenReturn(classificationList1);
		when(mockSystem.classifyRow(mockRow2)).thenReturn(classificationList2);
		when(mockSystem.classifyRow(mockRow3)).thenReturn(classificationList3);
		
		double accuracy = calculator.calculateAccuracy(mockDataRows);
		assertEquals(2.0/3.0, accuracy, 0.0001);
		assertEquals(2.0/3.0, calculator.getAccuracy(), 0.0001);
		assertThat(calculator.getCorrectCount(), is(2));
		assertThat(calculator.getIncorrectCount(), is(1));
		assertThat(calculator.getTotalCount(), is(3));
	}
	
	@Test
	public void testCalculateAccuracy_NullList() {
		double accuracy = calculator.calculateAccuracy(null);
		assertEquals(0.0, accuracy, 0.0001);
		assertEquals(0.0, calculator.getAccuracy(), 0.0001);
		assertThat(calculator.getCorrectCount(), is(0));
		assertThat(calculator.getIncorrectCount(), is(0));
		assertThat(calculator.getTotalCount(), is(0));
	}
	
	@Test
	public void testCalculateAccuracy_EmptyList() {
		List<IDataRow<String>> emptyList = Collections.emptyList();
		double accuracy = calculator.calculateAccuracy(emptyList);
		assertEquals(0.0, accuracy, 0.0001);
		assertEquals(0.0, calculator.getAccuracy(), 0.0001);
		assertThat(calculator.getCorrectCount(), is(0));
		assertThat(calculator.getIncorrectCount(), is(0));
		assertThat(calculator.getTotalCount(), is(0));
	}
	
	@Test
	public void testCalculateAccuracy_ListWithNull() {
		List<IDataRow<String>> mockDataRows = createMockDataRows();
		mockDataRows.add(1, null);
		
		List<IClassification<String>> classificationList1 = createClassificationList(CATEGORY_1);
		List<IClassification<String>> classificationList2 = createClassificationList(CATEGORY_1);		
		List<IClassification<String>> classificationList3 = createClassificationList(CATEGORY_3);
		
		when(mockSystem.classifyRow(mockRow1)).thenReturn(classificationList1);
		when(mockSystem.classifyRow(mockRow2)).thenReturn(classificationList2);
		when(mockSystem.classifyRow(mockRow3)).thenReturn(classificationList3);
		
		double accuracy = calculator.calculateAccuracy(mockDataRows);
		assertEquals(2.0/3.0, accuracy, 0.0001);
		assertEquals(2.0/3.0, calculator.getAccuracy(), 0.0001);
		assertThat(calculator.getCorrectCount(), is(2));
		assertThat(calculator.getIncorrectCount(), is(1));
		assertThat(calculator.getTotalCount(), is(3));
	}
	
	@Test
	public void testResetCounts() {
		List<IDataRow<String>> mockDataRows = createMockDataRows();
		
		List<IClassification<String>> classificationList1 = createClassificationList(CATEGORY_1);
		List<IClassification<String>> classificationList2 = createClassificationList(CATEGORY_1);		
		List<IClassification<String>> classificationList3 = createClassificationList(CATEGORY_3);
		
		when(mockSystem.classifyRow(mockRow1)).thenReturn(classificationList1);
		when(mockSystem.classifyRow(mockRow2)).thenReturn(classificationList2);
		when(mockSystem.classifyRow(mockRow3)).thenReturn(classificationList3);
		
		double accuracy = calculator.calculateAccuracy(mockDataRows);
		assertEquals(2.0/3.0, accuracy, 0.0001);
		assertThat(calculator.getAccuracy(), is(2.0/3.0));
		assertThat(calculator.getCorrectCount(), is(2));
		assertThat(calculator.getIncorrectCount(), is(1));
		assertThat(calculator.getTotalCount(), is(3));
		
		calculator.resetCounts();
		
		assertEquals(0.0, calculator.getAccuracy(), 0.0001);
		assertThat(calculator.getCorrectCount(), is(0));
		assertThat(calculator.getIncorrectCount(), is(0));
		assertThat(calculator.getTotalCount(), is(0));
	}
	
	@Test
	public void testCalculateAccuracy_AggregateCounts() {
		List<IDataRow<String>> mockDataRows = createMockDataRows();
		
		List<IClassification<String>> classificationList1 = createClassificationList(CATEGORY_1);
		List<IClassification<String>> classificationList2 = createClassificationList(CATEGORY_1);		
		List<IClassification<String>> classificationList3 = createClassificationList(CATEGORY_3);
		
		when(mockSystem.classifyRow(mockRow1)).thenReturn(classificationList1);
		when(mockSystem.classifyRow(mockRow2)).thenReturn(classificationList2);
		when(mockSystem.classifyRow(mockRow3)).thenReturn(classificationList3);
		
		calculator.calculateAccuracy(mockDataRows);
		assumeThat(calculator.getCorrectCount(), is(2));
		assumeThat(calculator.getIncorrectCount(), is(1));
		assumeThat(calculator.getTotalCount(), is(3));
		
		double accuracy = calculator.calculateAccuracy(mockDataRows, false);
		assertEquals(2.0/3.0, accuracy, 0.0001);
		assertEquals(2.0/3.0, calculator.getAccuracy(), 0.0001);
		assertThat(calculator.getCorrectCount(), is(4));
		assertThat(calculator.getIncorrectCount(), is(2));
		assertThat(calculator.getTotalCount(), is(6));
	}
	
	@Test
	public void testCalculateAccuracy_AggregateCounts_Null() {
		List<IDataRow<String>> mockDataRows = createMockDataRows();
		
		List<IClassification<String>> classificationList1 = createClassificationList(CATEGORY_1);
		List<IClassification<String>> classificationList2 = createClassificationList(CATEGORY_1);		
		List<IClassification<String>> classificationList3 = createClassificationList(CATEGORY_3);
		
		when(mockSystem.classifyRow(mockRow1)).thenReturn(classificationList1);
		when(mockSystem.classifyRow(mockRow2)).thenReturn(classificationList2);
		when(mockSystem.classifyRow(mockRow3)).thenReturn(classificationList3);
		
		calculator.calculateAccuracy(mockDataRows);
		assumeThat(calculator.getCorrectCount(), is(2));
		assumeThat(calculator.getIncorrectCount(), is(1));
		assumeThat(calculator.getTotalCount(), is(3));
		
		double accuracy = calculator.calculateAccuracy(null, false);
		assertEquals(2.0/3.0, accuracy, 0.0001);
		assertEquals(2.0/3.0, calculator.getAccuracy(), 0.0001);
		assertThat(calculator.getCorrectCount(), is(2));
		assertThat(calculator.getIncorrectCount(), is(1));
		assertThat(calculator.getTotalCount(), is(3));
	}
	
	@Test
	public void testCalculateAccuracy_AggregateCounts_Empty() {
		List<IDataRow<String>> mockDataRows = createMockDataRows();
		
		List<IClassification<String>> classificationList1 = createClassificationList(CATEGORY_1);
		List<IClassification<String>> classificationList2 = createClassificationList(CATEGORY_1);		
		List<IClassification<String>> classificationList3 = createClassificationList(CATEGORY_3);
		
		when(mockSystem.classifyRow(mockRow1)).thenReturn(classificationList1);
		when(mockSystem.classifyRow(mockRow2)).thenReturn(classificationList2);
		when(mockSystem.classifyRow(mockRow3)).thenReturn(classificationList3);
		
		calculator.calculateAccuracy(mockDataRows);
		assumeThat(calculator.getCorrectCount(), is(2));
		assumeThat(calculator.getIncorrectCount(), is(1));
		assumeThat(calculator.getTotalCount(), is(3));
		
		List<IDataRow<String>> emptyDataRows = Collections.emptyList();
		double accuracy = calculator.calculateAccuracy(emptyDataRows, false);
		assertEquals(2.0/3.0, accuracy, 0.0001);
		assertEquals(2.0/3.0, calculator.getAccuracy(), 0.0001);
		assertThat(calculator.getCorrectCount(), is(2));
		assertThat(calculator.getIncorrectCount(), is(1));
		assertThat(calculator.getTotalCount(), is(3));
	}
	
	@SuppressWarnings("unchecked")
	private List<IDataRow<String>> createMockDataRows() {
		List<IDataRow<String>> mockDataRows = new ArrayList<IDataRow<String>>();
		mockRow1 = mock(IDataRow.class);
		mockRow2 = mock(IDataRow.class);
		mockRow3 = mock(IDataRow.class);
		
		when(mockRow1.getCategory()).thenReturn(CATEGORY_1);
		when(mockRow2.getCategory()).thenReturn(CATEGORY_2);
		when(mockRow3.getCategory()).thenReturn(CATEGORY_3);
		
		mockDataRows.add(mockRow1);
		mockDataRows.add(mockRow2);
		mockDataRows.add(mockRow3);
		
		return mockDataRows;
	}
	
	private List<IClassification<String>> createClassificationList(String category) {
		List<IClassification<String>> classifications = new ArrayList<IClassification<String>>();
		
		@SuppressWarnings("unchecked")
		IClassification<String> mockClassification1 = mock(IClassification.class);
		classifications.add(mockClassification1);
		when(mockClassification1.getCategory()).thenReturn(category);
		
		return classifications;
	}
}
