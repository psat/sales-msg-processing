package com.psat.util;

import com.psat.sales.Sale;
import com.psat.sales.SaleMessage;

public class SalesTestHelper {

  private SalesTestHelper(){

  }

  public static SaleMessage createSaleMessage(String productType, int value) {
    return createSaleMessage(productType, value, 1);
  }

  public static SaleMessage createSaleMessage(String productType, int value, int count) {
    return new SaleMessage(new Sale(productType, value), count);
  }
}
