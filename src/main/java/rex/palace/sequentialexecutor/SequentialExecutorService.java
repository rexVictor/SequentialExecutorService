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

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


/**
 * An API breaking implementation for ExecutorService.
 *
 * <p>Its purpose is to test functionality under non parallel conditions.
 */
public class SequentialExecutorService implements ExecutorService {

    /**
     * The TaskOrganizer Future handling is delegated to.
     */
    private final TaskOrganizer organizer = new TaskOrganizer();

    /**
     * Indicates if shutdown was called.
     */
    private boolean shutdown = false;

    /**
     * Indicates if shutdownNow was called.
     */
    private boolean shutdownNow = false;

    /**
     * Indicates if this service has been shutdown.
     */
    private boolean isShutdown = false;

    /**
     * The ExecutorServiceState this ExecutorService is in.
     */
    private ExecutorServiceState serviceState = ExecutorServiceState.IMMEDIATELY;

    /**
     * Creates a new SequentialExecutorService.
     */
    public SequentialExecutorService() {
        super();
    }

    /**
     * Does nothing if this service has not yet shutdown and throws
     * an RejectedExecutionException otherwise.
     *
     * @throws RejectedExecutionException if this service is shutdown.
     */
    protected final void throwExceptionIfShutdown() {
        if (isShutdown) {
            throw new RejectedExecutionException(
                    "This service has already been shutdown.");
        }
    }

    @Override
    public <T> T invokeAny(
            Collection<? extends Callable<T>> tasks,
            long timeout, TimeUnit unit)
            throws ExecutionException, InterruptedException {
        return invokeAny(tasks);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks)
            throws InterruptedException, ExecutionException {
        throwExceptionIfShutdown();
        return tasks.stream()
                .map(callable -> submit(callable, ExecutorServiceState.IMMEDIATELY))
                .filter(Future::isDone)
                .filter(ExecutorServiceHelper::isRegularlyDone)
                .findAny().get().get();
    }

    @Override
    public <T> List<Future<T>> invokeAll(
            Collection<? extends Callable<T>> tasks,
            long timeout, TimeUnit unit) {
        return invokeAll(tasks);
    }

    @Override
    public <T> List<Future<T>> invokeAll(
            Collection<? extends Callable<T>> tasks) {
        throwExceptionIfShutdown();
        return tasks.stream().map(this::submit).collect(Collectors.toList());
    }

    @Override
    public Future<Void> submit(Runnable task) {
        return submit(task, null);
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        return submit(task, serviceState, result);
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return submit(task, serviceState);
    }

    /**
     * Submits the Runnable according to the ExecutorServiceState.
     *
     * @param runnable the runnable to submit
     * @param state    the serviceState defining how to submit
     * @param result   the result the Future shall return
     * @param <T>      the type of result
     * @return a Future for runnable
     */
    private <T> Future<T> submit(
            Runnable runnable, ExecutorServiceState state, T result) {
        return submit(ExecutorServiceHelper.convert(runnable, result), state);
    }

    /**
     * Submits the Callable according to the ExecutorServiceState.
     *
     * @param callable the callable to submit
     * @param state the state defining how to submit
     * @param <T> the type of callable
     * @return a Future for callable
     */
    private <T> Future<T> submit(
            Callable<T> callable, ExecutorServiceState state) {
        throwExceptionIfShutdown();
        return organizer.submit(state, callable);
    }

    @Override
    public List<Runnable> shutdownNow() {
        shutdownNow = true;
        isShutdown = true;
        return organizer.notFinishedTasks().collect(Collectors.toList());
    }

    @Override
    public void shutdown() {
        shutdown = true;
        isShutdown = true;
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit)
            throws InterruptedException {
        if (!isShutdown) {
            return false;
        }
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptedException();
        }
        organizer.awaitTermination();
        return organizer.notFinishedTasksCount() == 0;
    }

    @Override
    public boolean isTerminated() {
        if (!isShutdown) {
            return false;
        }
        return organizer.notFinishedTasksCount() == 0;
    }

    @Override
    public boolean isShutdown() {
        return isShutdown;
    }

    /**
     * Executes the given command immediately in the calling thread.
     *
     * @param command the command to execute
     * @throws NullPointerException if command is null
     */
    @Override
    public void execute(Runnable command) {
        submit(command, ExecutorServiceState.IMMEDIATELY, null);
    }

    /**
     * Submits a task for termination in time of calling awaitTermination().
     *
     * @param callable the task to be successfully executed on awaitTermination()
     * @param <T>      the type of callable
     * @return a future object which is accessible after awaitTermination() is called
     */
    public <T> Future<T> submitForTerminationInTime(Callable<T> callable) {
        return submit(callable, ExecutorServiceState.AWAIT_TERMINATION);
    }

    /**
     * Submits a task which will never be run.
     *
     * @param callable the task to never run
     * @param <T>      the type of callable
     * @return the future of this callable
     */
    public <T> Future<T> submitForNotFishingOnTermination(Callable<T> callable) {
        return submit(callable, ExecutorServiceState.NEVER);
    }

    /**
     * Sets the serviceState of the ExecutorService.
     *
     * @param state the serviceState to set this ExecutorService in
     * @throws NullPointerException if serviceState is null
     */
    public void setExecutorServiceState(ExecutorServiceState state) {
        serviceState = Objects.requireNonNull(state);
    }

    /**
     * Returns if shutdownNow() has been called.
     *
     * @return true if and only if shutdownNow() has been called.
     */
    public boolean isShutdownNow() {
        return shutdownNow;
    }

    /**
     * Returns if shutdown() has been called.
     *
     * @return true if and only if shutdown() has been called.
     */
    public boolean isJustShutdown() {
        return shutdown;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getClass().getSimpleName());
        sb.append('[');
        if (isShutdown) {
            sb.append("SHUTDOWN");
        } else {
            sb.append("READY");
        }
        sb.append(", submittedTasks = ")
                .append(organizer.submittedTasksCount())
                .append(", finishedTasks = ")
                .append(organizer.finishedTasksCount())
                .append(']');
        return sb.toString();
    }

}

/* vim:set shiftwidth=4 softtabstop=4 expandtab: */