package com.tech.task;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

public class MyrotiukCombiner<T> extends Combiner<T> {

    protected MyrotiukCombiner(SynchronousQueue<T> outputQueue) {
        super(outputQueue);
    }

    @Override
    public void addInputQueue(BlockingQueue<T> queue, double priority, long isEmptyTimeout, TimeUnit timeUnit) throws CombinerException {

    }

    @Override
    public void removeInputQueue(BlockingQueue<T> queue) throws CombinerException {

    }

    @Override
    public boolean hasInputQueue(BlockingQueue<T> queue) {
        return false;
    }
}
