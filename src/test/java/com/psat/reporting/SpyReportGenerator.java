package com.psat.reporting;

import com.psat.sales.SaleMessage;

import java.util.List;
import java.util.Optional;

public class SpyReportGenerator<S extends SaleMessage> implements ReportGenerator<S> {

  private String report;
  private ReportGenerator<S> reportGenerator;

  public SpyReportGenerator(ReportGenerator<S> reportGenerator) {
    this.reportGenerator = reportGenerator;
    this.report = "";
  }

  @Override
  public Optional<String> generate(List<S> sales, S totalSales) {
    Optional<String> optionalReport = reportGenerator.generate(sales, totalSales);
    optionalReport.ifPresent(s -> report = s);
    return optionalReport;
  }

  @Override
  public Optional<String> generate(List<S> sales) {
    Optional<String> optionalReport = reportGenerator.generate(sales);
    optionalReport.ifPresent(s -> report = s);
    return optionalReport;
  }

  public String getReport() {
    return report;
  }
}
