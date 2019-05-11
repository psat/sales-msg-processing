package com.psat.sales;

import com.psat.Message;
import com.psat.visitor.Visitor;

import java.util.Objects;

public class SaleMessage implements Message {

  private int id;
  private Sale sale;
  private int count;

  public SaleMessage(int id, Sale sale) {
    this(id, sale, 1);
  }

  public SaleMessage(int id, Sale sale, int count) {
    this.id = id;
    this.sale = sale;
    this.count = count;
  }

  public static SaleMessage saleMessageIdentity(String productType) {
    return new SaleMessage(-1, new Sale(productType, 0), 0);
  }

  public int getId() {
    return id;
  }

  public Sale getSale() {
    return sale;
  }

  public int getCount() {
    return count;
  }

  @Override
  public void accept(Visitor visitor) {
    visitor.visit(this);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof SaleMessage)) return false;
    SaleMessage that = (SaleMessage) o;
    return id == that.id &&
            count == that.count &&
            Objects.equals(sale, that.sale);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, sale, count);
  }

  @Override
  public String toString() {
    return "SaleMessage{" +
            "id=" + id +
            ", sale=" + sale +
            ", count=" + count +
            '}';
  }
}
