package com.tech.task;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

import static org.fest.assertions.api.Assertions.assertThat;

public class MyrotiukCombinerTest {

    private final MyrotiukCombiner<String> testedInstance = new MyrotiukCombiner<>(new SynchronousQueue());

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void shouldThrowIllegalArgumentExceptionIfPriorityLoEtoZero() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Priority can't be less than zero");
        testedInstance.addInputQueue(new SynchronousQueue(), -1, 54, TimeUnit.SECONDS);
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionIfIsEmptyTimeoutLessThanZero() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("isEmptyTimeout can't be less than zero");
        testedInstance.addInputQueue(new SynchronousQueue(), 6.8, -40, TimeUnit.SECONDS);
    }

    @Test
    public void shouldThrowCombinerExceptionIfPriorityIsZero() throws Exception {
        thrown.expect(Combiner.CombinerException.class);
        thrown.expectMessage("Priority can't be zero");
        testedInstance.addInputQueue(new SynchronousQueue(), 0, 45, TimeUnit.SECONDS);
    }

    @Test
    public void shouldReturnTrueAfterAddingQueue() throws Exception {
        BlockingQueue queue = new SynchronousQueue();
        testedInstance.addInputQueue(queue, 2.0, 12, TimeUnit.SECONDS);
        assertThat(testedInstance.hasInputQueue(queue)).isTrue();
    }

    @Test
    public void shouldReturnFalseAfterDeletingQueue() throws Exception {
        BlockingQueue queue = new SynchronousQueue();
        testedInstance.addInputQueue(queue, 2.0, 12, TimeUnit.SECONDS);
        testedInstance.removeInputQueue(queue);
        assertThat(testedInstance.hasInputQueue(queue)).isFalse();
    }
}