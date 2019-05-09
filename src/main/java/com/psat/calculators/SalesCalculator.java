package com.psat.calculators;

import com.psat.sales.Sale;
import com.psat.sales.SaleMessage;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.psat.sales.SaleMessage.saleMessageIdentity;
import static java.util.stream.Collectors.toList;

public class SalesCalculator implements Calculator<List<SaleMessage>> {

  @Override
  public List<SaleMessage> calculate(List<SaleMessage> sales) {
    return getSalesGroupedByProduct(sales).entrySet()
            .stream()
            .map(entry -> entry.getValue()
                    .stream()
                    .reduce(saleMessageIdentity(entry.getKey()), this::salesAccumulator)
            )
            .sorted(Comparator.comparing(sale -> sale.getSale().getProductType()))
            .collect(toList());
  }

  private Map<String, List<SaleMessage>> getSalesGroupedByProduct(List<SaleMessage> sales) {
    return sales.stream()
            .collect(Collectors.groupingBy(saleMessage -> saleMessage.getSale().getProductType()));
  }

  private int calculateTotal(SaleMessage saleMessage) {
    return saleMessage.getCount() * saleMessage.getSale().getValue();
  }

  private SaleMessage salesAccumulator(SaleMessage saleMessage, SaleMessage otherSaleMessage) {
    int value = calculateTotal(otherSaleMessage);
    int accumulatedCount = saleMessage.getCount() + otherSaleMessage.getCount();
    int accumulatedValue = saleMessage.getSale().getValue() + value;

    return new SaleMessage(
            new Sale(otherSaleMessage.getSale().getProductType(), accumulatedValue),
            accumulatedCount);
  }
}
