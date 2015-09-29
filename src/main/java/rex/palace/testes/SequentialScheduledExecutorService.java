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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * An API breaking implementation of ScheduledExecutorService.
 */
public class SequentialScheduledExecutorService
         extends SequentialExecutorService
         implements ScheduledExecutorService {

    /**
     * All tasks scheduled by this ExecutorService.
     */
    private final List<SequentialScheduledFuture<?>> scheduledTasks = new ArrayList<>();

    /**
     * The TimeController the futures generated by this register to.
     */
    private final TimeController timeController;

    /**
     * Creates a new SequentialScheduledExecutorService.
     *
     * @param timeController the TimeController to use for creating tasks
     * @throws NullPointerException if timeController is null
     */
    public SequentialScheduledExecutorService(TimeController timeController) {
        this.timeController = Objects.requireNonNull(timeController);
    }


    @Override
    public <V> ScheduledFuture<V> schedule(Callable<V> callable,
            long delay, TimeUnit unit) {
        throwExceptionIfShutdown();
        SequentialScheduledFuture<V> future
                = SequentialScheduledFutures.getDelayed(callable, delay,
                unit, timeController);
        scheduledTasks.add(future);
        return future;
    }

    @Override
    public ScheduledFuture<?> schedule(Runnable command,
            long delay, TimeUnit unit) {
        return schedule(Executors.callable(command), delay, unit);
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command,
            long initialDelay, long period, TimeUnit unit) {
        return scheduleWithFixedDelay(command, initialDelay, period, unit);
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command,
            long initialDelay, long delay, TimeUnit unit) {
        throwExceptionIfShutdown();
        SequentialScheduledFuture<Object> future;
        if (initialDelay == 0L) {
            future = SequentialScheduledFutures.getPeriodic(
                    Executors.callable(command), delay, unit, timeController);
            future.timePassed(delay, unit);
        } else {
            future = SequentialScheduledFutures.getDelayedPeriodic(
                    Executors.callable(command), initialDelay,
                    delay, unit, timeController);
        }
        scheduledTasks.add(future);
        return future;
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit)
            throws InterruptedException {
        if (isShutdown()) {
            return false;
        }
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptedException();
        }
        try {
            timeController.letTimePassUntil(
                    () -> scheduledTasks.stream().allMatch(Future::isDone),
                    timeout, unit);
        } catch (TimeoutException e) {
            //expected
            return false;
        }
        return super.awaitTermination(timeout, unit);
    }

    @Override
    public List<Runnable> shutdownNow() {
        List<Runnable> superRunnables = super.shutdownNow();
        Stream<Runnable> notFinishedScheduled =
                scheduledTasks.stream()
                .filter(future -> !future.isDone())
                        .map(future -> (Runnable) future);
        superRunnables.addAll(notFinishedScheduled.collect(Collectors.toList()));
        return superRunnables;
    }

}

/* vim:set shiftwidth=4 softtabstop=4 expandtab: */
