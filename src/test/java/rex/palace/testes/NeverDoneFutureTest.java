package rex.palace.testes;

import org.testng.annotations.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by rex on 9/9/15.
 */
public class NeverDoneFutureTest {

    @Test(expectedExceptions = ExecutionException.class)
    public void get() throws ExecutionException, InterruptedException {
        Future<Void> future = new NeverDoneFuture<>(() -> null);
        future.get();
    }

}
