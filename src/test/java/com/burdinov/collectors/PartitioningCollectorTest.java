package com.burdinov.collectors;

import org.junit.Test;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.Assert.*;

public class PartitioningCollectorTest {

    @Test
    public void all_partitions_equal() {
        List<List<Integer>> collected = IntStream.rangeClosed(1, 30).boxed().collect(Collectors.toPartitionedList(3));
        assertTrue(collected.stream().allMatch(subList -> subList.size() == 3));
    }

    @Test
    public void last_partition_smaller() {
        List<List<Integer>> collected = IntStream.range(1, 30).boxed().collect(Collectors.toPartitionedList(3));
        assertTrue(collected.stream().limit(9).allMatch(subList -> subList.size() == 3));
        assertEquals(2, collected.get(collected.size() - 1).size());
    }

    @Test
    public void merge_partitions_when_parallel() {
        List<List<Integer>> collected = IntStream.range(1, 30).boxed().parallel().collect(Collectors.toPartitionedList(3));
        assertTrue(collected.stream().limit(9).allMatch(subList -> subList.size() == 3));
        assertEquals(2, collected.get(collected.size() - 1).size());
    }
}