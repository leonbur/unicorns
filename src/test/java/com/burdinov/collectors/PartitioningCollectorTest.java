package com.burdinov.collectors;


import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;


public class PartitioningCollectorTest {

    @Test
    public void all_partitions_equal() {
        List<List<Integer>> collected = IntStream.rangeClosed(1, 9).boxed().collect(Collectors.toPartitionedList(3));
        assertThat(collected).allSatisfy(sublist -> assertThat(sublist).hasSize(3));
    }

    @Test
    public void last_partition_smaller() {
        List<List<Integer>> collected = IntStream.range(1, 9).boxed().collect(Collectors.toPartitionedList(3));
        List<List<Integer>> expected = Arrays.asList(
                Arrays.asList(1, 2, 3),
                Arrays.asList(4, 5, 6),
                Arrays.asList(7, 8)
        );
        assertThat(collected).containsOnlyOnceElementsOf(expected);
    }

    @Test
    public void merge_partitions_when_parallel() {
        List<List<Integer>> collected = IntStream.range(1, 9).boxed().parallel().collect(Collectors.toPartitionedList(3));
        List<List<Integer>> expected = Arrays.asList(
                Arrays.asList(1, 2, 3),
                Arrays.asList(5, 6, 7),
                Arrays.asList(4, 8)
        );
        assertThat(collected).containsOnlyOnceElementsOf(expected);
    }
}