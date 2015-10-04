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

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Tests the SequentialExecutorService class.
 */
public class SequentialExecutorServiceTest {

    /**
     * A SequentialExecutorService instance used by the tests.
     */
    private SequentialExecutorService executorService;

    /**
     * Empty constructor.
     */
    public SequentialExecutorServiceTest(){
    }

    /**
     * Initializes the instance variables.
     */
    @BeforeMethod
    public void initializeInstanceVariables() {
        executorService = new SequentialExecutorService();
    }

    @Test
    public void invokeAll_Callable() throws ExecutionException, InterruptedException {
        AtomicInteger count = new AtomicInteger(0);
        Callable<Integer> callable = count::getAndIncrement;
        List<Callable<Integer>> list =
                Stream.generate(() -> callable).limit(10L).collect(Collectors.toList());
        List<Future<Integer>> futures = executorService.invokeAll(list, 0L, null);

        Assert.assertEquals(count.get(), 10);
        for (int i = 0; i < 10; i++) {
            Integer integer = futures.get(i).get();
            Assert.assertEquals(integer, Integer.valueOf(i));
        }
    }

    @Test(expectedExceptions = RejectedExecutionException.class)
    public void invokeAll_Shutdown() throws ExecutionException, InterruptedException {
        Assert.assertFalse(executorService.isShutdown());
        executorService.shutdown();
        executorService.invokeAll(new HashSet<>());
    }

    @Test(expectedExceptions = RejectedExecutionException.class)
    public void invokeAny_Shutdown() throws ExecutionException, InterruptedException {
        Assert.assertFalse(executorService.isShutdown());
        executorService.shutdown();
        executorService.invokeAny(new HashSet<>());
    }

    @Test(expectedExceptions = RejectedExecutionException.class)
    public void submit_shutdown() throws ExecutionException, InterruptedException {
        Assert.assertFalse(executorService.isShutdown());
        executorService.shutdown();
        executorService.submit(() -> null);
    }

    @Test
    public void invokeAny_Callable() throws ExecutionException, InterruptedException {
        AtomicInteger count = new AtomicInteger(0);
        Callable<Integer> callable = count::getAndIncrement;
        List<Callable<Integer>> list =
                Stream.generate(() -> callable).limit(10L).collect(Collectors.toList());
        executorService.invokeAny(list, 0L, null);

        Assert.assertEquals(count.get(), 1);
    }

    @Test
    public void invokeAny_Callable_Exception() throws ExecutionException, InterruptedException {
        AtomicInteger count = new AtomicInteger(0);
        Callable<Integer> callable = count::getAndIncrement;
        List<Callable<Integer>> list =
                Stream.generate(() -> callable).limit(10L).collect(Collectors.toList());
        list.add(0, () -> {
                throw new Exception();
            });
        executorService.invokeAny(list, 0L, null);

        Assert.assertEquals(count.get(), 1);
    }

    @Test
    public void submit_Callable_immediately() throws ExecutionException, InterruptedException {
        executorService.setExecutorServiceState(ExecutorServiceState.IMMEDIATELY);
        Callable<Integer> callable = () -> 5;
        Future<Integer> future = executorService.submit(callable);
        Assert.assertTrue(future.isDone());
        Assert.assertEquals(future.get(), Integer.valueOf(5));
    }

    @Test
    public void submit_Runnable_immediately() throws ExecutionException, InterruptedException {
        executorService.setExecutorServiceState(ExecutorServiceState.IMMEDIATELY);
        AtomicBoolean gotCalled = new AtomicBoolean(false);
        Future<Void> future = executorService.submit(() -> gotCalled.set(true));
        Assert.assertTrue(future.isDone());
        Assert.assertTrue(gotCalled.get());
    }

    @Test
    public void submit_Runnable_Result_immediately()
            throws ExecutionException, InterruptedException {
        executorService.setExecutorServiceState(ExecutorServiceState.IMMEDIATELY);
        Future<Boolean> future = executorService.submit(() -> { }, false);
        Assert.assertTrue(future.isDone());
        Assert.assertFalse(future.get());
    }

    @Test(expectedExceptions = ClassNotFoundException.class)
    public void submit_Callable_immediately_thrown() throws Throwable {
        executorService.setExecutorServiceState(ExecutorServiceState.IMMEDIATELY);
        Callable<Integer> callable = () -> { throw new ClassNotFoundException(); };
        Future<Integer> future = executorService.submit(callable);
        Assert.assertTrue(future.isDone());
        try {
            future.get();
        } catch (ExecutionException e) {
            throw e.getCause();
        }
    }

    @Test
    public void submit_Callable_onCall() throws ExecutionException, InterruptedException {
        executorService.setExecutorServiceState(ExecutorServiceState.ONCALL);
        Callable<Integer> callable = () -> 5;
        Future<Integer> future = executorService.submit(callable);
        Assert.assertFalse(future.isDone());
        Assert.assertEquals(future.get(), Integer.valueOf(5));
    }

    @Test(expectedExceptions = ClassNotFoundException.class)
    public void submit_Callable_onCall_thrown() throws Throwable {
        executorService.setExecutorServiceState(ExecutorServiceState.ONCALL);
        Callable<Integer> callable = () -> { throw new ClassNotFoundException(); };
        Future<Integer> future = executorService.submit(callable);
        Assert.assertFalse(future.isDone());
        try {
            future.get();
        } catch (ExecutionException e) {
            throw e.getCause();
        }
    }

    @Test
    public void shutdown() {
        Assert.assertFalse(executorService.isShutdown());
        executorService.shutdown();
        Assert.assertTrue(executorService.isShutdown());
        Assert.assertTrue(executorService.isJustShutdown());
        Assert.assertFalse(executorService.isShutdownNow());
    }

    @Test
    public void execute_Runnable() throws ExecutionException, InterruptedException {
        AtomicBoolean gotCalled = new AtomicBoolean(false);
        executorService.execute(() -> gotCalled.set(true));
        Assert.assertTrue(gotCalled.get());
    }

    @Test
    public void shutdownNow() {
        Callable<Void> callable = () -> null;
        List<Future<Void>> futures =
                Stream.generate(() -> callable).limit(10L)
                        .map(executorService::submitForNotFishingOnTermination)
                        .collect(Collectors.toList());
        List<Runnable> runnables = executorService.shutdownNow();

        Assert.assertTrue(executorService.isShutdown());
        Assert.assertTrue(executorService.isShutdownNow());
        Assert.assertFalse(executorService.isTerminated());

        Assert.assertEqualsNoOrder(runnables.toArray(), futures.toArray());
    }

    @Test
    public void shutdownNow_empty() {
        List<Runnable> runnables = executorService.shutdownNow();
        Assert.assertTrue(executorService.isShutdownNow());
        Assert.assertFalse(executorService.isJustShutdown());
        Assert.assertTrue(executorService.isShutdown());
        Assert.assertTrue(runnables.isEmpty());
    }

    @Test
    public void shutdownNow_Finished() {
        Callable<Void> callable = () -> null;
        Stream.generate(() -> callable).limit(10L).map(executorService::submit)
                .collect(Collectors.toList());
        List<Runnable> runnables = executorService.shutdownNow();
        Assert.assertTrue(executorService.isShutdownNow());
        Assert.assertFalse(executorService.isJustShutdown());
        Assert.assertTrue(executorService.isShutdown());
        Assert.assertTrue(runnables.isEmpty());
    }

    @Test
    public void awaitTermination_NotShutdown() throws InterruptedException {
        Assert.assertFalse(executorService.awaitTermination(10L, TimeUnit.MILLISECONDS));
    }

    @Test(expectedExceptions = InterruptedException.class)
    public void awaitTermination_interrupted() throws Exception {
        executorService.shutdown();
        TestThread testThread = new TestThread(() -> {
            Thread.currentThread().interrupt();
            executorService.awaitTermination(1L, null);
            return null;
        });

        testThread.start();
        testThread.join();
        testThread.finish();
    }

    @Test
    public void awaitTermination_empty() throws InterruptedException {
        executorService.shutdown();
        Assert.assertTrue(executorService.awaitTermination(1L, null));
    }

    @Test
    public void awaitTermination_notEmpty() throws InterruptedException {
        executorService.submitForNotFishingOnTermination(() -> null);
        executorService.shutdown();
        Assert.assertFalse(executorService.awaitTermination(1L, null));
    }

    @Test
    public void awaitTermination_inTime() throws InterruptedException, ExecutionException {
        Future<Integer> future = executorService.submitForTerminationInTime(() -> 5);
        executorService.shutdown();
        Assert.assertFalse(future.isDone());
        executorService.awaitTermination(1L, null);

        Assert.assertTrue(future.isDone());
        Assert.assertEquals(future.get(), Integer.valueOf(5));
    }

    @Test(expectedExceptions = ExecutionException.class)
    public void awaitTermination_inTime_Exception()
            throws InterruptedException, ExecutionException {
        Future<Integer> future =
                executorService.submitForTerminationInTime(() -> { throw new Exception(); });
        executorService.shutdown();
        Assert.assertFalse(future.isDone());
        executorService.awaitTermination(1L, null);

        Assert.assertTrue(future.isDone());
        future.get();
    }

    @Test
    public void isTerminated_notShutdown() {
        Assert.assertFalse(executorService.isTerminated());
    }

    @Test
    public void isTerminated_shutdown_empty() {
        executorService.shutdown();
        Assert.assertTrue(executorService.isTerminated());
    }

    @Test
    public void toString_shutdown() {
        executorService.shutdown();
        StringBuilder regexPattern = new StringBuilder();
        regexPattern.append("SequentialExecutorService\\[")
                .append("SHUTDOWN")
                .append(", submittedTasks = ")
                .append("\\d*")
                .append(", finishedTasks = ")
                .append("\\d*")
                .append("\\]");
        Pattern pattern = Pattern.compile(regexPattern.toString());

        Matcher matcher = pattern.matcher(executorService.toString());

        Assert.assertTrue(matcher.matches());
    }

    @Test
    public void toString_ready() {
        StringBuilder regexPattern = new StringBuilder();
        regexPattern.append("SequentialExecutorService\\[")
                .append("READY")
                .append(", submittedTasks = ")
                .append("\\d*")
                .append(", finishedTasks = ")
                .append("\\d*")
                .append("\\]");
        Pattern pattern = Pattern.compile(regexPattern.toString());

        Matcher matcher = pattern.matcher(executorService.toString());

        Assert.assertTrue(matcher.matches());
    }

}

/* vim:set shiftwidth=4 softtabstop=4 expandtab: */
