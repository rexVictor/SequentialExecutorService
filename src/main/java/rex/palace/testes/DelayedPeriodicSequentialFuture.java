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

import java.util.concurrent.Callable;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * A task which is periodically run after an initial delay.
 *
 * @param <T> the return type of this Future.
 */
public class DelayedPeriodicSequentialFuture<T>
        implements SequentialScheduledFuture<T> {

    /**
     * The Future this' methods are delegated to.
     */
    private SequentialScheduledFuture<T> future;

    /**
     * Creates a new DelayedPeriodicSequentialFuture.
     *
     * @param callable the task to run
     * @param initialDelay the initial delay of this task
     * @param period the period this task is run with
     * @param unit the time unit of initialDelay and period
     */
    public DelayedPeriodicSequentialFuture(
            Callable<T> callable, long initialDelay, long period, TimeUnit unit) {
        if (initialDelay ==  0L) {
            future = new SequentialPeriodicFuture<>(callable, period, unit);
        } else {
            future = new DelayedDecorator<T>(callable, initialDelay, period, unit, this);
        }
    }

    @Override
    public T get(long timeOut, TimeUnit unit)
            throws ExecutionException, TimeoutException, InterruptedException {
        return future.get(timeOut, unit);
    }

    @Override
    public T get() throws ExecutionException, InterruptedException {
        return future.get();
    }

    @Override
    public void run() {
        future.run();
    }

    @Override
    public boolean isDone() {
        return future.isDone();
    }

    @Override
    public boolean isCancelled() {
        return future.isCancelled();
    }

    @Override
    public boolean cancel(boolean interruptPossible) {
        return future.cancel(interruptPossible);
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return future.getDelay(unit);
    }

    @Override
    public int compareTo(Delayed other) {
        return future.compareTo(other);
    }

    @Override
    public void timePassed(long time, TimeUnit unit) {
        future.timePassed(time, unit);
    }

    /**
     * Substitutes the future this' methods tasks are delegated to.
     *
     * @param future the new future to delegate to
     */
    void setFuture(SequentialScheduledFuture<T> future) {
        this.future = future;
    }

}

/* vim:set shiftwidth=4 softtabstop=4 expandtab: */
