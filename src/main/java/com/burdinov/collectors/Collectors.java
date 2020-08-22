package com.burdinov.collectors;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class Collectors {

    public static <T> Collector<T, ?, List<List<T>>> toPartitionedList(int partitionSize) {
        class PartitionedList {
            private final int chunkSize;

            private ArrayList<T> chunk;
            private final List<List<T>> accumulated;

            public PartitionedList(int chunkSize) {
                this.chunkSize = chunkSize;
                this.chunk = new ArrayList<>(chunkSize);
                this.accumulated = new ArrayList<>();
            }

            public void add(T t) {
                chunk.add(t);
                if (chunk.size() >= chunkSize) {
                    accumulated.add(chunk);
                    chunk = new ArrayList<>();
                }
            }

            public PartitionedList merge(PartitionedList other) {
                this.accumulated.addAll(other.accumulated);
                other.chunk.forEach(this::add);
                return this;
            }

            public List<List<T>> finalizer() {
                if (!chunk.isEmpty())
                    accumulated.add(chunk);
                return accumulated;
            }


        }

        return new Collector<T, PartitionedList, List<List<T>>>() {
            @Override
            public Supplier<PartitionedList> supplier() {
                return () -> new PartitionedList(partitionSize);
            }

            @Override
            public BiConsumer<PartitionedList, T> accumulator() {
                return PartitionedList::add;
            }

            @Override
            public BinaryOperator<PartitionedList> combiner() {
                return PartitionedList::merge;
            }

            @Override
            public Function<PartitionedList, List<List<T>>> finisher() {
                return PartitionedList::finalizer;
            }

            @Override
            public Set<Characteristics> characteristics() {
                return EnumSet.of(Characteristics.UNORDERED);
            }
        };
    }


}
