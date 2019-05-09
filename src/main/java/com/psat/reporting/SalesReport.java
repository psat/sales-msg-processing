package com.psat.reporting;

import com.psat.sales.SaleMessage;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SalesReport implements ReportGenerator {

  private static final Logger LOGGER = Logger.getLogger(SalesReport.class.getSimpleName());

  private static final String SALE_REPORT_ENTRY_FORMAT = "%s:\n\t#Sales: %d\n\t Total: %d";
  private static final String SALES_REPORT_HEADER = "\n--------------\nSALES REPORT\n--------------\n\n";
  private static final String SALES_REPORT_FOOTER_FORMAT = "\n\n--------------\n--------------\n" +
          "Grand Total\n\t#Sales: %d\n\t Total: %d\n--------------";

  @Override
  public Optional<String> generate(List<SaleMessage> aggregatedSales, Optional<SaleMessage> totalSales) {
    String footer = totalSales
            .map(this::createFooter)
            .orElse(String.format(SALES_REPORT_FOOTER_FORMAT, 0, 0));

    return aggregatedSales.isEmpty() || !totalSales.isPresent() ?
            generateInvalidReport(footer) : generateValidReport(aggregatedSales, footer);
  }

  private Optional<String> generateValidReport(List<SaleMessage> aggregatedSales, String footer) {
    return generateReport(
            aggregatedSales.stream(),
            this::createReportEntry,
            footer
    );
  }

  private Optional<String> generateInvalidReport(String footer) {
    return generateReport(
            Stream.of(SaleMessage.saleMessageIdentity("")),
            saleMessage -> "Invalid input!",
            footer
    );
  }

  private Optional<String> generateReport(Stream<SaleMessage> stream,
                                          Function<SaleMessage, String> reportEntryGenerator,
                                          String footer) {
    String reportBody = stream
            .map(reportEntryGenerator)
            .collect(
                    Collectors.joining("\n", SALES_REPORT_HEADER, footer)
            );

    LOGGER.info(reportBody);
    return Optional.ofNullable(reportBody);
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
