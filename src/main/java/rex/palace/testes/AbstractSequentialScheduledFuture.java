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

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * An abstract implementation of a SequentialScheduledFuture.
 *
 * @param <T> the type this future holds.
 */
abstract class AbstractSequentialScheduledFuture<T>
        extends AbstractSequentialFuture<T>
        implements SequentialScheduledFuture<T> {

    /**
     * The remaining time in nanos before this task gets run.
     */
    protected long remainingDelay;

    /**
     * The TimeController this task is registered to.
     */
    protected final TimeController timeController;

    /**
     * The initial delay.
     */
    protected final long initialDelay;

    /**
     * Creates a new AbstractSequentialScheduledFuture.
     *
     * @param callable the task to perform
     * @param delay the delay before this task is performed
     * @param unit the TimeUnit of delay
     * @param timeController the TimeController to be registered to.
     *
     * @throws NullPointerException if callable, unit or timeController is null
     */
    protected AbstractSequentialScheduledFuture(
            Callable<T> callable, long delay,
            TimeUnit unit, TimeController timeController) {
        super(callable);
        if (delay <= 0L) {
            throw new IllegalArgumentException(
                    "The delay must be positive, but was '" + delay + "'.");
        }
        remainingDelay = Objects.requireNonNull(
                unit, "The unit must not be null").toNanos(delay);
        this.timeController = Objects.requireNonNull(timeController,
                "The timeController must not be null");
        initialDelay = remainingDelay;
        timeController.register(this);
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return Objects.requireNonNull(unit).convert(
                remainingDelay, TimeUnit.NANOSECONDS);
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        boolean result = super.cancel(mayInterruptIfRunning);
        if (result) {
            timeController.unregister(this);
        }
        return result;
    }

    /**
     * Basic implementation. It reduces the passed time from remaining delay.
     *
     * @param time the time that has passed
     * @param unit the TimeUnit of time
     * @return false
     */
    @Override
    public boolean timePassed(long time, TimeUnit unit) {
        remainingDelay -= Objects.requireNonNull(unit).toNanos(time);
        return false;
    }

    /**
     * Causes the associated TimeController to simulate passing time
     * until this future is done and returns it result.
     *
     * @return the result of the future
     *
     * @throws ExecutionException if an Exception occurred running this task
     * @throws InterruptedException if the calling Thread is interrupted
     */
    @Override
    public T get() throws ExecutionException, InterruptedException {
        timeController.letTimePassUntil(this::isDone);
        return super.get();
    }

    /**
     * Causes the associated TimeController to simulate passing time
     * until this future is done or the timeout occurred and returns it result.
     *
     * @param timeout the time to wait
     * @param unit the TimeUnit of timeout
     * @return the result of the future
     *
     * @throws ExecutionException if an Exception occurred running this task
     * @throws InterruptedException if the calling Thread is interrupted
     * @throws TimeoutException if the result was not ready in time
     */
    @Override
    public T get(long timeout, TimeUnit unit)
            throws TimeoutException, ExecutionException, InterruptedException {
        timeController.letTimePassUntil(this::isDone, timeout, unit);
        return super.get(timeout, unit);
    }

    @Override
    protected String toStringHelper() {
        return "TimeController = " + timeController + ", "
                + super.toStringHelper()
                + ", remainingDelay = " + remainingDelay
                + ", initialDelay = " + initialDelay;
    }

    @Override
    public String toString() {
        return "SequentialScheduledFuture" + "[" + toStringHelper() + "]";
    }

}

/* vim:set shiftwidth=4 softtabstop=4 expandtab: */
