/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This file is part of Robbie.
 *
 * Robbie is a 2d-adventure game.
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

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

/**
 * Created by rex on 9/8/15.
 */
public class OnCallFutureTest {

    /**
     * A null returning Callable.
     */
    private final Callable<?> callable = () -> null;

    /**
     * Empty constructor.
     */
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
    public void cancel_afterDone()
            throws InterruptedException, ExecutionException, TimeoutException {
        Future<?> future = new OnCallFuture<>(callable);
        Assert.assertFalse(future.isCancelled());
        future.get(1L, null);
        Assert.assertFalse(future.cancel(false));
    }

    @Test(expectedExceptions = CancellationException.class)
    public void get_afterCancel()
            throws InterruptedException, ExecutionException, TimeoutException {
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

/* vim:set shiftwidth=4 softtabstop=4 expandtab: */
