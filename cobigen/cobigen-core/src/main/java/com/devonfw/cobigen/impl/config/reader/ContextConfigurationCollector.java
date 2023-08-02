package com.devonfw.cobigen.impl.config.reader;

import com.devonfw.cobigen.impl.config.ContextConfiguration;

import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class ContextConfigurationCollector implements Collector<ContextConfiguration, ContextConfiguration, ContextConfiguration> {

  public static ContextConfigurationCollector toContextConfiguration() {
    return new ContextConfigurationCollector();
  }

  @Override
  public Supplier<ContextConfiguration> supplier() {
    return ContextConfiguration::new;
  }

  @Override
  public BiConsumer<ContextConfiguration, ContextConfiguration> accumulator() {
    return ContextConfiguration::merge;
  }

  @Override
  public BinaryOperator<ContextConfiguration> combiner() {
    return ContextConfiguration::merge;
  }

  @Override
  public Function<ContextConfiguration, ContextConfiguration> finisher() {
    return null;
  }

  @Override
  public Set<Characteristics> characteristics() {
    return Set.of(Characteristics.CONCURRENT, Characteristics.UNORDERED, Characteristics.IDENTITY_FINISH);
  }
}
