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

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * An abstract implementation of a SequentialFuture.
 *
 * @param <T> the type this future holds.
 */
public abstract class AbstractSequentialFuture<T> implements SequentialFuture<T> {

    /**
     * Indicates if this task has been cancelled.
     */
    protected boolean cancelled = false;

    /**
     * Indicates if this task has been run.
     */
    protected boolean ran = false;

    /**
     * The Exception which occurred during the calculation.
     */
    private Exception exception;

    /**
     * The result this future holds.
     */
    private T result;

    /**
     * The CallableWrapper providing the run method.
     */
    private final CallableWrapper<T> wrapper;

    /**
     * Creates a new AbstractSequentialFuture.
     *
     * @param callable the task to run
     * @throws NullPointerException if callable is null
     */
    public AbstractSequentialFuture(Callable<T> callable) {
        wrapper = new CallableWrapper<>(this, callable);
    }

    /**
     * Since nothing runs parallel, this method just delegates to get().
     *
     * @param timeOut discarded
     * @param unit discarded
     * @return the result of this task
     * @throws ExecutionException if an exception occurred during this task
     * @throws InterruptedException if the calling thread is interrupted
     * @throws TimeoutException in this class never; subclasses may throw
     */
    @Override
    public T get(long timeOut, TimeUnit unit)
            throws ExecutionException, InterruptedException, TimeoutException {
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
    public void run() {
        wrapper.run();
        ran = true;
    }

    @Override
    public void setException(Exception runException) {
        this.exception = runException;
    }

    @Override
    public void setResult(T result) {
        this.result = result;
    }

    @Override
    public boolean isDone() {
        return cancelled || ran;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public boolean cancel(boolean interruptPossible) {
        if (isDone()) {
            return false;
        }
        cancelled = true;
        return true;
    }

    @Override
    public boolean hasRun() {
        return ran;
    }

    @Override
    public boolean didExceptionHappen() {
        return exception != null;
    }

}

/* vim:set shiftwidth=4 softtabstop=4 expandtab: */
