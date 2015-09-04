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
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * ImmediatelyScheduledFuture represents a ScheduledFuture which
 * gets executed a number of times immediately on creation.
 *
 * @param <T> the return type
 */
public class ImmediatelyScheduledFuture<T> implements ScheduledFuture<T> {

    /**
     * The delay of this Future.
     */
    private final long delay;

    /**
     * The unit of delay.
     */
    private final TimeUnit delayUnit;

    /**
     * The Exception thrown during execution.
     */
    private final Throwable exception;

    /**
     * The result of the execution.
     */
    private final T result;

    /**
     * If this is done.
     */
    private boolean done = false;

    /**
     * If this got cancelled.
     */
    private boolean cancelled = false;

    /**
     * Creates a new ImmediatelyScheduledFuture.
     *
     * @param runCount how often to run callable
     * @param delay the time to wait
     * @param delayUnit the unit of delay
     * @param callable the callable to execute
     */
    public ImmediatelyScheduledFuture(long runCount,
            long delay, TimeUnit delayUnit, Callable<T> callable) {
        this.delay = delay;
        this.delayUnit = delayUnit;
        T tempResult = null;
        Throwable tempException = null;
        for (int i = 0; i < runCount; i++) {
            try {
                tempResult = callable.call();
                tempException = null;
            } catch (Throwable throwable) {
                tempException = throwable;
                tempResult = null;
                done = true;
            }
        }
        result = tempResult;
        exception = tempException;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(delay, delayUnit);
    }

    @Override
    public int compareTo(Delayed delayed) {
        throw new UnsupportedOperationException();
    }

    @Override
    public T get(long timeOut, TimeUnit unit) throws ExecutionException, InterruptedException {
        return get();
    }

    @Override
    public T get() throws ExecutionException, InterruptedException {
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptedException();
        }
        if (exception == null) {
            return result;
        }
        throw new ExecutionException(exception);
    }

    @Override
    public boolean isDone() {
        return done;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public boolean cancel(boolean interrupt) {
        cancelled = true;
        done = true;
        return cancelled;
    }

}

/* vim:set shiftwidth=4 softtabstop=4 expandtab: */
