package com.psat;

import com.psat.calculators.SalesCalculator;
import com.psat.calculators.TotalSalesCalculator;
import com.psat.reporting.ReportGenerator;
import com.psat.sales.AdjustSaleMessage;
import com.psat.sales.SaleMessage;
import com.psat.visitor.Visitor;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.psat.util.Preconditions.checkNotNull;

public class MsgProcessingService implements Visitor {

  private static final Logger LOGGER = Logger.getLogger(MsgProcessingService.class.getSimpleName());

  private Map<Integer, SaleMessage> repository;
  private List<AdjustSaleMessage> adjustmentsRepository;
  private ReportGenerator reportGenerator;
  private SalesCalculator salesCalculator;
  private TotalSalesCalculator totalSalesCalculator;

  private int receivedMessages = 0;

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

  @Override
  public void visit(SaleMessage saleMessage) {
    storeSale(saleMessage);
  }

  @Override
  public void visit(AdjustSaleMessage saleMessage) {
    adjustSales(saleMessage);
  }

  // VisibleForTesting
  void addReceived(int receivedMessages) {
    this.receivedMessages += receivedMessages;
  }

  public void process(SaleMessage saleMessage) {
    receivedMessages++;
    saleMessage.accept(this);
    calculateAndReport();
  }

  private void calculateAndReport(){
    if (receivedMessages % 10 == 0) {
      List<SaleMessage> aggregated = salesCalculator.calculate(repository.values());
      Optional<SaleMessage> totals = totalSalesCalculator.calculate(repository.values());
      Optional<String> optionalReport = reportGenerator.generate(aggregated, totals);
      optionalReport.ifPresent(LOGGER::info);
    }
  }

  private void storeSale(SaleMessage saleMessage) {
    repository.put(saleMessage.getId(), saleMessage);
  }

  private void adjustSales(AdjustSaleMessage adjustSaleMessage) {
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
