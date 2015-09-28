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
import rex.palace.testhelp.TestThread;

import java.sql.SQLException;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Tests the AbstractSequentialFuture class.
 */
public class AbstractSequentialFutureTest {

    /**
     * Counts how often call() is called or throws an Exception if set.
     */
    private static class CallCounter implements Callable<Integer> {

        /**
         * How often call() got called.
         */
        public int callCount;

        /**
         * The exception to throw.
         */
        public Exception exception;

        /**
         * Creates a new CallCounter.
         */
        CallCounter() {
            super();
        }

        /**
         * Throws exception if it is not null, otherwise returns how often this method got called.
         *
         * @return how often this method got called
         * @throws Exception if exception is not null
         */
        @Override
        public Integer call() throws Exception {
            if (exception != null) {
                throw exception;
            }
            return ++callCount;
        }
    }

    /**
     * The CallCounter instance used for the tests.
     */
    private CallCounter callCounter;

    /**
     * The AbstractSequentialFuture to run tests on.
     */
    private AbstractSequentialFuture<Integer> future;

    /**
     * Empty Constructor.
     */
    public AbstractSequentialFutureTest() {
        super();
    }


    /**
     * Initializes the instance variables.
     */
    @BeforeMethod
    public void initializeInstanceVariables() {
        callCounter = new CallCounter();
        future = new AbstractSequentialFuture<Integer>(callCounter) { };
    }

    /**
     * Asserts that the future field is in a correct state before running actual tests.
     */
    @BeforeMethod(dependsOnMethods = "initializeInstanceVariables")
    public void initAssertions() {
        Assert.assertFalse(future.hasRun());
        Assert.assertFalse(future.isCancelled());
        Assert.assertFalse(future.isExceptionHappened());
        Assert.assertFalse(future.isDone());
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void new_nullCallable() {
        new AbstractSequentialFuture<Void>(null) { };
    }

    @Test(expectedExceptions = CancellationException.class)
    public void cancel() {
        Assert.assertTrue(future.cancel(true));
        Assert.assertTrue(future.isCancelled());
        Assert.assertTrue(future.isDone());
        try {
            future.run();
        } catch (CancellationException e) {
            Assert.assertFalse(future.hasRun());
            Assert.assertFalse(future.isExceptionHappened());
            Assert.assertEquals(callCounter.callCount, 0);
            throw e;
        }
    }

    @Test
    public void cancel_afterRun() {
        future.run();

        Assert.assertFalse(future.cancel(true));
        Assert.assertFalse(future.isCancelled());
        Assert.assertTrue(future.isDone());
        Assert.assertTrue(future.hasRun());
        Assert.assertFalse(future.isExceptionHappened());
        Assert.assertEquals(callCounter.callCount, 1);
    }

    @Test
    public void run_normal() throws ExecutionException, InterruptedException, TimeoutException {
        future.run();

        Assert.assertTrue(future.hasRun());
        Assert.assertTrue(future.isDone());
        Assert.assertFalse(future.isCancelled());
        Assert.assertFalse(future.isExceptionHappened());
        Assert.assertEquals(callCounter.callCount, 1);
        Assert.assertEquals(future.get(), Integer.valueOf(1));
        Assert.assertEquals(future.get(10L, TimeUnit.MILLISECONDS), Integer.valueOf(1));
    }

    @Test(expectedExceptions = SQLException.class)
    public void run_exceptional() throws Throwable {
        callCounter.exception = new SQLException();
        future.run();

        Assert.assertTrue(future.hasRun());
        Assert.assertTrue(future.isDone());
        Assert.assertFalse(future.isCancelled());
        Assert.assertEquals(callCounter.callCount, 0);
        Assert.assertTrue(future.isExceptionHappened());
        try {
            future.get();
        } catch (ExecutionException e) {
            throw e.getCause();
        }
    }

    @Test(expectedExceptions = InterruptedException.class)
    public void get_interrupted() throws Exception {
        TestThread testThread = new TestThread(() -> {
            Thread.currentThread().interrupt();
            future.get();
            return null;
        });
        testThread.start();
        testThread.join();
        testThread.finish();
    }

    @Test
    public void toString_cancelled() {
        Pattern pattern
                = Pattern.compile(
                "SequentialFuture \\[task = .*, state = cancelled\\]");

        future.cancel(true);
        Matcher matcher = pattern.matcher(future.toString());

        Assert.assertTrue(matcher.matches());
    }

    @Test
    public void toString_done_exceptional() {
        Pattern pattern
                = Pattern.compile(
                "SequentialFuture \\[task = .*, state = done failure: .*\\]");

        callCounter.exception = new ClassCastException();
        future.run();
        Matcher matcher = pattern.matcher(future.toString());

        Assert.assertTrue(matcher.matches());
    }

    @Test
    public void toString_done_regular() {
        Pattern pattern
                = Pattern.compile(
                "SequentialFuture \\[task = .*, state = done result: .*\\]");

        future.run();
        Matcher matcher = pattern.matcher(future.toString());

        Assert.assertTrue(matcher.matches());
    }

    @Test
    public void toString_notDone() {
        Pattern pattern
                = Pattern.compile(
                "SequentialFuture \\[task = .*, state = running\\]");

        Matcher matcher = pattern.matcher(future.toString());

        Assert.assertTrue(matcher.matches());
    }

}

/* vim:set shiftwidth=4 softtabstop=4 expandtab: */
