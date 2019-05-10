package com.psat;

import com.psat.calculators.SalesCalculator;
import com.psat.calculators.TotalSalesCalculator;
import com.psat.reporting.ReportGenerator;
import com.psat.sales.SaleMessage;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import static com.psat.util.Preconditions.checkNotNull;

public class MsgProcessingService {

  private static final Logger LOGGER = Logger.getLogger(MsgProcessingService.class.getSimpleName());

  private List<SaleMessage> repository;
  private ReportGenerator reportGenerator;
  private SalesCalculator salesCalculator;
  private TotalSalesCalculator totalSalesCalculator;

  public MsgProcessingService(List<SaleMessage> repository,
                              SalesCalculator salesCalculator,
                              TotalSalesCalculator totalSalesCalculator,
                              ReportGenerator reportGenerator) {
    checkNotNull(repository);
    checkNotNull(salesCalculator);
    checkNotNull(totalSalesCalculator);
    checkNotNull(reportGenerator);

    this.repository = repository;
    this.salesCalculator = salesCalculator;
    this.totalSalesCalculator = totalSalesCalculator;
    this.reportGenerator = reportGenerator;
  }

  public void process(SaleMessage saleMessage) {
    repository.add(saleMessage);

    if (repository.size() % 10 == 0) {
      List<SaleMessage> aggregated = salesCalculator.calculate(repository);
      Optional<SaleMessage> totals = totalSalesCalculator.calculate(repository);
      Optional<String> optionalReport = reportGenerator.generate(aggregated, totals);
      optionalReport.ifPresent(LOGGER::info);
    }
  }

}
