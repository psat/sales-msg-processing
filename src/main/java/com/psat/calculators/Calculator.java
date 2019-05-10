package com.psat.calculators;

import com.psat.sales.SaleMessage;

import java.util.Collection;

public interface Calculator<T> {

  T calculate(Collection<SaleMessage> sales);

}
