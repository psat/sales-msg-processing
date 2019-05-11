package com.psat.reporting;

import com.psat.sales.SaleMessage;

import java.util.List;
import java.util.Optional;

public class SpyReportGenerator implements ReportGenerator<SaleMessage> {

  private String report;
  private ReportGenerator<SaleMessage> reportGenerator;

  public SpyReportGenerator(ReportGenerator<SaleMessage> reportGenerator) {
    this.reportGenerator = reportGenerator;
    this.report = "";

  }

  @Override
  public Optional<String> generate(List<SaleMessage> sales, SaleMessage totalSales) {
    Optional<String> optionalReport = reportGenerator.generate(sales, totalSales);
    optionalReport.ifPresent(s -> report = s);
    return optionalReport;
  }

  @Override
  public Optional<String> generate(List<SaleMessage> sales) {
    Optional<String> optionalReport = reportGenerator.generate(sales);
    optionalReport.ifPresent(s -> report = s);
    return optionalReport;
  }

  public String getReport() {
    return report;
  }
}
