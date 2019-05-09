package com.psat.calculators;

import com.psat.sales.SaleMessage;

import java.util.List;

public interface Calculator<T> {

  T calculate(List<SaleMessage> sales);

}
