package com.psat.reporting;

import com.psat.sales.SaleMessage;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface ReportGenerator<S extends SaleMessage> {

  default Optional<String> generate(List<S> sales, S totalSales) {
    return Optional.empty();
  }

  default Optional<String> generate(List<S> sales) {
    return generate(sales, null);
  }

  default Optional<String> generate(Stream<S> stream,
                                    Function<S, String> reportEntryGenerator,
                                    String header,
                                    String footer) {
    String reportBody = stream
            .map(reportEntryGenerator)
            .collect(
                    Collectors.joining("\n", header, footer)
            );

    return Optional.ofNullable(reportBody);
  }
}
