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
import org.testng.annotations.Test;
import rex.palace.testhelp.TestThread;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

/**
 * Tests the ImmediatelyFuture class.
 */
public class ImmediatelyFutureTest {

    /**
     * A null returning callable.
     */
    private final Callable<?> callable = () -> null;

    /**
     * Empty constructor.
     */
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

    @Test(expectedExceptions = InterruptedException.class)
    public void get_interrupted() throws Exception {
        ImmediatelyFuture<Void> future = new ImmediatelyFuture<>(() -> null);
        TestThread thread = new TestThread(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                Thread.currentThread().interrupt();
                return future.get();
            }
        });
        thread.start();
        thread.join();
        thread.finish();
    }

}

/* vim:set shiftwidth=4 softtabstop=4 expandtab: */
