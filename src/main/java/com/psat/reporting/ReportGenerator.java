package com.psat.reporting;

import com.psat.sales.SaleMessage;

import java.util.List;
import java.util.Optional;

public interface ReportGenerator {

  Optional<String> generate(List<SaleMessage> sales, Optional<SaleMessage> totalSales);

}
