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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * An API breaking implementation for ExecutorService.
 *
 * <p> Its purpose is to test functionality under non paraller condititons.
 */
public class SequentialExecutorService implements ExecutorService {

    /**
     * The state a SequentialExecutorService can be in.
     */
    public static enum ExecutorServiceState {
        /**
         * Performs submitted tasks immediately.
         */
        IMMEDIATELY,
        /**
         * Preforms submitted tasks when the get is called
         * on the Future.
         */
        ONCALL,
        /**
         * Never performs the submitted tasks.
         */
        NEVER
    }

    /**
     * Indicates if shutdown was called.
     */
    private boolean shutdown = false;

    /**
     * Indicates if shutdownNow was called.
     */
    private boolean shutdownNow = false;

    /**
     * The state this ExecutorService is in.
     */
    private ExecutorServiceState state = ExecutorServiceState.IMMEDIATELY;

    /**
     * The tasks which will be successfully run when calling shutdown().
     */
    private final List<Callable<?>> onAwaitTerminatonSuccessfulTasks = new ArrayList<>();

    /**
     * The tasks which will be left over when calling shutdown().
     */
    private final List<Callable<?>> onAwaitTerminatonLeftOverTasks = new ArrayList<>();

    /**
     * Creates a new SequentialExecutorService.
     */
    public SequentialExecutorService() {
    }

    @Override
    public <T> T invokeAny(
            Collection<? extends Callable<T>> callables,
            long timeOut, TimeUnit unit) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T invokeAny(
            Collection<? extends Callable<T>> callables) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> List<Future<T>> invokeAll(
            Collection<? extends Callable<T>> callables,
            long timeOut, TimeUnit unit) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> List<Future<T>> invokeAll(
            Collection<? extends Callable<T>> callables) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Future<?> submit(Runnable runnable) {
        switch (state) {
            case IMMEDIATELY:
                try {
                    runnable.run();
                    return new SequentialFuture<Void>((Void) null);
                } catch (Exception e) {
                    return new SequentialFuture<Void>(e);
                }
            case NEVER:
                return new NeverDoneFuture<Void>();
            default:
                throw new UnsupportedOperationException();
        }
    }

    @Override
    public <T> Future<T> submit(Runnable runnable, T result) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> Future<T> submit(Callable<T> callable) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Runnable> shutdownNow() {
        shutdownNow = true;
        return new ArrayList<>();
    }

    @Override
    public void shutdown() {
        shutdown = true;
    }

    @Override
    public boolean awaitTermination(long timeOut, TimeUnit unit)
                throws InterruptedException {
        for (Callable<?> callable : onAwaitTerminatonSuccessfulTasks) {
            runCallable(callable);
        }
        return onAwaitTerminatonLeftOverTasks.isEmpty();
    }

    /**
     * Runs the callable and wraps thrown exception in RuntimeExceptions or
     * rethrows them if they are InterruptedException.
     *
     * @param callable the callable to run
     * @param <T> the type of callable
     * @return the result of callable
     * @throws InterruptedException if callables throws one
     */
    private <T> T runCallable(Callable<T> callable) throws InterruptedException {
        try {
            return callable.call();
        } catch (InterruptedException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isTerminated() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isShutdown() {
        return shutdown;
    }

    @Override
    public void execute(Runnable runnable) {
        throw new UnsupportedOperationException();
    }

    /**
     * Submits a task for termination in time of calling awaitTermination().
     * @param callable the task to be successfully executed on awaitTermination().
     *
     * @return a future object which is accessible after awaitTermination() is called
     */
    public Future<?> submitForTerminationInTime(Callable<?> callable) {
        onAwaitTerminatonSuccessfulTasks.add(callable);
        return null;
    }

    /**
     * Submits a task whill will never be run.
     *
     * @param callable the task to never run
     * @return the future of this callable
     */
    public Future<?> submitForNotFishingOnTermination(Callable<?> callable) {
        onAwaitTerminatonLeftOverTasks.add(callable);
        return null;
    }

    /**
     * Sets the state of the ExecutorService.
     *
     * @param state the state to set this ExecutorService in
     * @throws NullPointerException if state is null
     */
    public void setState(ExecutorServiceState state) {
        this.state = Objects.requireNonNull(state);
    }

}

/* vim:set shiftwidth=4 softtabstop=4 expandtab: */
