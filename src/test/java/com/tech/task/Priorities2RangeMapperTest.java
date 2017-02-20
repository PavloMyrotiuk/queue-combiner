package com.tech.task;

import org.javatuples.Triplet;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

import static org.fest.assertions.api.Assertions.assertThat;

public class Priorities2RangeMapperTest {

    private Priorities2RangeMapper testedInstance = new Priorities2RangeMapper();

    @Test
    public void shouldReturnMapWithSameSize() {
        BlockingQueue<String> queue = new SynchronousQueue<>();
        double priority = 5.0;
        long timeout = 4L;
        TimeUnit timeUnit = TimeUnit.SECONDS;

        Map<BlockingQueue<String>, Triplet<Double, Long, TimeUnit>> given = new HashMap<>();
        given.put(queue, new Triplet<>(priority, timeout, timeUnit));
        Map<BlockingQueue<String>, Triplet<Range, Long, TimeUnit>>  result = testedInstance.apply(given);
        assertThat(result).hasSize(1);
    }

    @Test
    public void shouldReturnMapWithSameTimeoutAndTimeUnit() {
        BlockingQueue<String> queue = new SynchronousQueue<>();
        double priority = 5.0;
        long timeout = 4L;
        TimeUnit timeUnit = TimeUnit.SECONDS;

        Map<BlockingQueue<String>, Triplet<Double, Long, TimeUnit>> given = new HashMap<>();
        given.put(queue, new Triplet<>(priority, timeout, timeUnit));
        Map<BlockingQueue<String>, Triplet<Range, Long, TimeUnit>>  result = testedInstance.apply(given);
        assertThat(result.get(queue).getValue1()).isEqualTo(timeout);
        assertThat(result.get(queue).getValue2()).isEqualTo(timeUnit);
    }

    @Test
    public void shouldReturnRangeFrom0To1ForOneElement() {
        BlockingQueue<String> queue = new SynchronousQueue<>();
        double priority = 5.0;
        long timeout = 4L;
        TimeUnit timeUnit = TimeUnit.SECONDS;

        Map<BlockingQueue<String>, Triplet<Double, Long, TimeUnit>> given = new HashMap<>();
        given.put(queue, new Triplet<>(priority, timeout, timeUnit));
        Map<BlockingQueue<String>, Triplet<Range, Long, TimeUnit>>  result = testedInstance.apply(given);
        assertThat(result.get(queue).getValue0()).isEqualTo(new Range(0.0, 1.0));
    }

    @Test
    public void shouldEquallySplitRangeBetweenQueuesWithSamePriority() {
        BlockingQueue<String> queue = new SynchronousQueue<>();
        BlockingQueue<String> queue2 = new SynchronousQueue<>();

        Map<BlockingQueue<String>, Triplet<Double, Long, TimeUnit>> given = new HashMap<>();
        given.put(queue, new Triplet<>(5.0, 4L, TimeUnit.SECONDS));
        given.put(queue2, new Triplet<>(5.0, 4L, TimeUnit.SECONDS));
        Map<BlockingQueue<String>, Triplet<Range, Long, TimeUnit>>  result = testedInstance.apply(given);
        assertThat(result.get(queue).getValue0().getTo() - result.get(queue).getValue0().getFrom()).isEqualTo(result.get(queue2).getValue0().getTo() - result.get(queue2).getValue0().getFrom());
    }

    @Test
    public void shouldCorrectlySplitRangeBetweenTwoQueuesWithDifferentPriorities() {
        BlockingQueue<String> queue = new SynchronousQueue<>();
        BlockingQueue<String> queue2 = new SynchronousQueue<>();

        Map<BlockingQueue<String>, Triplet<Double, Long, TimeUnit>> given = new HashMap<>();
        given.put(queue, new Triplet<>(9.5, 4L, TimeUnit.SECONDS));
        given.put(queue2, new Triplet<>(0.5, 4L, TimeUnit.SECONDS));
        Map<BlockingQueue<String>, Triplet<Range, Long, TimeUnit>>  result = testedInstance.apply(given);
        assertThat(result.get(queue2).getValue0()).isEqualTo(new Range(0.0, 0.05));
        assertThat(result.get(queue).getValue0()).isEqualTo(new Range(0.05, 1.0));
    }

    @Test
    public void shouldCorrectlySplitRangeBetweenThreeQueuesWithDifferentPriorities() {
        BlockingQueue<String> queue = new SynchronousQueue<>();
        BlockingQueue<String> queue2 = new SynchronousQueue<>();
        BlockingQueue<String> queue3 = new SynchronousQueue<>();

        Map<BlockingQueue<String>, Triplet<Double, Long, TimeUnit>> given = new HashMap<>();
        given.put(queue, new Triplet<>(3., 4L, TimeUnit.SECONDS));
        given.put(queue2, new Triplet<>(5., 4L, TimeUnit.SECONDS));
        given.put(queue3, new Triplet<>(2., 4L, TimeUnit.SECONDS));
        Map<BlockingQueue<String>, Triplet<Range, Long, TimeUnit>>  result = testedInstance.apply(given);
        assertThat(result.get(queue3).getValue0()).isEqualTo(new Range(0.0, 0.2));
        assertThat(result.get(queue).getValue0()).isEqualTo(new Range(0.2, 0.5));
        assertThat(result.get(queue2).getValue0()).isEqualTo(new Range(0.5, 1.0));
    }
}