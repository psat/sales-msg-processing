package com.psat.reporting;

import com.psat.sales.SaleMessage;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.psat.util.SalesTestHelper.createSaleMessage;
import static org.assertj.core.api.Assertions.assertThat;

public class SalesReportTest {

  private static final String EXPECTED_INVALID_REPORT = "\n--------------\n" +
          "SALES REPORT\n" +
          "--------------\n\n" +
          "Invalid input!\n" +
          "\n--------------\n" +
          "--------------\n" +
          "Grand Total:\n" +
          "\t#Sales: 0\n" +
          "\t Total: 0\n" +
          "--------------";

  private SalesReport testee;

  @Before
  public void setUp() {
    testee = new SalesReport();
  }

  @Test
  public void givenEmptyListOfMessages_thenNoSalesReportIsGenerated() {
    assertThat(testee.generate(new ArrayList<>(), Optional.empty())).get().isEqualTo(EXPECTED_INVALID_REPORT);
  }

  @Test
  public void givenTotalSalesIsEmpty_thenNoSalesReportIsGenerated() {
    List<SaleMessage> aggregatedSales = new ArrayList<>();
    aggregatedSales.add(createSaleMessage("toblerone", 10, 1));
    assertThat(testee.generate(aggregatedSales, Optional.empty())).get().isEqualTo(EXPECTED_INVALID_REPORT);
  }

  @Test
  public void givenAggregatedAndTotals_forOneEntry_thenReportIsGenerated() {
    List<SaleMessage> aggregatedSales = new ArrayList<>();
    aggregatedSales.add(createSaleMessage("toblerone", 10, 2));

    Optional<SaleMessage> totalSaleMessage = Optional.of(createSaleMessage("0", 10, 2));

    assertThat(testee.generate(aggregatedSales, totalSaleMessage)).get().isEqualTo("\n--------------\n" +
            "SALES REPORT\n" +
            "--------------\n" +
            "\n" +
            "toblerone:\n" +
            "\t#Sales: 2\n" +
            "\t Total: 10\n" +
            "\n" +
            "--------------\n" +
            "--------------\n" +
            "Grand Total:\n" +
            "\t#Sales: 2\n" +
            "\t Total: 10\n" +
            "--------------");
  }

  @Test
  public void givenAggregatedAndTotals_forSeveralEntries_thenReportIsGenerated() {
    List<SaleMessage> aggregatedSales = new ArrayList<>();
    aggregatedSales.add(createSaleMessage("toblerone", 10, 1));
    aggregatedSales.add(createSaleMessage("mars", 20, 3));
    aggregatedSales.add(createSaleMessage("snickers", 20, 1));

    Optional<SaleMessage> totalSaleMessage = Optional.of(createSaleMessage("0", 40, 5));

    assertThat(testee.generate(aggregatedSales, totalSaleMessage)).get().isEqualTo("\n--------------\n" +
            "SALES REPORT\n" +
            "--------------\n" +
            "\n" +
            "toblerone:\n" +
            "\t#Sales: 1\n" +
            "\t Total: 10\n" +
            "mars:\n" +
            "\t#Sales: 3\n" +
            "\t Total: 20\n" +
            "snickers:\n" +
            "\t#Sales: 1\n" +
            "\t Total: 20\n" +
            "\n" +
            "--------------\n" +
            "--------------\n" +
            "Grand Total:\n" +
            "\t#Sales: 5\n" +
            "\t Total: 40\n" +
            "--------------");
  }
}