package com.psat.calculators;

import com.psat.sales.SaleMessage;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.psat.sales.SaleMessage.saleMessageIdentity;
import static com.psat.util.SalesTestHelper.createSaleMessage;
import static org.assertj.core.api.Assertions.assertThat;

public class TotalSalesCalculatorTest {

  private TotalSalesCalculator testee;

  @Before
  public void setUp() {
    testee = new TotalSalesCalculator();
  }

  @Test
  public void givenEmptyListOfMessages_thenSaleMessageIdentityIsReturned() {
    assertThat(testee.calculate(new ArrayList<>())).get().isEqualTo(saleMessageIdentity(""));
  }

  @Test
  public void givenAListOfMessages_withOneEntry_thenTotalReturned() {
    SaleMessage mars = createSaleMessage("mars", 10, 1);
    List<SaleMessage> sales = new ArrayList<>();
    sales.add(mars);

    calculateAndAssert(sales, 10, 1);
  }

  @Test
  public void givenAListOfMessages_withTwoEntries_thenTotalReturned() {
    List<SaleMessage> sales = new ArrayList<>();
    sales.add(createSaleMessage("mars", 20, 2));
    sales.add(createSaleMessage("twix", 20, 1));

    calculateAndAssert(sales, 40, 3);
  }

  @Test
  public void givenAListOfMessages_withSeveralEntries_thenAggregatedResultsReturned() {
    List<SaleMessage> sales = new ArrayList<>();
    sales.add(createSaleMessage("mars", 10, 1));
    sales.add(createSaleMessage("raiders", 4, 1));
    sales.add(createSaleMessage("twix", 20, 3));
    sales.add(createSaleMessage("lion", 2, 1));

    calculateAndAssert(sales, 36, 6);
  }

  private void calculateAndAssert(List<SaleMessage> sales, int expectedTotalValue, int expectedTotalCount) {
    Optional<SaleMessage> expected = Optional.of(createSaleMessage(-1, "", expectedTotalValue, expectedTotalCount));
    assertThat(testee.calculate(sales)).isEqualTo(expected);
  }
}