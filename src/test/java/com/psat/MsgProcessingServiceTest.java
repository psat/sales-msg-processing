package com.psat;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class MsgProcessingServiceTest {

  private static final String NO_SALES_REPORT = "No Sales\nTotal Value: 0";

  private List<SaleMessage> repository;
  private ReportGenerator reportGenerator;
  private String report;

  private MsgProcessingService testee;

  @Before
  public void setUp() {
    repository = new ArrayList<>();
    reportGenerator = () -> report = NO_SALES_REPORT;
    report = "";

    testee = new MsgProcessingService(repository, reportGenerator);
  }

  @Test
  public void givenMessage_whenToBeProcessed_thenMessageGetsRecorded() {
    SaleMessage saleMessage = new SaleMessage();
    testee.process(saleMessage);

    assertThat(repository).isNotEmpty();
    assertThat(repository).containsExactly(saleMessage);
  }

  @Test
  public void givenMessage_whenToBeProcessed_thenNoReportIsGenerated() {
    SaleMessage saleMessage = new SaleMessage();
    testee.process(saleMessage);

    assertThat(report).isEqualTo("");
  }

  @Test
  public void givenMessage_andMessageCountIs10_whenToBeProcessed_thenAReportIsGenerated() {
    List<SaleMessage> processedMessages = Stream
            .generate(SaleMessage::new)
            .limit(9)
            .collect(Collectors.toList());
    repository.addAll(processedMessages);

    SaleMessage saleMessage = new SaleMessage();
    testee.process(saleMessage);

    assertThat(report).isNotEqualTo("");
  }
}