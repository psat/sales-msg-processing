package com.psat;

import com.psat.calculators.SalesCalculator;
import com.psat.calculators.TotalSalesCalculator;
import com.psat.reporting.ReportGenerator;
import com.psat.sales.AdjustSaleMessage;
import com.psat.sales.SaleMessage;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.psat.util.Preconditions.checkNotNull;

public class MsgProcessingService {

  private static final Logger LOGGER = Logger.getLogger(MsgProcessingService.class.getSimpleName());

  private Map<Integer, SaleMessage> repository;
  private List<AdjustSaleMessage> adjustmentsRepository;
  private ReportGenerator reportGenerator;
  private SalesCalculator salesCalculator;
  private TotalSalesCalculator totalSalesCalculator;

  public MsgProcessingService(Map<Integer, SaleMessage> repository,
                              List<AdjustSaleMessage> adjustmentsRepository,
                              SalesCalculator salesCalculator,
                              TotalSalesCalculator totalSalesCalculator,
                              ReportGenerator reportGenerator) {
    checkNotNull(repository);
    checkNotNull(adjustmentsRepository);
    checkNotNull(salesCalculator);
    checkNotNull(totalSalesCalculator);
    checkNotNull(reportGenerator);

    this.repository = repository;
    this.adjustmentsRepository = adjustmentsRepository;
    this.salesCalculator = salesCalculator;
    this.totalSalesCalculator = totalSalesCalculator;
    this.reportGenerator = reportGenerator;
  }

  public void process(SaleMessage saleMessage) {
    repository.put(saleMessage.getId(), saleMessage);

    if (repository.size() % 10 == 0) {
      List<SaleMessage> aggregated = salesCalculator.calculate(repository.values());
      Optional<SaleMessage> totals = totalSalesCalculator.calculate(repository.values());
      Optional<String> optionalReport = reportGenerator.generate(aggregated, totals);
      optionalReport.ifPresent(LOGGER::info);
    }
  }

  public void adjustSales(AdjustSaleMessage adjustSaleMessage) {
    repository.values().stream()
            .filter(saleMessage -> saleMessage.getSale().getProductType().equals(adjustSaleMessage.getSale().getProductType()))
            .map(adjustSaleMessage::adjust)
            .map(saleMessage -> repository.put(saleMessage.getId(), saleMessage))
            .collect(Collectors.toList())
            .stream()
            .findFirst()
            .ifPresent(saleMessage -> adjustmentsRepository.add(adjustSaleMessage));
  }
}
