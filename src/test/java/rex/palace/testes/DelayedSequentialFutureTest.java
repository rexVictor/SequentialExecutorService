/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This file is part of SequentialExecutorService.
 *
 * SequentialExecutorService contains non-parallel implementations
 * for Java's ExecutorService and ScheduledExecutorService.
 * Copyright (C) 2015 Matthias Johannes Reimchen
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package rex.palace.testes;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.concurrent.CancellationException;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Tests the DelayedSequentialFuture class.
 */
public class DelayedSequentialFutureTest {

    /**
     * A stub implementation of Delayed.
     */
    private static class DelayedStub implements Delayed {

        /**
         * The current delay in nano seconds.
         */
        public long delayInNanos;

        /**
         * Creates a new DelayedStub.
         */
        DelayedStub() {
            super();
        }

        @Override
        public long getDelay(TimeUnit unit) {
            return unit.convert(delayInNanos, TimeUnit.NANOSECONDS);
        }

        @Override
        public int compareTo(Delayed other) {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * The TimeController usable by the tests.
     */
    private TimeController timeController;

    /**
     * The DelayedSequentialFuture to test.
     */
    private SequentialScheduledFuture<Void> future;

    /**
     * Empty constructor.
     */
    public DelayedSequentialFutureTest() {
        super();
    }

    /**
     * Initializes the instance variables.
     */
    @BeforeMethod
    public void initializeInstanceVariables() {
        timeController = TimeControllers.getInstance();
        future = SequentialFutures.getDelayed(() -> null, 10L,
                TimeUnit.MILLISECONDS, timeController);
    }

    @Test(expectedExceptions = CancellationException.class)
    public void cancel() {
        Assert.assertFalse(future.isCancelled());
        future.cancel(true);

        Assert.assertTrue(future.isCancelled());
        future.timePassed(10L, TimeUnit.MILLISECONDS);

    }

    @Test
    public void ranAfterDelay() throws ExecutionException, InterruptedException {
        Assert.assertFalse(future.isDone());

        future.timePassed(9L, TimeUnit.MILLISECONDS);
        Assert.assertFalse(future.isDone());
        future.timePassed(1L, TimeUnit.MILLISECONDS);


        Assert.assertTrue(future.isDone());
        Assert.assertEquals(future.get(), null);

    }

    @Test
    public void getDelay() {
        Assert.assertEquals(future.getDelay(TimeUnit.MILLISECONDS), 10L);
        future.timePassed(1L, TimeUnit.MILLISECONDS);
        Assert.assertEquals(future.getDelay(TimeUnit.MILLISECONDS), 9L);
        future.timePassed(5L, TimeUnit.MILLISECONDS);
        Assert.assertEquals(future.getDelay(TimeUnit.MILLISECONDS), 4L);
    }

    @Test
    public void compareTo() {
        DelayedStub delayed = new DelayedStub();
        delayed.delayInNanos = TimeUnit.MILLISECONDS.toNanos(5L);

        Assert.assertEquals(future.compareTo(delayed), 1L);

        future.timePassed(5L, TimeUnit.MILLISECONDS);
        Assert.assertEquals(future.compareTo(delayed), 0L);

        future.timePassed(1L, TimeUnit.MILLISECONDS);
        Assert.assertEquals(future.compareTo(delayed), -1L);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void compareTo_null() {
        future.compareTo(null);
    }

    @Test
    public void get_limited() throws InterruptedException, ExecutionException, TimeoutException {
        SequentialScheduledFuture<Integer> integerFuture
                = SequentialFutures.getDelayed(() -> 5,
                10L, TimeUnit.MILLISECONDS, timeController);
        Assert.assertEquals(integerFuture.get(11L, TimeUnit.MILLISECONDS), Integer.valueOf(5));
    }

    @Test
    public void toString_running() {
        StringBuilder regexPattern = new StringBuilder();
        regexPattern.append("SequentialScheduledFuture\\[")
                .append("TimeController = ")
                .append(".*")
                .append(", task = ")
                .append(".*")
                .append(", state = ")
                .append("running")
                .append(", remainingDelay = ")
                .append(".*")
                .append(", initialDelay = ")
                .append(".*")
                .append("\\]");
        Pattern pattern
                = Pattern.compile(regexPattern.toString());

        SequentialScheduledFuture<Void> future
                = SequentialFutures.getDelayed(
                () -> null, 10L, TimeUnit.NANOSECONDS, TimeControllers.getNop());

        Matcher matcher = pattern.matcher(future.toString());

        Assert.assertTrue(matcher.matches());
    }

}

/* vim:set shiftwidth=4 softtabstop=4 expandtab: */
