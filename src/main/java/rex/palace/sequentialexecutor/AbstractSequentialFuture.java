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

package rex.palace.sequentialexecutor;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

/**
 * An abstract implementation of a SequentialFuture.
 *
 * @param <T> the type this future holds.
 */
abstract class AbstractSequentialFuture<T> implements SequentialFuture<T> {

    /**
     * Indicates if this task has been cancelled.
     */
    protected boolean cancelled = false;

    /**
     * Indicates if this task has been run.
     */
    protected boolean ran = false;

    /**
     * The Exception which occurred during the run.
     */
    private Exception exception;

    /**
     * The result of the run.
     */
    private T result;

    /**
     * The CallableWrapper providing the run method.
     */
    private final CallableWrapper<T> wrapper;

    /**
     * Constructs a new AbstractSequentialFuture with the specified task.
     *
     * @param callable the task to run
     * @throws NullPointerException if callable is null
     */
    AbstractSequentialFuture(Callable<T> callable) {
        wrapper = new CallableWrapper<>(this, callable);
    }

    /**
     * Returns the result of this task if it has been run.
     *
     * <p>If it has not run, it blocks the current Thread until
     * it is interrupted or the result is available, but beware:
     * This class was designed for a non-parallel environment. So
     * if the result is not available this method might block for eternity.
     *
     * @return the result of this task
     * @throws ExecutionException if this task threw an Exception
     * @throws InterruptedException if this task is not cancelled and
     *         the current Thread is interrupted
     * @throws CancellationException if this task got cancelled.
     */
    @Override
    public T get() throws ExecutionException, InterruptedException {
        if (cancelled) {
            throw new CancellationException(
                    ExecutorServiceHelper.CANCELLATION_MESSAGE);
        }
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptedException(
                    ExecutorServiceHelper.INTERUPPTED_MESSAGE);
        }
        if (!hasRun()) {
            throw new IllegalStateException("Task has not run yet.");
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
    public void setException(Exception exception) {
        this.exception = exception;
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
    public boolean cancel(boolean mayInterruptIfRunning) {
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
    public boolean isExceptionHappened() {
        return exception != null;
    }

    /**
     * A helper method for toString().
     * @return a String representation of the relevant fields
     */
    protected String toStringHelper() {
        StringBuilder sb = new StringBuilder("task=")
                .append(wrapper)
                .append(",state=");
        if (cancelled) {
            sb.append("cancelled");
        } else if (isDone()) {
            sb.append("done ");
            if (isExceptionHappened()) {
                sb.append("failure: ")
                        .append(exception.getClass().getName());
            } else {
                sb.append("result: ").append(result);
            }
        } else {
            sb.append("running");
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return "SequentialFuture[" + toStringHelper() + ']';
    }

}
