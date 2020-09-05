package com.burdinov.collectors;


import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;


public class PartitionForeachCollectorTest {

    @Test
    public void all_partitions_equal() {
        List<List<Integer>> result = new ArrayList<>();
        IntStream.rangeClosed(1, 9).boxed().collect(Collectors.partitionForeach(3, result::add));
        assertThat(result).allSatisfy(sublist -> assertThat(sublist).hasSize(3));
    }

    @Test
    public void last_partition_smaller() {
        List<List<Integer>> result = new ArrayList<>();
        IntStream.range(1, 9).boxed().collect(Collectors.partitionForeach(3, result::add));
        List<List<Integer>> expected = Arrays.asList(
                Arrays.asList(1, 2, 3),
                Arrays.asList(4, 5, 6),
                Arrays.asList(7, 8)
        );
        assertThat(result).containsOnlyOnceElementsOf(expected);
    }

    @Test
    public void merge_partitions_when_parallel() {
        Object lock = new Object();
        List<List<Integer>> result = new ArrayList<>();
        IntStream.range(1, 9).boxed().parallel().collect(Collectors.partitionForeach(3, t -> {
            synchronized (lock) {
                result.add(t);
            }
        }));
        List<List<Integer>> expected = Arrays.asList(
                Arrays.asList(1, 2, 3),
                Arrays.asList(5, 6, 7),
                Arrays.asList(4, 8)
        );
        assertThat(result).containsOnlyOnceElementsOf(expected);
    }
}