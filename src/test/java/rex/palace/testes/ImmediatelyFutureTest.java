package rex.palace.testes;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

/**
 * Created by rex on 9/8/15.
 */
public class ImmediatelyFutureTest {

    private final Callable<?> callable = () -> null;

    public ImmediatelyFutureTest(){
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void new_nullCallable() {
        new ImmediatelyFuture<>(null);
    }

    @Test
    public void cancel_true() {
        Future<?> future = new ImmediatelyFuture<>(callable);
        Assert.assertFalse(future.isCancelled());
        Assert.assertFalse(future.cancel(true));
    }

    @Test
    public void cancel_false() throws InterruptedException, ExecutionException, TimeoutException {
        Future<?> future = new ImmediatelyFuture<>(callable);
        Assert.assertFalse(future.isCancelled());
        Assert.assertFalse(future.cancel(false));
        Assert.assertSame(future.get(1L, null), null);
    }

}
