package rex.palace.testes;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.RunnableFuture;

/**
 * The state a SequentialExecutorService can be in.
 */
public enum ExecutorServiceState {

    /**
     * Performs submitted tasks immediately.
     */
    IMMEDIATELY {

        @Override
        public <V> RunnableFuture<V> submit(Callable<V> callable) {
            return new ImmediatelyFuture<V>(callable);
        }

    },

    /**
     * Preforms submitted tasks when the get is called
     * on the Future.
     */
    ONCALL {

        @Override
        public <V> RunnableFuture<V> submit(Callable<V> callable) {
            return new OnCallFuture<V>(callable);
        }

    },
    /**
     * Never performs the submitted tasks.
     */
    NEVER {
        @Override
        public <V> RunnableFuture<V> submit(Callable<V> callable) {
            return new NeverDoneFuture<V>(callable);
        }
    };

    public abstract <V> RunnableFuture<V> submit(Callable<V> callable);

}
