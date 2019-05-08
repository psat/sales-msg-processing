package com.psat;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

public class MsgProcessingServiceTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  private static final String NO_SALES_REPORT = "No Sales\nTotal Value: 0";

  private List<SaleMessage> repository;
  private String report;

  private MsgProcessingService testee;

  @Before
  public void setUp() {
    repository = new ArrayList<>();
    report = "";

    testee = new MsgProcessingService(repository, () -> report = NO_SALES_REPORT);
  }

  @Test
  public void givenMessage_whenToBeProcessed_thenMessageGetsRecorded() {
    SaleMessage saleMessage = createMarsSale(20);
    testee.process(saleMessage);

    assertThat(repository).containsExactly(saleMessage);
  }

  @Test
  public void givenMessage_whenToBeProcessed_thenNoReportIsGenerated() {
    SaleMessage saleMessage = createMarsSale(10);
    testee.process(saleMessage);

    assertThat(report).isEqualTo("");
  }

  @Test
  public void givenMessage_andMessageCountIs10_whenToBeProcessed_thenAReportIsGenerated() {
    List<SaleMessage> processedMessages = Stream
            .generate(() -> createMarsSale(5))
            .limit(9)
            .collect(toList());
    repository.addAll(processedMessages);

    SaleMessage saleMessage = createMarsSale(15);
    testee.process(saleMessage);

    assertThat(report).isNotBlank();
    assertThat(report).isNotEqualTo(NO_SALES_REPORT);
  }

  private SaleMessage createMarsSale(int i) {
    return new SaleMessage(new Sale("mars", i));
  }
}