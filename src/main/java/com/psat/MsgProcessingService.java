package com.psat;

import java.util.List;

import static com.psat.util.Preconditions.checkNotNull;

public class MsgProcessingService {

  private List<SaleMessage> repository;
  private ReportGenerator reportGenerator;

  public MsgProcessingService(List<SaleMessage> repository, ReportGenerator reportGenerator) {
    checkNotNull(repository);
    checkNotNull(reportGenerator);

    this.repository = repository;
    this.reportGenerator = reportGenerator;
  }

  public void process(SaleMessage saleMessage) {
    repository.add(saleMessage);

    if (repository.size() % 10 == 0) {
      reportGenerator.generate();
    }
  }

}
