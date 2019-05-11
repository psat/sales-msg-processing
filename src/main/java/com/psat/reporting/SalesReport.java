package com.psat.reporting;

import com.psat.sales.SaleMessage;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class SalesReport implements ReportGenerator<SaleMessage> {

  private static final String SALE_REPORT_ENTRY_FORMAT = "%s:\n\t#Sales: %d\n\t Total: %d";
  private static final String SALES_REPORT_HEADER = "\n--------------\nSALES REPORT\n--------------\n\n";
  private static final String SALES_REPORT_FOOTER_FORMAT = "\n\n--------------\n--------------\n" +
          "Grand Total:\n\t#Sales: %d\n\t Total: %d\n--------------";

  @Override
  public Optional<String> generate(List<SaleMessage> aggregatedSales, SaleMessage totalSales) {
    Optional<SaleMessage> optionalTotal = Optional.ofNullable(totalSales);
    String footer = optionalTotal
            .map(this::createFooter)
            .orElse(String.format(SALES_REPORT_FOOTER_FORMAT, 0, 0));

    return aggregatedSales.isEmpty() || !optionalTotal.isPresent() ?
            generateInvalidReport(footer) : generateValidReport(aggregatedSales, footer);
  }

  private Optional<String> generateValidReport(List<SaleMessage> aggregatedSales, String footer) {
    return generate(
            aggregatedSales.stream(),
            this::createReportEntry,
            SALES_REPORT_HEADER,
            footer
    );
  }

  private Optional<String> generateInvalidReport(String footer) {
    return generate(
            Stream.of(SaleMessage.saleMessageIdentity("")),
            saleMessage -> "Invalid input!",
            SALES_REPORT_HEADER,
            footer
    );
  }

  private String createReportEntry(SaleMessage saleMessage) {
    return String.format(SALE_REPORT_ENTRY_FORMAT,
            saleMessage.getSale().getProductType(),
            saleMessage.getCount(),
            saleMessage.getSale().getValue()
    );
  }

  private String createFooter(SaleMessage saleMessage) {
    return String.format(SALES_REPORT_FOOTER_FORMAT,
            saleMessage.getCount(),
            saleMessage.getSale().getValue()
    );
  }
}
