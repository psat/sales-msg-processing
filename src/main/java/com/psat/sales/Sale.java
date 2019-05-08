package com.psat.sales;

import java.util.Objects;

import static com.psat.util.Preconditions.checkNotNull;

public class Sale {

  private final String productType;
  private final int value;

  public Sale(String productType, int value) {
    checkNotNull(productType);

    this.productType = productType;
    this.value = value;
  }

  public String getProductType() {
    return productType;
  }

  public int getValue() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Sale)) return false;

    Sale that = (Sale) o;

    return Objects.equals(productType, that.productType) &&
            that.value == value;
  }

  @Override
  public int hashCode() {
    return Objects.hash(productType, value);
  }

  @Override
  public String toString() {
    return "Sale{" +
            "productType='" + productType + '\'' +
            ", value=" + value +
            '}';
  }
}
