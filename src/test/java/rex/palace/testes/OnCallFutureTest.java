package rex.palace.testes;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.concurrent.*;

/**
 * Created by rex on 9/8/15.
 */
public class OnCallFutureTest {

    private final Callable<?> callable = () -> null;

    public OnCallFutureTest(){
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void new_nullCallable() {
        new OnCallFuture<>(null);
    }

    @Test
    public void cancel_true() {
        Future<?> future = new OnCallFuture<>(callable);
        Assert.assertFalse(future.isCancelled());
        Assert.assertTrue(future.cancel(true));
        Assert.assertTrue(future.isCancelled());
    }

    @Test
    public void cancel_false() throws InterruptedException, ExecutionException, TimeoutException {
        Future<?> future = new OnCallFuture<>(callable);
        Assert.assertFalse(future.isCancelled());
        Assert.assertTrue(future.cancel(false));
        Assert.assertTrue(future.isCancelled());
    }

    @Test
    public void cancel_twice() {
        Future<?> future = new OnCallFuture<>(callable);
        Assert.assertFalse(future.isCancelled());
        Assert.assertTrue(future.cancel(false));
        Assert.assertTrue(future.isCancelled());
        Assert.assertFalse(future.cancel(true));
    }

    @Test
    public void cancel_afterDone() throws InterruptedException, ExecutionException, TimeoutException {
        Future<?> future = new OnCallFuture<>(callable);
        Assert.assertFalse(future.isCancelled());
        future.get(1L, null);
        Assert.assertFalse(future.cancel(false));
    }

    @Test(expectedExceptions = CancellationException.class)
    public void get_afterCancel() throws InterruptedException, ExecutionException, TimeoutException {
        Future<?> future = new OnCallFuture<>(callable);
        Assert.assertFalse(future.isCancelled());
        future.cancel(true);
        future.get(1L, null);
    }

    @Test
    public void isDone_false() {
        Future<?> future = new OnCallFuture<>(callable);
        Assert.assertFalse(future.isDone());
    }

    @Test
    public void isDone_cancelled() {
        Future<?> future = new OnCallFuture<>(callable);
        future.cancel(true);
        Assert.assertTrue(future.isDone());
    }

    @Test
    public void isDone_success() throws ExecutionException, InterruptedException {
        Future<?> future = new OnCallFuture<>(callable);
        future.get();
        Assert.assertTrue(future.isDone());
    }

}
