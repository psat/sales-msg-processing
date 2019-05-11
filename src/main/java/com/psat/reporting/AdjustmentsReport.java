package com.psat.reporting;

import com.psat.sales.AdjustSaleMessage;
import com.psat.sales.Sale;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class AdjustmentsReport implements ReportGenerator<AdjustSaleMessage> {

  private static final String REPORT_HEADER = "\n--------------\nADJUSTMENTS REPORT\n--------------\n\n";
  private static final String REPORT_ENTRY_FORMAT = "\t%s(%s, %d)";
  private static final String REPORT_FOOTER_FORMAT = "\n\n--------------\n--------------\n" +
          "Total Adjustments: %d\n--------------";

  @Override
  public Optional<String> generate(List<AdjustSaleMessage> sales) {
    String footer = String.format(REPORT_FOOTER_FORMAT, sales.size());
    return sales.isEmpty() ?
            generateNoAdjustmentsReport(footer) : generateValidReport(sales, footer);
  }

  private Optional<String> generateValidReport(List<AdjustSaleMessage> sales, String footer) {
    return generate(
            sales.stream(),
            this::createReportEntry,
            REPORT_HEADER,
            footer
    );
  }

  private Optional<String> generateNoAdjustmentsReport(String footer) {
    return generate(
            Stream.of(new AdjustSaleMessage(-1, new Sale("", 0), AdjustSaleMessage.Operation.ADD)),
            saleMessage -> "No Adjusted Records!",
            REPORT_HEADER,
            footer
    );
  }

  private String createReportEntry(AdjustSaleMessage saleMessage) {
    return String.format(REPORT_ENTRY_FORMAT,
            saleMessage.getOperation(),
            saleMessage.getSale().getProductType(),
            saleMessage.getSale().getValue()
    );
  }
}
