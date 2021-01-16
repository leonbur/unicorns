package com.burdinov.collectors;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.*;
import java.util.stream.Collector;

public class Collectors {

    /**
     * collects the stream to a list of lists of size {@code partitionSize}
     *
     * @param partitionSize the size of the sub-lists (last sub-list may be smaller)
     * @param <T> the type of the object in the stream
     * @return a list of lists of size at most {@code partitionSize}
     */
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

    /**
     * an aggregating collector that batches the stream and runs the {@code action} on each batch.
     *
     * Note: The returned {@code Collector} is not concurrent.
     *
     * @param batchSize the size of each batch
     * @param action the action to perform on the batch
     * @param <T> the type of the object in the stream
     * @return null. the batches are discarded after handled by {@code action}
     */
    public static <T> Collector<T, ?, Void> batchingForeach(int batchSize, Consumer<? super List<T>> action) {

        class Accumulator {
            private final int maxAccumulated;
            private List<T> accumulated;
            private final Consumer<? super List<T>> action;

            public Accumulator(int maxAccumulated, Consumer<? super List<T>> action) {
                this.maxAccumulated = maxAccumulated;
                this.action = action;
                this.accumulated = new ArrayList<>();
            }

            public void add(T t) {
                accumulated.add(t);
                if (accumulated.size() >= maxAccumulated) {
                    action.accept(accumulated);
                    accumulated = new ArrayList<>();
                }
            }

            public Accumulator merge(Accumulator other) {
                if (other != null)
                    other.accumulated.forEach(this::add);
                return this;
            }

            public void finalizer() {
                if (!accumulated.isEmpty())
                    action.accept(accumulated);
            }
        }

        return new Collector<T, Accumulator, Void>() {
            @Override
            public Supplier<Accumulator> supplier() {
                return () -> new Accumulator(batchSize, action);
            }

            @Override
            public BiConsumer<Accumulator, T> accumulator() {
                return Accumulator::add;
            }

            @Override
            public BinaryOperator<Accumulator> combiner() {
                return Accumulator::merge;
            }

            @Override
            public Function<Accumulator, Void> finisher() {
                return accumulator -> {
                    accumulator.finalizer();
                    return null;
                };
            }

            @Override
            public Set<Characteristics> characteristics() {
                return EnumSet.of(Characteristics.UNORDERED);
            }
        };
    }
}
