package com.psat.visitor;

import com.psat.sales.AdjustSaleMessage;
import com.psat.sales.SaleMessage;

public interface Visitor {

  void visit(SaleMessage saleMessage);

  void visit(AdjustSaleMessage saleMessage);

}
