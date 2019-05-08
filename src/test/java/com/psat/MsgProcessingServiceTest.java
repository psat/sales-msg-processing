package com.psat;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class MsgProcessingServiceTest {

  private List<SaleMessage> repository;
  private MsgProcessingService testee;

  @Before
  public void setUp() {
    repository = new ArrayList<>();
    testee = new MsgProcessingService(repository);
  }

  @Test
  public void givenMessage_whenToBeProcessed_thenMessageGetsRecorded() {
    testee.process(new SaleMessage());

    assertThat(repository).isNotEmpty();
  }

}