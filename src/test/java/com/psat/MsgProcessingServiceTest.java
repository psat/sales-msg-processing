package com.psat;

import com.psat.calculators.SalesCalculator;
import com.psat.calculators.TotalSalesCalculator;
import com.psat.reporting.SalesReport;
import com.psat.reporting.SpyReportGenerator;
import com.psat.sales.AdjustSaleMessage;
import com.psat.sales.SaleMessage;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.*;
import java.util.stream.Stream;

import static com.psat.sales.AdjustSaleMessage.Operation.ADD;
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
  private SpyReportGenerator spyReportGenerator;

  private MsgProcessingService testee;

  @Before
  public void setUp() {
    repository = new HashMap<>();
    adjustedRepository = new ArrayList<>();
    salesCalculator = new SalesCalculator();
    totalSalesCalculator = new TotalSalesCalculator();
    spyReportGenerator = new SpyReportGenerator(new SalesReport());
    testee = new MsgProcessingService(
            repository, adjustedRepository, salesCalculator, totalSalesCalculator, spyReportGenerator
    );
  }

  @Test
  public void givenANullRepository_thenNullPointerExceptionIsThrown() {
    thrown.expect(NullPointerException.class);
    testee = new MsgProcessingService(
            null, adjustedRepository,
            salesCalculator, totalSalesCalculator,
            (saleMessages, totalSales) -> Optional.of(""));
  }

  @Test
  public void givenANullAdjustedRepository_thenNullPointerExceptionIsThrown() {
    thrown.expect(NullPointerException.class);
    testee = new MsgProcessingService(
            repository, null,
            salesCalculator, totalSalesCalculator,
            (saleMessages, totalSales) -> Optional.of(""));
  }

  @Test
  public void givenANullSalesCalculator_thenNullPointerExceptionIsThrown() {
    thrown.expect(NullPointerException.class);
    testee = new MsgProcessingService(
            repository, adjustedRepository,
            null, totalSalesCalculator,
            (saleMessages, totalSales) -> Optional.of(""));
  }

  @Test
  public void givenANullTotalSalesCalculator_thenNullPointerExceptionIsThrown() {
    thrown.expect(NullPointerException.class);
    testee = new MsgProcessingService(
            repository, adjustedRepository, null, totalSalesCalculator,
            (saleMessages, totalSales) -> Optional.of(""));
  }

  @Test
  public void givenANullReportGenerator_thenNullPointerExceptionIsThrown() {
    thrown.expect(NullPointerException.class);
    testee = new MsgProcessingService(
            repository, adjustedRepository,
            salesCalculator, totalSalesCalculator,
            null);
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
    generateSalesAndStore(0, 9,"mars", 5);
    SaleMessage saleMessage = createSaleMessage(10, "twix", 15);
    testee.process(saleMessage);

    assertThat(spyReportGenerator.getReport()).contains("mars:\n" +
            "\t#Sales: 9\n" +
            "\t Total: 45");

    assertThat(spyReportGenerator.getReport()).contains("twix:\n" +
            "\t#Sales: 1\n" +
            "\t Total: 15");

    assertThat(spyReportGenerator.getReport()).contains("Grand Total\n" +
            "\t#Sales: 10\n" +
            "\t Total: 60");
  }

  @Test
  public void givenMessage_andMessageCountIsMultipleOf10_whenToBeProcessed_thenAReportIsGeneratedForEachMultiple() {
    generateSalesAndStore(0, 9, "mars", 5);
    testee.process(createSaleMessage(9, "twix", 15));

    assertThat(spyReportGenerator.getReport()).contains("mars:\n" +
            "\t#Sales: 9\n" +
            "\t Total: 45");
    assertThat(spyReportGenerator.getReport()).contains("twix:\n" +
            "\t#Sales: 1\n" +
            "\t Total: 15");
    assertThat(spyReportGenerator.getReport()).contains("Grand Total\n" +
            "\t#Sales: 10\n" +
            "\t Total: 60");

    generateSalesAndStore(10, 9, "mars", 5);
    testee.process(createSaleMessage(19, "mars", 20));

    assertThat(spyReportGenerator.getReport()).contains("mars:\n" +
            "\t#Sales: 19\n" +
            "\t Total: 110");
    assertThat(spyReportGenerator.getReport()).contains("twix:\n" +
            "\t#Sales: 1\n" +
            "\t Total: 15");
    assertThat(spyReportGenerator.getReport()).contains("Grand Total\n" +
            "\t#Sales: 20\n" +
            "\t Total: 125");
  }

  private void generateSalesAndStore(int startId, int limit, String productType, int saleValue) {
    repository.putAll(Stream.iterate(startId, i -> i + 1)
            .map(id -> createSaleMessage(id, productType, saleValue))
            .limit(limit)
            .collect(toMap(SaleMessage::getId, saleMessage -> saleMessage)));
  }

  @Test
  public void givenAnAdjustSaleMessage_andNoMessagesInSalesRepository_whenAdjust_thenNoAdjustmentsMade() {
    AdjustSaleMessage adjustSaleMessage = createAdjustSaleMessage("", 2, ADD);
    testee.adjustSales(adjustSaleMessage);

    assertThat(adjustedRepository).isEmpty();
  }

  @Test
  public void givenAnAdjustSaleMessage_andNoMessagesOfSameTypeInSalesRepository_whenAdjust_thenNoAdjustmentsMade() {
    generateSalesAndStore(0, 3, "twix", 25);
    AdjustSaleMessage adjustSaleMessage = createAdjustSaleMessage("", 2, ADD);
    testee.adjustSales(adjustSaleMessage);

    assertThat(adjustedRepository).isEmpty();
  }

  @Test
  public void givenAnAdjustSaleMessage_andMessagesOfSameTypeInSalesRepository_whenAdjust_thenAdjustmentsMade() {
    SaleMessage mars = createSaleMessage(1, "mars", 1, 1);
    repository.put(1, mars);
    generateSalesAndStore(2, 3, "twix", 25);

    AdjustSaleMessage adjustSaleMessage = createAdjustSaleMessage("twix", 2, ADD);
    testee.adjustSales(adjustSaleMessage);

    assertThat(adjustedRepository).hasSize(1);

    assertThat(repository).hasSize(4);
    repository.values().stream()
            .filter(saleMessage -> saleMessage.getSale().getProductType().equals("twix"))
            .forEach(saleMessage -> assertThat(saleMessage.getSale().getValue()).isEqualTo(27));

    assertThat(repository.get(mars.getId())).isEqualTo(mars);
  }
}