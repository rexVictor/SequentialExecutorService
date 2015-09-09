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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * An API breaking implementation for ExecutorService.
 *
 * <p>Its purpose is to test functionality under non parallel conditions.
 */
public class SequentialExecutorService implements ExecutorService {

    /**
     * Indicates if shutdown was called.
     */
    private boolean shutdown = false;

    /**
     * Indicates if shutdownNow was called.
     */
    private boolean shutdownNow = false;

    private boolean isShutdown = false;

    /**
     * The state this ExecutorService is in.
     */
    private ExecutorServiceState state = ExecutorServiceState.IMMEDIATELY;

    private List<RunnableFuture<?>> submittedTasks = new ArrayList<>();

    /**
     * The tasks which will be successfully run when calling shutdown().
     */
    private final List<RunnableFuture<?>> onAwaitTerminatonSuccessfulTasks = new ArrayList<>();

    /**
     * Creates a new SequentialExecutorService.
     */
    public SequentialExecutorService() {
        super();
    }

    @Override
    public <T> T invokeAny(
            Collection<? extends Callable<T>> callables,
            long timeOut, TimeUnit unit) throws ExecutionException, InterruptedException {
        return invokeAny(callables);
    }

    @Override
    public <T> T invokeAny(
            Collection<? extends Callable<T>> callables) throws InterruptedException, ExecutionException {
        if (isShutdown) {
            throw new RejectedExecutionException();
        }
        return callables.stream().map(ImmediatelyFuture<T>::new).filter(Future::isDone).
                filter(this::isRegularyDone).findAny().get().get();
    }

    private boolean isRegularyDone(Future<?> future) {
        try {
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            return false;
        }
        return true;
    }

    @Override
    public <T> List<Future<T>> invokeAll(
            Collection<? extends Callable<T>> callables,
            long timeOut, TimeUnit unit) {
        return invokeAll(callables);
    }

    @Override
    public <T> List<Future<T>> invokeAll(
            Collection<? extends Callable<T>> callables) {
        if (isShutdown) {
            throw new RejectedExecutionException();
        }
        return callables.stream().map(ImmediatelyFuture<T>::new).collect(Collectors.toList());
    }

    @Override
    public Future<Void> submit(Runnable runnable) {
        return submit(convert(runnable), state);
    }

    private Callable<Void> convert(Runnable runnable) {
        return convert(runnable, null);
    }

    private <T> Callable<T> convert(Runnable runnable, T result) {
        return new Callable<T>() {
            @Override
            public T call() throws Exception {
                runnable.run();
                return result;
            }
        };
    }

    @Override
    public <T> Future<T> submit(Runnable runnable, T result) {
        return submit(convert(runnable, result), state);
    }

    @Override
    public <T> Future<T> submit(Callable<T> callable) {
        return submit(callable, state);
    }

    private <T> Future<T> submit(Callable<T> callable, ExecutorServiceState state) {
        if (isShutdown) {
            throw new RejectedExecutionException();
        }
        RunnableFuture<T> future = state.submit(callable);
        submittedTasks.add(future);
        return future;
    }

    @Override
    public List<Runnable> shutdownNow() {
        shutdownNow = true;
        isShutdown = true;
        return notFinishedTasks().collect(Collectors.toList());
    }

    private Stream<? extends Runnable> notFinishedTasks() {
        return submittedTasks.stream().filter(future -> !future.isDone());
    }

    @Override
    public void shutdown() {
        shutdown = true;
        isShutdown = true;
    }

    @Override
    public boolean awaitTermination(long timeOut, TimeUnit unit)
                throws InterruptedException {
        if (!isShutdown) {
            return false;
        }
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptedException();
        }
        onAwaitTerminatonSuccessfulTasks.stream().forEach(this::getResult);
        return notFinishedTasks().collect(Collectors.toList()).isEmpty();
    }

    /**
     * Gets the future and wraps thrown exception in RuntimeExceptions or
     * rethrows them if they are InterruptedException.
     *
     * @param callable the callable to run
     * @param <T> the type of callable
     * @return the result of callable
     * @throws InterruptedException if callables throws one
     */
    private void getResult(Future<?> future) {
        try {
            future.get();
        } catch (ExecutionException | InterruptedException expected) {
            //expected
        }
    }

    @Override
    public boolean isTerminated() {
        if (!isShutdown) {
            return false;
        }
        return notFinishedTasks().collect(Collectors.toList()).isEmpty();
    }

    @Override
    public boolean isShutdown() {
        return isShutdown;
    }

    @Override
    public void execute(Runnable runnable) {
        runnable.run();
    }

    /**
     * Submits a task for termination in time of calling awaitTermination().
     * @param callable the task to be successfully executed on awaitTermination().
     *
     * @return a future object which is accessible after awaitTermination() is called
     */
    public <T> Future<T> submitForTerminationInTime(Callable<T> callable) {
        RunnableFuture<T> future = ExecutorServiceState.ONCALL.submit(callable);
        onAwaitTerminatonSuccessfulTasks.add(future);
        return future;
    }

    /**
     * Submits a task which will never be run.
     *
     * @param callable the task to never run
     * @return the future of this callable
     */
    public <T> Future<T> submitForNotFishingOnTermination(Callable<T> callable) {
        return submit(callable, ExecutorServiceState.NEVER);
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

    public boolean isShutdownNow() {
        return shutdownNow;
    }

    public boolean isJustShutdown() {
        return shutdown;
    }
}

/* vim:set shiftwidth=4 softtabstop=4 expandtab: */
