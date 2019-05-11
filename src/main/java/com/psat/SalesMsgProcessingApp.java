package com.psat;

import com.psat.calculators.SalesCalculator;
import com.psat.calculators.TotalSalesCalculator;
import com.psat.reporting.AdjustmentsReport;
import com.psat.reporting.SalesReport;
import com.psat.sales.AdjustSaleMessage;
import com.psat.sales.AdjustSaleMessage.Operation;
import com.psat.sales.Sale;
import com.psat.sales.SaleMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class SalesMsgProcessingApp {

  private static final Logger LOGGER = Logger.getLogger(SalesMsgProcessingApp.class.getSimpleName());
  private static final List<String> PRODUCT_TYPES = Arrays.asList("mars", "snickers", "twix", "raiders", "toblerone");

  private static ThreadLocalRandom random = ThreadLocalRandom.current();

  public static void main(String[] varargs) {
    LOGGER.info("Initializing");
    MsgProcessingService msgProcessingService = new MsgProcessingService(
            new HashMap<>(), new ArrayList<>(),
            new SalesCalculator(), new TotalSalesCalculator(),
            new SalesReport(), new AdjustmentsReport()
    );

    LOGGER.info("Ready to receive messages");
    Stream.iterate(0, integer -> integer + 1)
            .limit(100)
            .map(SalesMsgProcessingApp::createRandomSale)
            .peek(saleMessage -> LOGGER.warning("Sending message with id: " + saleMessage.getId()))
            .peek(saleMessage -> simulateWorking())
            .forEach(msgProcessingService::process);

    LOGGER.info("Exiting");
  }

  private static void simulateWorking() {
    try {
      Thread.sleep(random.nextInt(200, 300));
    } catch (InterruptedException e) {
      // nothing to do here
    }
  }

  private static SaleMessage createRandomSale(int id) {
    int index = random.nextInt(0, 5);
    int value = random.nextInt(1, 50);
    String productType = PRODUCT_TYPES.get(index);

    if (random.nextBoolean()) {
      Operation operation = Operation.values()[random.nextInt(0, 3)];
      return new AdjustSaleMessage(-1, new Sale(productType, (value / 10) + 1), operation);

    } else {
      return new SaleMessage(id, new Sale(productType, value));
    }
  }
}