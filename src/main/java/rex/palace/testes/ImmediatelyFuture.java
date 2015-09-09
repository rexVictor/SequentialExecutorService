package rex.palace.testes;

import java.util.concurrent.Callable;

/**
 * Created by rex on 9/8/15.
 */
public class ImmediatelyFuture<V> extends SequentialFuture<V> {

    public ImmediatelyFuture(Callable<V> callable){
        super(callable);
        run();
    }

}
