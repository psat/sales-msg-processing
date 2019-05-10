package com.psat.calculators;

import com.psat.sales.SaleMessage;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.psat.util.SalesTestHelper.createSaleMessage;
import static org.assertj.core.api.Assertions.assertThat;

public class SalesCalculatorTest {

  private SalesCalculator testee;

  @Before
  public void setUp() {
    testee = new SalesCalculator();
  }

  @Test
  public void givenEmptyListOfMessages_thenNoAggregatedResultsIsReturned() {
    List<SaleMessage> calculate = testee.calculate(new ArrayList<>());
    assertThat(calculate).isEmpty();
  }

  @Test
  public void givenAListOfMessages_withOneEntryForOneProduct_thenAggregatedResultsReturned() {
    SaleMessage mars = createSaleMessage("mars", 10);
    ArrayList<SaleMessage> sales = new ArrayList<>();
    sales.add(mars);

    assertThat(testee.calculate(sales)).isEqualTo(sales);
  }

  @Test
  public void givenAListOfMessages_withTwoEntriesForOneProduct_thenAggregatedResultsReturned() {
    List<SaleMessage> sales = new ArrayList<>();
    sales.add(createSaleMessage("mars", 10));
    sales.add(createSaleMessage("mars", 20));

    List<SaleMessage> expected = new ArrayList<>();
    expected.add(createSaleMessage("mars", 30, 2));

    assertThat(testee.calculate(sales)).isEqualTo(expected);
  }

  @Test
  public void givenAListOfMessages_withSeveralEntriesForTwoProducts_thenAggregatedResultsReturned() {
    List<SaleMessage> sales = new ArrayList<>();
    sales.add(createSaleMessage("mars", 10));
    sales.add(createSaleMessage("mars", 20));
    sales.add(createSaleMessage("twix", 20));

    List<SaleMessage> expected = new ArrayList<>();
    expected.add(createSaleMessage("mars", 30, 2));
    expected.add(createSaleMessage("twix", 20, 1));

    assertThat(testee.calculate(sales)).isEqualTo(expected);
  }

  @Test
  public void givenAListOfMessages_withSeveralEntriesForSeveralProducts_thenAggregatedResultsReturned() {
    List<SaleMessage> sales = new ArrayList<>();
    sales.add(createSaleMessage("mars", 10));
    sales.add(createSaleMessage("mars", 20));
    sales.add(createSaleMessage("twix", 20));
    sales.add(createSaleMessage("lion", 5));
    sales.add(createSaleMessage("lion", 2));

    List<SaleMessage> expected = new ArrayList<>();
    expected.add(createSaleMessage("lion", 7, 2));
    expected.add(createSaleMessage("mars", 30, 2));
    expected.add(createSaleMessage("twix", 20, 1));

    assertThat(testee.calculate(sales)).isEqualTo(expected);
  }
}