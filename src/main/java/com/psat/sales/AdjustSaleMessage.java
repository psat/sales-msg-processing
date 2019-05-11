package com.psat.sales;

import com.psat.visitor.Visitor;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.logging.Logger;

public class AdjustSaleMessage extends SaleMessage {

  private static final Logger LOGGER = Logger.getLogger(AdjustSaleMessage.class.getSimpleName());

  private Operation operation;

  public AdjustSaleMessage(int id, Sale sale, Operation operation) {
    super(id, sale, 0);
    this.operation = operation;
  }

  public Operation getOperation() {
    return operation;
  }

  @Override
  public void accept(Visitor visitor) {
    visitor.visit(this);
  }

  public SaleMessage adjust(SaleMessage saleMessage) {
    if (productTypeIsDifferentThan(saleMessage)) {
      LOGGER.warning(() -> String.format("Cannot apply %s operation between different product types", operation));
      return saleMessage;
    }

    return operation.apply(saleMessage, this);
  }

  private boolean productTypeIsDifferentThan(SaleMessage saleMessage) {
    return !saleMessage.getSale().getProductType().equals(this.getSale().getProductType());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;

    if (!(o instanceof AdjustSaleMessage)) return false;

    if (!super.equals(o)) return false;

    AdjustSaleMessage that = (AdjustSaleMessage) o;
    return operation == that.operation;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), operation);
  }

  @Override
  public String toString() {
    return "AdjustSaleMessage{" +
            "operation=" + operation +
            "} " + super.toString();
  }

  public enum Operation implements BiFunction<SaleMessage, AdjustSaleMessage, SaleMessage> {

    ADD {
      @Override
      public SaleMessage apply(SaleMessage saleMessage, AdjustSaleMessage adjustSaleMessage) {
        return Operation.apply(saleMessage, adjustSaleMessage, Integer::sum);
      }
    },

    SUBTRACT {
      @Override
      public SaleMessage apply(SaleMessage saleMessage, AdjustSaleMessage adjustSaleMessage) {
        return Operation.apply(saleMessage, adjustSaleMessage, (value1, value2) -> value1 - value2);
      }
    },

    MULTIPLY {
      @Override
      public SaleMessage apply(SaleMessage saleMessage, AdjustSaleMessage adjustSaleMessage) {
        return Operation.apply(saleMessage, adjustSaleMessage, (value1, value2) -> value1 * value2);
      }
    };

    @Override
    public String toString() {
      return this.name().toLowerCase();
    }

    private static SaleMessage apply(SaleMessage saleMessage, AdjustSaleMessage adjustSaleMessage, BinaryOperator<Integer> op) {
      Sale sale = saleMessage.getSale();
      int adjustedValue = op.apply(sale.getValue(), adjustSaleMessage.getSale().getValue());
      return new SaleMessage(saleMessage.getId(),
              new Sale(sale.getProductType(), adjustedValue),
              saleMessage.getCount());
    }
  }
}
