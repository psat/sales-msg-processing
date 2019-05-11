package com.psat.reporting;

import com.psat.sales.AdjustSaleMessage;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.psat.sales.AdjustSaleMessage.Operation.*;
import static com.psat.util.SalesTestHelper.createAdjustSaleMessage;
import static org.assertj.core.api.Assertions.assertThat;

public class AdjustmentsReportTest {

  private static final String EXPECTED_INVALID_REPORT = "\n" +
          "--------------\n" +
          "ADJUSTMENTS REPORT\n" +
          "--------------\n" +
          "\n" +
          "Invalid Adjusted Records Input!\n" +
          "\n" +
          "--------------\n" +
          "--------------\n" +
          "Total Adjustments:: 0\n" +
          "--------------";

  private AdjustmentsReport testee;

  @Before
  public void setUp() {
    testee = new AdjustmentsReport();
  }

  @Test
  public void givenEmptyListOfMessages_thenNoSalesReportIsGenerated() {
    assertThat(testee.generate(new ArrayList<>())).get().isEqualTo(EXPECTED_INVALID_REPORT);
  }

  @Test
  public void givenSeveralEntries_thenReportIsGenerated() {
    List<AdjustSaleMessage> adjustedSales = new ArrayList<>();
    adjustedSales.add(createAdjustSaleMessage("toblerone", 10, ADD));
    adjustedSales.add(createAdjustSaleMessage("mars", 20, SUBTRACT));
    adjustedSales.add(createAdjustSaleMessage("snickers", 5, MULTIPLY));
    adjustedSales.add(createAdjustSaleMessage("mars", 5, MULTIPLY));

    assertThat(testee.generate(adjustedSales)).get().isEqualTo("\n" +
            "--------------\n" +
            "ADJUSTMENTS REPORT\n" +
            "--------------\n" +
            "\n" +
            "\t\tadd( toblerone, 10)\n" +
            "\n" +
            "\t\tsubtract( mars, 20)\n" +
            "\n" +
            "\t\tmultiply( snickers, 5)\n" +
            "\n" +
            "\t\tmultiply( mars, 5)\n" +
            "\n" +
            "\n" +
            "--------------\n" +
            "--------------\n" +
            "Total Adjustments:: 4\n" +
            "--------------");
  }
}