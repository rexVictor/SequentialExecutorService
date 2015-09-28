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
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import rex.palace.testhelp.ArgumentConverter;

import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.LongStream;

/**
 * Tests the PeriodicSequentialFuture class.
 */
public class PeriodicSequentialFutureTest {

    /**
     * The name of a method needed for 100% Coverage.
     */
    private static final String RESET_FUTURE = "resetFuture";

    /**
     * Counts how often call() got called or
     * throws an Exception if exception is not null.
     */
    private static class CallCounter implements Callable<Void> {

        /**
         * How often call() got called.
         */
        public int callCount = 0;

        /**
         * The exception to throw.
         */
        public Exception exception = null;

        /**
         * Creates a new CallCounter.
         */
        CallCounter() {
            super();
        }

        /**
         * Throws an Exception if exception is not null and
         * increments callCount otherwise.
         *
         * @return null
         * @throws Exception if exception is not null
         */
        @Override
        public Void call() throws Exception {
            if (exception != null) {
                throw exception;
            }
            callCount++;
            return null;
        }
    }

    /**
     * A Mock instance of a TimeController.
     */
    private static class MockTimeController implements TimeController {

        /**
         * The last registered TimeListener.
         */
        public TimeListener registered = null;

        /**
         * Creates a new MockTimeController.
         */
        MockTimeController() {
            super();
        }

        @Override
        public void letTimePass(long time, TimeUnit unit) {
            //does nothing
        }

        @Override
        public void register(TimeListener listener) {
            registered = listener;
        }

        @Override
        public void unregister(TimeListener listener) {
            //does nothing
        }
    }

    /**
     * A callCounter instance tests can use.
     */
    private CallCounter callCounter;

    /**
     * The PeriodicSequentialFuture to run tests on.
     */
    private SequentialScheduledFuture<Void> future;

    /**
     * The timeController future is registered to.
     */
    private TimeController timeController;

    /**
     * A MockTimeController instance.
     */
    private MockTimeController mockTimeController;

    /**
     * Empty Constructor.
     */
    public PeriodicSequentialFutureTest() {
        super();
    }

    @DataProvider(name = "nonPositiveLongs")
    public Iterator<Object[]> getNonPositiveLongs() {
        return ArgumentConverter.convert(LongStream.range(0, 10).mapToObj(lg -> -lg));
    }

    /**
     * Initializes the instance variables.
     */
    @BeforeMethod
    public void initializeInstanceVariable() {
        callCounter = new CallCounter();
        timeController = TimeControllers.getInstance();
        future = SequentialFutures.getPeriodic(
                callCounter, 10L, TimeUnit.NANOSECONDS, timeController);
        mockTimeController = new MockTimeController();
    }

    /**
     * Makes basic assertions before the actual tests are run.
     */
    @BeforeMethod(dependsOnMethods = "initializeInstanceVariable")
    public void basicAssertions() {
        Assert.assertFalse(future.isCancelled());
        Assert.assertFalse(future.isDone());
        Assert.assertEquals(callCounter.callCount, 0);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void new_nullCallable() {
        SequentialFutures.getPeriodic(
                null, 10L, TimeUnit.NANOSECONDS, TimeControllers.getNop());
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void new_nullUnit() {
        SequentialFutures.getPeriodic(
                callCounter, 10L, null, TimeControllers.getNop());
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void new_nullController() {
        SequentialFutures.getPeriodic(
                callCounter, 10L, TimeUnit.NANOSECONDS, null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
            dataProvider = "nonPositiveLongs")
    public void new_NonPositiveDuration(long duration) {
        SequentialFutures.getPeriodic(
                callCounter, duration, TimeUnit.NANOSECONDS,
                TimeControllers.getNop());
    }

    @Test
    public void new_registersAtTimeController() {
        TimeListener futureToRegister =
                SequentialFutures.getPeriodic(
                        callCounter, 10L, TimeUnit.NANOSECONDS, mockTimeController);

        Assert.assertSame(mockTimeController.registered, futureToRegister);
    }

    @Test
    public void normalRun() {
        timeController.letTimePass(100L, TimeUnit.NANOSECONDS);

        Assert.assertEquals(callCounter.callCount, 10);
        Assert.assertFalse(future.isDone());
        Assert.assertFalse(future.isCancelled());
    }

    @Test
    public void exceptionalRun() {
        timeController.letTimePass(100L, TimeUnit.NANOSECONDS);
        Assert.assertEquals(callCounter.callCount, 10);
        callCounter.exception = new FileNotFoundException();
        timeController.letTimePass(10L, TimeUnit.NANOSECONDS);

        Assert.assertEquals(callCounter.callCount, 10);
        Assert.assertTrue(future.isDone());
        Assert.assertFalse(future.isCancelled());

        timeController.letTimePass(100L, TimeUnit.NANOSECONDS);
        Assert.assertEquals(callCounter.callCount, 10);

    }

    @Test
    public void reset_notIfCancelled()
            throws NoSuchMethodException, InvocationTargetException,
            IllegalAccessException {
        timeController.letTimePass(1L, TimeUnit.NANOSECONDS);
        Assert.assertEquals(future.getDelay(TimeUnit.NANOSECONDS), 9L);

        future.cancel(true);

        Method method = future.getClass().getDeclaredMethod(RESET_FUTURE);
        method.setAccessible(true);
        method.invoke(future);

        Assert.assertEquals(future.getDelay(TimeUnit.NANOSECONDS), 9L);
    }

    @Test
    public void reset_notIfCancelledAndExceptional()
            throws NoSuchMethodException, InvocationTargetException,
            IllegalAccessException {
        callCounter.exception = new MalformedURLException();
        timeController.letTimePass(11L, TimeUnit.NANOSECONDS);
        Assert.assertEquals(future.getDelay(TimeUnit.NANOSECONDS), -1L);

        future.cancel(true);

        Method method = future.getClass().getDeclaredMethod(RESET_FUTURE);
        method.setAccessible(true);
        method.invoke(future);

        Assert.assertEquals(future.getDelay(TimeUnit.NANOSECONDS), -1L);
    }

    @Test
    public void cancel() {
        Assert.assertTrue(future.cancel(true));
        Assert.assertTrue(future.isCancelled());

        timeController.letTimePass(100L, TimeUnit.MILLISECONDS);

        Assert.assertEquals(callCounter.callCount, 0);

        Assert.assertFalse(future.cancel(true));
    }

    @Test
    public void cancel_AfterException() {
        callCounter.exception = new ClassCastException();
        timeController.letTimePass(10L, TimeUnit.NANOSECONDS);

        Assert.assertFalse(future.cancel(true));
    }

    @Test
    public void getDelay() {
        Assert.assertEquals(future.getDelay(TimeUnit.NANOSECONDS), 10L);
        timeController.letTimePass(9L, TimeUnit.NANOSECONDS);

        Assert.assertEquals(future.getDelay(TimeUnit.NANOSECONDS), 1L);

        timeController.letTimePass(11L, TimeUnit.NANOSECONDS);

        Assert.assertEquals(future.getDelay(TimeUnit.NANOSECONDS), 10L);

        timeController.letTimePass(3L, TimeUnit.NANOSECONDS);
        Assert.assertEquals(future.getDelay(TimeUnit.NANOSECONDS), 7L);
    }

    @Test(expectedExceptions = MalformedURLException.class, timeOut = 1000L)
    public void get() throws Throwable {
        callCounter.exception = new MalformedURLException();
        try {
            future.get();
        } catch (ExecutionException e) {
            throw e.getCause();
        }
    }

    @Test(expectedExceptions = TimeoutException.class, timeOut = 1000L)
    public void get_TimeOut() throws InterruptedException, ExecutionException, TimeoutException {
        callCounter.exception = new MalformedURLException();
        future.get(9L, TimeUnit.NANOSECONDS);
    }

    @Test(expectedExceptions = MalformedURLException.class, timeOut = 1000L)
    public void get_limited() throws Throwable {
        callCounter.exception = new MalformedURLException();
        try {
            future.get(10L, TimeUnit.NANOSECONDS);
        } catch (ExecutionException e) {
            throw e.getCause();
        }
    }

}

/* vim:set shiftwidth=4 softtabstop=4 expandtab: */
