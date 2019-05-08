package com.psat.sales;

import java.util.Objects;

public class SaleMessage {

  private Sale sale;
  private int count;

  public SaleMessage(Sale sale) {
    this(sale, 1);
  }

  public SaleMessage(Sale sale, int count) {
    this.sale = sale;
    this.count = count;
  }

  public Sale getSale() {
    return sale;
  }

  public int getCount() {
    return count;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;

    if (!(o instanceof SaleMessage)) return false;

    SaleMessage that = (SaleMessage) o;
    return count == that.count &&
            Objects.equals(sale, that.sale);
  }

  @Override
  public int hashCode() {
    return Objects.hash(sale, count);
  }

  @Override
  public String toString() {
    return "SaleMessage{" +
            "sale=" + sale +
            ", count=" + count +
            '}';
  }
}
