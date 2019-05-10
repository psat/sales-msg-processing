package com.psat.util;

import com.psat.sales.AdjustSaleMessage;
import com.psat.sales.AdjustSaleMessage.Operation;
import com.psat.sales.Sale;
import com.psat.sales.SaleMessage;

public class SalesTestHelper {

  private SalesTestHelper() {

  }

  public static SaleMessage createSaleMessage(String productType, int value) {
    return createSaleMessage(1, productType, value, 1);
  }

  public static SaleMessage createSaleMessage(int id, String productType, int value) {
    return createSaleMessage(id, productType, value, 1);
  }

  public static SaleMessage createSaleMessage(String productType, int value, int count) {
    return new SaleMessage(1, new Sale(productType, value), count);
  }

  public static SaleMessage createSaleMessage(int id, String productType, int value, int count) {
    return new SaleMessage(id, new Sale(productType, value), count);
  }

  public static AdjustSaleMessage createAdjustSaleMessage(String productType, int value, Operation op) {
    return new AdjustSaleMessage(-1, new Sale(productType, value), op);
  }
}
