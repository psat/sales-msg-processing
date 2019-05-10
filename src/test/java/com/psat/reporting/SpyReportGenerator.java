package com.psat.reporting;

import com.psat.sales.SaleMessage;

import java.util.List;
import java.util.Optional;

public class SpyReportGenerator implements ReportGenerator {

  private String report;
  private ReportGenerator reportGenerator;

  public SpyReportGenerator(ReportGenerator reportGenerator) {
    this.reportGenerator = reportGenerator;
    this.report = "";

  }

  @Override
  public Optional<String> generate(List<SaleMessage> sales, Optional<SaleMessage> totalSales) {
    Optional<String> optionalReport = reportGenerator.generate(sales, totalSales);
    optionalReport.ifPresent(s -> report = s);
    return optionalReport;
  }

  public String getReport() {
    return report;
  }
}
