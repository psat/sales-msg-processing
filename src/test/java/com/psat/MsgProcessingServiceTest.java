package com.psat;

import com.psat.calculators.SalesCalculator;
import com.psat.calculators.TotalSalesCalculator;
import com.psat.reporting.SalesReport;
import com.psat.reporting.SpyReportGenerator;
import com.psat.sales.SaleMessage;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.psat.util.SalesTestHelper.createSaleMessage;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

public class MsgProcessingServiceTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  private List<SaleMessage> repository;

  private MsgProcessingService testee;
  private SalesCalculator salesCalculator;
  private TotalSalesCalculator totalSalesCalculator;
  private SpyReportGenerator spyReportGenerator;

  @Before
  public void setUp() {
    repository = new ArrayList<>();
    salesCalculator = new SalesCalculator();
    totalSalesCalculator = new TotalSalesCalculator();
    spyReportGenerator = new SpyReportGenerator(new SalesReport());
    testee = new MsgProcessingService(repository, salesCalculator, totalSalesCalculator, spyReportGenerator);
  }

  @Test
  public void givenANullRepository_thenNullPointerExceptionIsThrown() {
    thrown.expect(NullPointerException.class);

    testee = new MsgProcessingService(null, salesCalculator, totalSalesCalculator,
            (saleMessages, totalSales) -> Optional.of(""));
  }

  @Test
  public void givenANullSalesCalculator_thenNullPointerExceptionIsThrown() {
    thrown.expect(NullPointerException.class);

    testee = new MsgProcessingService(repository, null, totalSalesCalculator,
            (saleMessages, totalSales) -> Optional.of(""));
  }

  @Test
  public void givenANullTotalSalesCalculator_thenNullPointerExceptionIsThrown() {
    thrown.expect(NullPointerException.class);

    testee = new MsgProcessingService(repository, null, totalSalesCalculator,
            (saleMessages, totalSales) -> Optional.of(""));
  }

  @Test
  public void givenANullReportGenerator_thenNullPointerExceptionIsThrown() {
    thrown.expect(NullPointerException.class);

    testee = new MsgProcessingService(new ArrayList<>(), salesCalculator, totalSalesCalculator, null);
  }

  @Test
  public void givenMessage_whenToBeProcessed_thenMessageGetsRecorded() {
    SaleMessage saleMessage = createSaleMessage("mars", 20);
    testee.process(saleMessage);

    assertThat(repository).containsExactly(saleMessage);
  }

  @Test
  public void givenMessage_whenToBeProcessed_thenNoReportIsGenerated() {
    SaleMessage saleMessage = createSaleMessage("mars", 10);
    testee.process(saleMessage);

    assertThat(spyReportGenerator.getReport()).isEmpty();
  }

  @Test
  public void givenMessage_andMessageCountIs10_whenToBeProcessed_thenAReportIsGenerated() {
    List<SaleMessage> processedMessages = Stream
            .generate(() -> createSaleMessage("mars", 5))
            .limit(9)
            .collect(toList());
    repository.addAll(processedMessages);

    SaleMessage saleMessage = createSaleMessage("twix", 15);
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
    repository.addAll(Stream
            .generate(() -> createSaleMessage("mars", 5))
            .limit(9)
            .collect(toList()));
    testee.process(createSaleMessage("twix", 15));

    assertThat(spyReportGenerator.getReport()).contains("mars:\n" +
            "\t#Sales: 9\n" +
            "\t Total: 45");
    assertThat(spyReportGenerator.getReport()).contains("twix:\n" +
            "\t#Sales: 1\n" +
            "\t Total: 15");
    assertThat(spyReportGenerator.getReport()).contains("Grand Total\n" +
            "\t#Sales: 10\n" +
            "\t Total: 60");

    repository.addAll(Stream
            .generate(() -> createSaleMessage("mars", 5))
            .limit(9)
            .collect(toList()));
    testee.process(createSaleMessage("mars", 20));

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
}