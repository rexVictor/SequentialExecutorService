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
import rex.palace.testhelp.CallCounter;
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
public class SequentialFutureTest {

    @FunctionalInterface
    interface FutureFactory {

        <T> SequentialFuture<T> build(Callable<T> callable);

    }

    private static final class FutureFactory0 implements FutureFactory {

        private final FutureFactory futureFactory;

        private FutureFactory0(FutureFactory futureFactory) {
            this.futureFactory = futureFactory;
        }

        @Override
        public <T> SequentialFuture<T> build(Callable<T> callable) {
            return futureFactory.build(callable);
        }

        @Override
        public String toString() {
            return build(() -> null).getClass().getSimpleName() + "Factory";
        }

    }

    private CallCounter callCounter; /* = new CallCounter(); */

    public SequentialFutureTest() {
        super();
    }

    @BeforeMethod
    public void initializeInstanceVariables() {
        callCounter = new CallCounter();
    }

    @DataProvider(name = "cancellableFutureFactories")
    public Object[][] getCancellableFutureFactories() {
        return new FutureFactory[][]{
                {new FutureFactory0(SequentialFutures::getOnCall)},
                {new FutureFactory0(SequentialFutures::getNeverDone)}
        };
    }


    @DataProvider(name = "futureFactories")
    public Object[][] getFutureFactories() {
        return new FutureFactory[][]{
                {new FutureFactory0(SequentialFutures::getImmediately)},
                {new FutureFactory0(SequentialFutures::getOnCall)},
                {new FutureFactory0(SequentialFutures::getNeverDone)}
        };
    }

    @DataProvider(name = "futureFactoriesAndBooleans")
    public Object[][] getFutureFactoriesCrossBoolean() {
        return new Object[][]{
                {new FutureFactory0(SequentialFutures::getImmediately), true},
                {new FutureFactory0(SequentialFutures::getOnCall), true},
                {new FutureFactory0(SequentialFutures::getNeverDone), true},
                {new FutureFactory0(SequentialFutures::getImmediately), false},
                {new FutureFactory0(SequentialFutures::getOnCall), false},
                {new FutureFactory0(SequentialFutures::getNeverDone), false}
        };
    }

    @Test(expectedExceptions = NullPointerException.class,
            dataProvider = "futureFactories")
    public final void getInstance_nullCallable(FutureFactory factory) {
        factory.build(null);
    }

    @Test(dataProvider = "futureFactories")
    public void cancel_afterRun(FutureFactory factory) {
        SequentialFuture<Integer> future = factory.build(callCounter);
        if (!future.hasRun()) {
            future.run();
        }

        Assert.assertFalse(future.cancel(true));
        Assert.assertFalse(future.isCancelled());
        Assert.assertTrue(future.isDone());
        Assert.assertTrue(future.hasRun());
        Assert.assertFalse(future.isExceptionHappened());
        Assert.assertEquals(callCounter.getCallCount(), 1);
    }

    @Test(dataProvider = "futureFactories")
    public void run_normal(FutureFactory factory)
            throws ExecutionException, InterruptedException, TimeoutException {
        SequentialFuture<Integer> future = factory.build(callCounter);
        if (!future.hasRun()){
            future.run();
        }

        Assert.assertTrue(future.hasRun());
        Assert.assertTrue(future.isDone());
        Assert.assertFalse(future.isCancelled());
        Assert.assertFalse(future.isExceptionHappened());
        Assert.assertEquals(callCounter.getCallCount(), 1);
        Assert.assertEquals(future.get(), Integer.valueOf(1));
        Assert.assertEquals(future.get(10L, TimeUnit.MILLISECONDS),
                Integer.valueOf(1));
    }

    @Test(expectedExceptions = SQLException.class,
            dataProvider = "futureFactories")
    public void run_exceptional(FutureFactory factory) throws Throwable {
        callCounter.setException(new SQLException());
        SequentialFuture<Integer> future = factory.build(callCounter);
        if (!future.hasRun()) {
            future.run();
        }

        Assert.assertTrue(future.hasRun());
        Assert.assertTrue(future.isDone());
        Assert.assertFalse(future.isCancelled());
        Assert.assertEquals(callCounter.getCallCount(), 0);
        Assert.assertTrue(future.isExceptionHappened());
        try {
            future.get();
        } catch (ExecutionException e) {
            throw e.getCause();
        }
    }

    @Test(dataProvider = "futureFactoriesAndBooleans")
    public void isCancelled(FutureFactory factory, boolean interrupt) {
        SequentialFuture<Integer> future = factory.build(callCounter);
        Assert.assertFalse(future.isCancelled());

        if (future.cancel(true)) {
            Assert.assertTrue(future.isCancelled());
        }

    }

    @Test(dataProvider = "futureFactories")
    public void isDone_afterRun(FutureFactory factory) {
        SequentialFuture<Integer> future = factory.build(callCounter);
        if (!future.hasRun()) {
            future.run();
        }
        Assert.assertTrue(future.isDone());
    }

    @Test(dataProvider = "futureFactories")
    public void isDone_afterCancel(FutureFactory factory) {
        SequentialFuture<Integer> future = factory.build(callCounter);
        future.cancel(true);
        Assert.assertTrue(future.isDone());
    }

    @Test(dataProvider = "cancellableFutureFactories",
            expectedExceptions = CancellationException.class)
    public void get_cancelled(FutureFactory factory)
            throws ExecutionException, InterruptedException {
        SequentialFuture<Integer> future = factory.build(callCounter);
        future.cancel(true);
        future.get();
    }

    @Test(dataProvider = "futureFactories")
    public void toString_common(FutureFactory factory) {
        StringBuilder stateRegex = new StringBuilder();
        stateRegex.append("(cancelled)|(running)|")
                .append("(done (failure|result)")
                .append(": .*)");
        StringBuilder regExPattern = new StringBuilder();
        regExPattern.append("^SequentialFuture\\[task=")
                .append(".*,")
                .append("state=(")
                .append(stateRegex)
                .append(")\\]$");

        SequentialFuture<Integer> future = factory.build(callCounter);
        Pattern pattern = Pattern.compile(regExPattern.toString());
        Matcher matcher = pattern.matcher(future.toString());

        Assert.assertTrue(matcher.matches());
    }

    @Test(dataProvider = "cancellableFutureFactories")
    public void toString_cancelled(FutureFactory factory) {
        SequentialFuture<Integer> future = factory.build(callCounter);
        future.cancel(true);

        StringBuilder regExPattern = new StringBuilder();
        regExPattern.append("^SequentialFuture\\[task=")
                .append(".*,")
                .append("state=")
                .append("cancelled")
                .append("\\]$");

        Pattern pattern = Pattern.compile(regExPattern.toString());
        Matcher matcher = pattern.matcher(future.toString());

        Assert.assertTrue(matcher.matches());
    }

    @Test(dataProvider = "futureFactories")
    public void toString_exception(FutureFactory factory) {
        callCounter.setException(new IllegalArgumentException());
        SequentialFuture<Integer> future = factory.build(callCounter);
        if (!future.hasRun()) {
            future.run();
        }

        StringBuilder regExPattern = new StringBuilder();
        regExPattern.append("^SequentialFuture\\[task=")
                .append(".*,")
                .append("state=")
                .append("done failure: ")
                .append(IllegalArgumentException.class.getName())
                .append("\\]$");

        Pattern pattern = Pattern.compile(regExPattern.toString());
        Matcher matcher = pattern.matcher(future.toString());

        Assert.assertTrue(matcher.matches());

    }

    @Test(dataProvider = "futureFactories",
            expectedExceptions = InterruptedException.class)
    public void get_interrupted(FutureFactory factory) throws Exception {
        SequentialFuture<Integer> future = factory.build(callCounter);
        TestThread thread = new TestThread(() -> {
            Thread.currentThread().interrupt();
            return future.get();
        }
        );
        thread.start();
        thread.join();
        thread.finish();
    }

    @Test(dataProvider = "futureFactories")
    public void cancel_twice(FutureFactory factory) {
        SequentialFuture<?> future = factory.build(callCounter);
        future.cancel(true);
        Assert.assertFalse(future.cancel(true));
    }

}

/* vim:set shiftwidth=4 softtabstop=4 expandtab: */
