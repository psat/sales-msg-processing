package com.psat.calculators;

import com.psat.sales.Sale;
import com.psat.sales.SaleMessage;

import java.util.List;
import java.util.Optional;

import static com.psat.sales.SaleMessage.saleMessageIdentity;

public class TotalSalesCalculator implements Calculator<Optional<SaleMessage>> {

  @Override
  public Optional<SaleMessage> calculate(List<SaleMessage> sales) {
    return Optional.ofNullable(
            sales.stream().reduce(saleMessageIdentity(""), this::totalSalesAccumulator)
    );
  }

  private SaleMessage totalSalesAccumulator(SaleMessage saleMessage, SaleMessage otherSaleMessage) {
    int accumulatedCount = saleMessage.getCount() + otherSaleMessage.getCount();
    int accumulatedValue = saleMessage.getSale().getValue() + otherSaleMessage.getSale().getValue();

    return new SaleMessage(
            new Sale("", accumulatedValue),
            accumulatedCount);
  }
}
