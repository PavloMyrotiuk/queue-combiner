package com.tech.task;

import org.javatuples.Triplet;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

public class MyrotiukCombiner<T> extends Combiner<T> {

    private Map<BlockingQueue<T>, Triplet<Double, Long, TimeUnit>> queuePriorityTimeoutTimeUnit;
    private Priorities2RangeMapper<T> mapper;
    private final QueuesLoadBalancer<T> queuesLoadBalancer = new QueuesLoadBalancer<>(this);

    protected MyrotiukCombiner(SynchronousQueue<T> outputQueue) {
        super(outputQueue);
        queuePriorityTimeoutTimeUnit = new ConcurrentHashMap<>();
        mapper = new Priorities2RangeMapper<>();
    }

    @Override
    public void addInputQueue(BlockingQueue<T> queue, double priority, long isEmptyTimeout, TimeUnit timeUnit) throws CombinerException {
        if (priority < 0) throw new IllegalArgumentException("Priority can't be less than zero");
        if (isEmptyTimeout < 0) throw new IllegalArgumentException("isEmptyTimeout can't be less than zero");
        if (priority == 0) throw new CombinerException("Priority can't be zero");
        Triplet<Double, Long, TimeUnit> insertedElement = queuePriorityTimeoutTimeUnit.putIfAbsent(queue, new Triplet<>(priority, isEmptyTimeout, timeUnit));
        if (insertedElement == null) {
            recalculatePriorities();
        }
    }

    @Override
    public void removeInputQueue(BlockingQueue<T> queue) throws CombinerException {
        Triplet<Double, Long, TimeUnit> deletedQueueValue = queuePriorityTimeoutTimeUnit.remove(queue);
        if (deletedQueueValue != null) {
            recalculatePriorities();
        }
    }

    @Override
    public boolean hasInputQueue(BlockingQueue<T> queue) {
        return queuePriorityTimeoutTimeUnit.containsKey(queue);
    }

    private void recalculatePriorities() {
        queuesLoadBalancer.rebalanceQueues(mapper.apply(queuePriorityTimeoutTimeUnit), outputQueue);
    }
}
