package rex.palace.testes;

import java.util.concurrent.*;

/**
 * Created by rex on 9/8/15.
 */
public class OnCallFuture<V> extends SequentialFuture<V> {

    public OnCallFuture(Callable<V> callable) {
        super(callable);
    }

    @Override
    public V get() throws ExecutionException {
        run();
        return super.get();
    }

}
