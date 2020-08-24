package com.burdinov.collectors;


import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class PartitionForeachCollectorTest {

    @Test
    public void all_partitions_equal() {
        List<List<Integer>> result = new ArrayList<>();
        IntStream.rangeClosed(1, 30).boxed().collect(Collectors.partitionForeach(3, result::add));
        assertTrue(result.stream().allMatch(subList -> subList.size() == 3));
    }

    @Test
    public void last_partition_smaller() {
        List<List<Integer>> result = new ArrayList<>();
        IntStream.range(1, 30).boxed().collect(Collectors.partitionForeach(3, result::add));
        assertTrue(result.stream().limit(9).allMatch(subList -> subList.size() == 3)); //[[1,2,3], [4,5,6], ..., [25,26,27]]
        assertEquals(2, result.get(result.size() - 1).size()); // [28, 29]
    }

    @Test
    public void merge_partitions_when_parallel() {
        Object lock = new Object();
        List<List<Integer>> result = new ArrayList<>();
        IntStream.range(1, 30).boxed().parallel().collect(Collectors.partitionForeach(3, t -> {
            synchronized (lock) {
                result.add(t);
            }
        }));
        assertTrue(result.stream().limit(9).allMatch(subList -> subList.size() == 3));
        assertEquals(2, result.get(result.size() - 1).size());
    }
}