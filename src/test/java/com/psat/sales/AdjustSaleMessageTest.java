package com.psat.sales;

import org.junit.Test;

import static com.psat.sales.AdjustSaleMessage.Operation.*;
import static com.psat.util.SalesTestHelper.createSaleMessage;
import static org.assertj.core.api.Assertions.assertThat;

public class AdjustSaleMessageTest {

  @Test
  public void givenASaleMessage_withDifferentProductType_noAdjustmentPerformed() {
    SaleMessage saleMessage = createSaleMessage("raiders", 10);

    assertThat(new AdjustSaleMessage(new Sale("mars", 20), ADD).adjust(saleMessage)).isEqualTo(saleMessage);
    assertThat(new AdjustSaleMessage(new Sale("mars", 20), SUBTRACT).adjust(saleMessage)).isEqualTo(saleMessage);
    assertThat(new AdjustSaleMessage(new Sale("mars", 20), MULTIPLY).adjust(saleMessage)).isEqualTo(saleMessage);
  }


  @Test
  public void givenASaleMessage_andAdjustSaleWithAddOperation_thenValuesShouldBeAdded() {
    SaleMessage saleMessage = createSaleMessage("mars", 10);

    SaleMessage adjustedSale = new AdjustSaleMessage(new Sale("mars", 20), ADD).adjust(saleMessage);

    assertThat(adjustedSale.getCount()).isEqualTo(saleMessage.getCount());
    assertThat(adjustedSale.getSale().getProductType()).isEqualTo(saleMessage.getSale().getProductType());
    assertThat(adjustedSale.getSale().getValue()).isEqualTo(30);
  }

  @Test
  public void givenASaleMessage_andAdjustSaleWithSubtractOperation_thenValuesShouldBeSubtracted() {
    SaleMessage saleMessage = createSaleMessage("mars", 30);

    SaleMessage adjustedSale = new AdjustSaleMessage(new Sale("mars", 5), SUBTRACT).adjust(saleMessage);

    assertThat(adjustedSale.getCount()).isEqualTo(saleMessage.getCount());
    assertThat(adjustedSale.getSale().getProductType()).isEqualTo(saleMessage.getSale().getProductType());
    assertThat(adjustedSale.getSale().getValue()).isEqualTo(25);
  }

  @Test
  public void givenASaleMessage_andAdjustSaleWithMultiplyOperation_thenValuesShouldBeMultiplied() {
    SaleMessage saleMessage = createSaleMessage("toblerone", 30);

    SaleMessage adjustedSale = new AdjustSaleMessage(new Sale("toblerone", 5), MULTIPLY).adjust(saleMessage);

    assertThat(adjustedSale.getCount()).isEqualTo(saleMessage.getCount());
    assertThat(adjustedSale.getSale().getProductType()).isEqualTo(saleMessage.getSale().getProductType());
    assertThat(adjustedSale.getSale().getValue()).isEqualTo(150);
  }
}