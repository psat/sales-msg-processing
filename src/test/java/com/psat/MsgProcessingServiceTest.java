package com.psat;

import com.psat.calculators.SalesCalculator;
import com.psat.calculators.TotalSalesCalculator;
import com.psat.reporting.AdjustmentsReport;
import com.psat.reporting.SalesReport;
import com.psat.reporting.SpyReportGenerator;
import com.psat.sales.AdjustSaleMessage;
import com.psat.sales.AdjustSaleMessage.Operation;
import com.psat.sales.SaleMessage;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.psat.sales.AdjustSaleMessage.Operation.*;
import static com.psat.util.SalesTestHelper.createAdjustSaleMessage;
import static com.psat.util.SalesTestHelper.createSaleMessage;
import static java.util.stream.Collectors.toMap;
import static org.assertj.core.api.Assertions.assertThat;

public class MsgProcessingServiceTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  private Map<Integer, SaleMessage> repository;
  private List<AdjustSaleMessage> adjustedRepository;
  private SalesCalculator salesCalculator;
  private TotalSalesCalculator totalSalesCalculator;
  private SpyReportGenerator<SaleMessage> spyReportGenerator;
  private SpyReportGenerator<AdjustSaleMessage> spyAdjustmentsReportGenerator;

  private MsgProcessingService testee;

  @Before
  public void setUp() {
    repository = new HashMap<>();
    adjustedRepository = new ArrayList<>();
    salesCalculator = new SalesCalculator();
    totalSalesCalculator = new TotalSalesCalculator();
    spyReportGenerator = new SpyReportGenerator<>(new SalesReport());
    spyAdjustmentsReportGenerator = new SpyReportGenerator<>(new AdjustmentsReport());
    testee = new MsgProcessingService(
            repository, adjustedRepository,
            salesCalculator, totalSalesCalculator,
            spyReportGenerator, spyAdjustmentsReportGenerator
    );
  }

  @Test
  public void givenANullRepository_thenNullPointerExceptionIsThrown() {
    thrown.expect(NullPointerException.class);
    testee = new MsgProcessingService(
            null, adjustedRepository,
            salesCalculator, totalSalesCalculator,
            spyReportGenerator, spyAdjustmentsReportGenerator);
  }

  @Test
  public void givenANullAdjustedRepository_thenNullPointerExceptionIsThrown() {
    thrown.expect(NullPointerException.class);
    testee = new MsgProcessingService(
            repository, null,
            salesCalculator, totalSalesCalculator,
            spyReportGenerator, spyAdjustmentsReportGenerator);
  }

  @Test
  public void givenANullSalesCalculator_thenNullPointerExceptionIsThrown() {
    thrown.expect(NullPointerException.class);
    testee = new MsgProcessingService(
            repository, adjustedRepository,
            null, totalSalesCalculator,
            spyReportGenerator, spyAdjustmentsReportGenerator);
  }

  @Test
  public void givenANullTotalSalesCalculator_thenNullPointerExceptionIsThrown() {
    thrown.expect(NullPointerException.class);
    testee = new MsgProcessingService(
            repository, adjustedRepository, null, totalSalesCalculator,
            spyReportGenerator, spyAdjustmentsReportGenerator);
  }

  @Test
  public void givenANullReportGenerator_thenNullPointerExceptionIsThrown() {
    thrown.expect(NullPointerException.class);
    testee = new MsgProcessingService(
            repository, adjustedRepository,
            salesCalculator, totalSalesCalculator,
            null, spyAdjustmentsReportGenerator);
  }

  @Test
  public void givenANullAdjustmentsReportGenerator_thenNullPointerExceptionIsThrown() {
    thrown.expect(NullPointerException.class);
    testee = new MsgProcessingService(
            repository, adjustedRepository,
            salesCalculator, totalSalesCalculator,
            spyReportGenerator, null);
  }

  @Test
  public void givenMessage_whenToBeProcessed_thenMessageGetsRecorded() {
    SaleMessage saleMessage = createSaleMessage(1, "mars", 20);
    testee.process(saleMessage);

    assertThat(repository.values()).containsExactly(saleMessage);
  }

  @Test
  public void givenMessage_whenToBeProcessed_thenNoReportIsGenerated() {
    SaleMessage saleMessage = createSaleMessage(1, "mars", 10);
    testee.process(saleMessage);

    assertThat(spyReportGenerator.getReport()).isEmpty();
  }

  @Test
  public void givenMessage_andMessageCountIs10_whenToBeProcessed_thenAReportIsGenerated() {
    generateSalesAndStore(0, 9, "mars", 5);
    SaleMessage saleMessage = createSaleMessage(10, "twix", 15);
    testee.process(saleMessage);

    assertReportEntry("mars", 9, 45);
    assertReportEntry("twix", 1, 15);
    assertReportTotalEntry(10, 60);
  }

  @Test
  public void givenMessage_andMessageCountIsMultipleOf10_whenToBeProcessed_thenAReportIsGeneratedForEachMultiple() {
    generateSalesAndStore(0, 9, "mars", 5);
    testee.process(createSaleMessage(9, "twix", 15));

    assertReportEntry("mars", 9, 45);
    assertReportEntry("twix", 1, 15);
    assertReportTotalEntry(10, 60);

    generateSalesAndStore(10, 9, "mars", 5);
    testee.process(createSaleMessage(19, "mars", 20));

    assertReportEntry("mars", 19, 110);
    assertReportEntry("twix", 1, 15);
    assertReportTotalEntry(20, 125);
  }

  private void generateSalesAndStore(int startId, int limit, String productType, int saleValue) {
    testee.addReceived(limit);
    repository.putAll(Stream.iterate(startId, i -> i + 1)
            .map(id -> createSaleMessage(id, productType, saleValue))
            .limit(limit)
            .collect(toMap(SaleMessage::getId, saleMessage -> saleMessage)));
  }

  @Test
  public void givenAnAdjustSaleMessage_andNoMessagesInSalesRepository_whenAdjust_thenNoAdjustmentsMade() {
    AdjustSaleMessage adjustSaleMessage = createAdjustSaleMessage("", 2, ADD);
    testee.process(adjustSaleMessage);

    assertThat(adjustedRepository).isEmpty();
  }

  @Test
  public void givenAnAdjustSaleMessage_andNoMessagesOfSameTypeInSalesRepository_whenAdjust_thenNoAdjustmentsMade() {
    generateSalesAndStore(0, 3, "twix", 25);
    AdjustSaleMessage adjustSaleMessage = createAdjustSaleMessage("", 2, ADD);
    testee.process(adjustSaleMessage);

    assertThat(adjustedRepository).isEmpty();
  }

  @Test
  public void givenAnAdjustSaleMessageForAdding_andMessagesOfSameTypeInSalesRepository_whenAdjust_thenAdjustmentsMade() {
    adjustAndAssert(ADD, 2, "twix", 25, 27);
  }

  @Test
  public void givenAnAdjustSaleMessageForSubtracting_andMessagesOfSameTypeInSalesRepository_whenAdjust_thenAdjustmentsMade() {
    adjustAndAssert(SUBTRACT, 5, "raiders", 22, 17);
  }

  @Test
  public void givenAnAdjustSaleMessageForMultiplying_andMessagesOfSameTypeInSalesRepository_whenAdjust_thenAdjustmentsMade() {
    adjustAndAssert(MULTIPLY, 3, "raiders", 5, 15);
  }

  @Test
  public void givenAnAdjustSaleMessageForAdd_10thMessageAndMessagesOfSameTypeInSalesRepository_whenAdjust_thenAdjustmentsMadeAndReportGenerated() {
    generateSalesAndStore(0, 3, "mars", 15);
    generateSalesAndStore(3, 6, "twix", 10);

    SaleMessage adjustMarsSales = createAdjustSaleMessage("mars", 3, ADD);
    testee.process(adjustMarsSales);

    assertThat(adjustedRepository).hasSize(1);
    assertThat(repository.values().stream()
            .filter(saleMessage -> saleMessage.getSale().getProductType().equals("mars"))
            .peek(saleMessage -> assertThat(saleMessage.getSale().getValue()).isEqualTo(18))
            .count()).isEqualTo(3);

    assertReportEntry("mars", 3, 54);
    assertReportEntry("twix", 6, 60);
    assertReportTotalEntry(9, 114);
  }

  @Test
  public void given49MessagesReceived_on50thMessage_whenToBeProcessed_thenAdjustmentsReportGeneratedAndProcessPaused() {
    generateSalesAndStore(0, 25, "mars", 15);
    generateSalesAndStore(25, 22, "twix", 10);
    testee.process(createAdjustSaleMessage("mars", 3, ADD));
    testee.process(createAdjustSaleMessage("twix", 4, SUBTRACT));
    testee.process(createSaleMessage(49, "twix", 15));

    SaleMessage sale51th = createSaleMessage(50, "toblerone", 13);
    testee.process(sale51th);
    AdjustSaleMessage sale52th = createAdjustSaleMessage("twix", 50, MULTIPLY);
    testee.process(sale52th);

    assertThat(repository).doesNotContain(new SimpleEntry<>(50, sale51th));
    assertThat(adjustedRepository).doesNotContain(sale52th);

    assertAdjustmentReportEntry(ADD, "mars", 3);
    assertAdjustmentReportEntry(SUBTRACT, "twix", 4);
    assertThat(spyAdjustmentsReportGenerator.getReport()).contains(String.format("Total Adjustments: %d", 2));

    assertThat(testee.getPaused()).isTrue();
  }

  private void adjustAndAssert(Operation op, int amount, String productType, int value, int expectedAdjustedValue) {
    SaleMessage mars = createSaleMessage(1, "mars", 1, 1);
    repository.put(1, mars);
    generateSalesAndStore(2, 3, productType, value);

    AdjustSaleMessage adjustSaleMessage = createAdjustSaleMessage(productType, amount, op);
    testee.process(adjustSaleMessage);

    assertThat(adjustedRepository).hasSize(1);

    assertThat(repository).hasSize(4);
    repository.values().stream()
            .filter(saleMessage -> saleMessage.getSale().getProductType().equals(productType))
            .forEach(saleMessage -> assertThat(saleMessage.getSale().getValue()).isEqualTo(expectedAdjustedValue));

    assertThat(repository.get(mars.getId())).isEqualTo(mars);
  }

  private void assertReportEntry(String expectedProductType, int expectedCountSales, int expectedTotal) {
    String expectedEntry = String.format("%s:\n" +
            "\t#Sales: %d\n" +
            "\t Total: %d", expectedProductType, expectedCountSales, expectedTotal);
    assertThat(spyReportGenerator.getReport()).contains(expectedEntry);
  }

  private void assertReportTotalEntry(int expectedCountSales, int expectedTotal) {
    assertReportEntry("Grand Total", expectedCountSales, expectedTotal);
  }

  private void assertAdjustmentReportEntry(Operation expectedOperation, String expectedProductType, int expectedValue) {
    String expectedEntry = String.format("%s(%s, %d)", expectedOperation, expectedProductType, expectedValue);
    assertThat(spyAdjustmentsReportGenerator.getReport()).contains(expectedEntry);
  }
}