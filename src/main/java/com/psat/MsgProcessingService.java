package com.psat;

import java.util.List;

public class MsgProcessingService {

  private List<SaleMessage> repository;

  public MsgProcessingService(List<SaleMessage> repository) {
    this.repository = repository;
  }

  public void process(SaleMessage saleMessage) {
    repository.add(saleMessage);
  }

}
