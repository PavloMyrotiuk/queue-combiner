package com.tech.task;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

public class Index {
    private static final int AMOUNT_OF_CONSUMED_MESSAGES = 1000000;

    public static void main(String[] args) throws Exception {
        SynchronousQueue<String> outputQueue = new SynchronousQueue<>();
        SynchronousQueue<String> q1 = new SynchronousQueue<>();
        SynchronousQueue<String> q2 = new SynchronousQueue<>();
        SynchronousQueue<String> q3 = new SynchronousQueue<>();
        MyrotiukCombiner combiner = new MyrotiukCombiner(outputQueue);

        combiner.addInputQueue(q1, 9.5, 1, TimeUnit.SECONDS);
        combiner.addInputQueue(q2, 0.5, 2, TimeUnit.SECONDS);
        combiner.addInputQueue(q3, 0.0005, 3, TimeUnit.SECONDS);

        Thread publisher1 = new Thread(new Publisher(q1), "publisher-1");
        Thread publisher2 = new Thread(new Publisher(q2), "publisher-2");
        Thread publisher3 = new Thread(new Publisher(q3), "publisher-3");
        Thread consumer = new Thread(new Consumer(outputQueue, AMOUNT_OF_CONSUMED_MESSAGES), "consumer-1");

        consumer.start();
        publisher1.start();
        publisher2.start();
        publisher3.start();
        Thread.sleep(10000);

        publisher1.interrupt();
        publisher2.interrupt();
        publisher3.interrupt();
        consumer.interrupt();
    }

    private static final class Publisher implements Runnable {
        private final BlockingQueue<String> queue;

        public Publisher(BlockingQueue<String> queue) {
            this.queue = queue;
        }

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                queue.offer(Thread.currentThread().getName());
            }
            System.out.println(Thread.currentThread().getName() + " finished publishing");
        }
    }

    private static final class Consumer<T> implements Runnable {
        private final SynchronousQueue<T> outputQueue;
        private final int messagesToReadFromQueue;

        private Map<T, Long> data = new HashMap<>();

        public Consumer(SynchronousQueue<T> outputQueue, int messagesToReadFromQueue) {
            this.outputQueue = outputQueue;
            this.messagesToReadFromQueue = messagesToReadFromQueue;
        }

        @Override
        public void run() {
            int consumedMessages = 0;
            while (!Thread.currentThread().isInterrupted() && consumedMessages < messagesToReadFromQueue) {

                T message;
                try {
                    message = outputQueue.take();
                    if (data.get(message) != null)
                        data.put(message, data.get(message) + 1);
                    else data.put(message, 1L);
                    consumedMessages++;
                } catch (InterruptedException e) {
                }
            }
            System.out.println("Consumed " + data);
        }
    }
}
