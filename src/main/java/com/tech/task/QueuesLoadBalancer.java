package com.tech.task;

import org.javatuples.Triplet;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class QueuesLoadBalancer<T> {

    private final Combiner combiner;

    private final Lock lock = new ReentrantLock();
    private Thread queuesLoadBalancer;

    public QueuesLoadBalancer(Combiner combiner) {
        this.combiner = combiner;
    }

    void rebalanceQueues(Map<BlockingQueue<T>, Triplet<Range, Long, TimeUnit>> inputQueues, SynchronousQueue<T> outputQueue) {
        lock.lock();
        try {
            if (queuesLoadBalancer != null) {
                queuesLoadBalancer.interrupt();
            }

            if (inputQueues.size() > 0) {
                blockUntilPreviousLoadBalancerIsAlive();
                queuesLoadBalancer = new Thread(new BalancerTask(inputQueues, outputQueue), "load-balancer");
                queuesLoadBalancer.start();
            }
        } finally {
            lock.unlock();
        }
    }

    private void blockUntilPreviousLoadBalancerIsAlive() {
        while (queuesLoadBalancer != null && queuesLoadBalancer.isAlive()) {
        }
    }

    private class BalancerTask implements Runnable {
        private final Map<BlockingQueue<T>, Triplet<Range, Long, TimeUnit>> inputQueues;
        private final SynchronousQueue<T> outputQueue;

        BalancerTask(Map<BlockingQueue<T>, Triplet<Range, Long, TimeUnit>> inputQueues, SynchronousQueue<T> outputQueue) {
            this.inputQueues = inputQueues;
            this.outputQueue = outputQueue;
        }

        @Override
        public void run() {
            Random random = new Random();
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    double randomValue = random.nextDouble();
                    for (Map.Entry<BlockingQueue<T>, Triplet<Range, Long, TimeUnit>> queueRange : inputQueues.entrySet()) {
                        BlockingQueue<T> queue = queueRange.getKey();
                        Range range = queueRange.getValue().getValue0();
                        Long timeout = queueRange.getValue().getValue1();
                        TimeUnit timeUnit = queueRange.getValue().getValue2();
                        if (range.inRange(randomValue)) {
                            T valueFromQueue = queue.poll(timeout, timeUnit);
                            if (valueFromQueue != null) {
                                outputQueue.offer(valueFromQueue);
                            } else {
                                new Thread(() -> {
                                    try {
                                        combiner.removeInputQueue(queue);
                                    } catch (Combiner.CombinerException e) {
                                        //NOP can be logged
                                    }
                                }).start();
                                Thread.currentThread().interrupt();
                            }
                        }
                    }
                } catch (InterruptedException e) {
                    System.out.println(Thread.currentThread().getName()+ " " + Thread.currentThread().getId() + " is interrupted");
                }
            }
        }
    }
}
