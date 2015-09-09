package rex.palace.testes;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import rex.palace.testhelp.TestThread;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by rex on 9/8/15.
 */
public class SequentialExecutorServiceTest {

    private SequentialExecutorService executorService;

    public SequentialExecutorServiceTest(){
    }

    @BeforeMethod
    public void initializeInstanceVariables() {
        executorService = new SequentialExecutorService();
    }

    @Test
    public void invokeAll_Callable() throws ExecutionException, InterruptedException {
        AtomicInteger count = new AtomicInteger(0);
        Callable<Integer> callable = () -> count.getAndIncrement();
        List<Callable<Integer>> list = Stream.generate(() -> callable).limit(10).collect(Collectors.toList());
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
        executorService.invokeAll(Collections.EMPTY_SET);
    }

    @Test(expectedExceptions = RejectedExecutionException.class)
    public void invokeAny_Shutdown() throws ExecutionException, InterruptedException {
        Assert.assertFalse(executorService.isShutdown());
        executorService.shutdown();
        executorService.invokeAny(Collections.EMPTY_SET);
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
        Callable<Integer> callable = () -> count.getAndIncrement();
        List<Callable<Integer>> list = Stream.generate(() -> callable).limit(10).collect(Collectors.toList());
        Integer result = executorService.invokeAny(list, 0L, null);

        Assert.assertEquals(count.get(), 1);
    }

    @Test
    public void invokeAny_Callable_Exception() throws ExecutionException, InterruptedException {
        AtomicInteger count = new AtomicInteger(0);
        Callable<Integer> callable = () -> count.getAndIncrement();
        List<Callable<Integer>> list = Stream.generate(() -> callable).limit(10).collect(Collectors.toList());
        list.add(0, () -> {
            throw new Exception();
        });
        Integer result = executorService.invokeAny(list, 0L, null);

        Assert.assertEquals(count.get(), 1);
    }

    @Test
    public void submit_Callable_immediately() throws ExecutionException, InterruptedException {
        executorService.setState(ExecutorServiceState.IMMEDIATELY);
        Callable<Integer> callable = () -> 5;
        Future<Integer> future = executorService.submit(callable);
        Assert.assertTrue(future.isDone());
        Assert.assertEquals(future.get(), Integer.valueOf(5));
    }

    @Test
    public void submit_Runnable_immediately() throws ExecutionException, InterruptedException {
        executorService.setState(ExecutorServiceState.IMMEDIATELY);
        AtomicBoolean gotCalled = new AtomicBoolean(false);
        Future<Void> future = executorService.submit(() -> gotCalled.set(true));
        Assert.assertTrue(future.isDone());
        Assert.assertTrue(gotCalled.get());
    }

    @Test
    public void submit_Runnable_Result_immediately() throws ExecutionException, InterruptedException {
        executorService.setState(ExecutorServiceState.IMMEDIATELY);
        Future<Boolean> future = executorService.submit(() -> { }, false);
        Assert.assertTrue(future.isDone());
        Assert.assertFalse(future.get());
    }

    @Test(expectedExceptions = ClassNotFoundException.class)
    public void submit_Callable_immediately_thrown() throws Throwable {
        executorService.setState(ExecutorServiceState.IMMEDIATELY);
        Callable<Integer> callable = () -> { throw new ClassNotFoundException(); };
        Future<Integer> future = executorService.submit(callable);
        Assert.assertTrue(future.isDone());
        try {
            future.get();
        } catch (ExecutionException e){
            throw e.getCause();
        }
    }

    @Test
    public void submit_Callable_onCall() throws ExecutionException, InterruptedException {
        executorService.setState(ExecutorServiceState.ONCALL);
        Callable<Integer> callable = () -> 5;
        Future<Integer> future = executorService.submit(callable);
        Assert.assertFalse(future.isDone());
        Assert.assertEquals(future.get(), Integer.valueOf(5));
    }

    @Test(expectedExceptions = ClassNotFoundException.class)
    public void submit_Callable_onCall_thrown() throws Throwable {
        executorService.setState(ExecutorServiceState.ONCALL);
        Callable<Integer> callable = () -> { throw new ClassNotFoundException(); };
        Future<Integer> future = executorService.submit(callable);
        Assert.assertFalse(future.isDone());
        try {
            future.get();
        } catch (ExecutionException e){
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
                Stream.generate(() -> callable).limit(10).
                        map(executorService::submitForNotFishingOnTermination).
                        collect(Collectors.toList());
        List<Runnable> runnables = executorService.shutdownNow();

        Assert.assertTrue(executorService.isShutdown());
        Assert.assertTrue(executorService.isShutdownNow());
        Assert.assertFalse(executorService.isTerminated());

        Assert.assertEquals(runnables, futures);
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
    public void shutdownNow_Finsished() {
        Callable<Void> callable = () -> null;
        List<Future<Void>> futures =
                Stream.generate(() -> callable).limit(10).
                        map(executorService::submit).
                        collect(Collectors.toList());
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
        TestThread testThread = new TestThread(new Callable<Void>() {

            @Override
            public Void call() throws Exception {
                Thread.currentThread().interrupt();
                executorService.awaitTermination(1L, null);
                return null;
            }
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
    public void awaitTermination_inTime_Exception() throws InterruptedException, ExecutionException {
        Future<Integer> future = executorService.submitForTerminationInTime(() -> { throw new Exception(); });
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

}
