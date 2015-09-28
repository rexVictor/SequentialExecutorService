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
import java.util.concurrent.TimeUnit;

/**
 * Static factory class for SequentialFutures.
 */
final class SequentialFutures {

    /**
     * Private constructor since this is a utility class.
     */
    private SequentialFutures() {
        super();
    }

    /**
     * Creates a SequentialScheduledFuture which gets run
     * after an initial delay.
     *
     * @param callable the task to run
     * @param initialDelay the delay before callable is run
     * @param timeUnit the TimeUnit of initialDelay
     * @param timeController the timeController which simulates time
     * @param <T> the return type of callable
     * @return a ScheduledFuture with an initial delay
     * @throws NullPointerException if callable, timeUnit or
     *         timeController is null
     * @throws IllegalArgumentException if initialDelay is not positive
     */
    static <T> SequentialScheduledFuture<T> getDelayed(
            Callable<T> callable, long initialDelay,
            TimeUnit timeUnit, TimeController timeController) {
        return new DelayedSequentialFuture<>(
                callable, initialDelay, timeUnit, timeController);
    }

    /**
     * Creates a SequentialScheduledFuture which gets run periodically.
     *
     * @param callable the task to run
     * @param period the period callable is rerun with
     * @param timeUnit the TimeUnit of period
     * @param timeController the timeController which simulates time
     * @param <T> the return type of callable
     * @return a ScheduledFuture with is rerun periodically
     * @throws NullPointerException if callable, timeUnit or
     *         timeController is null
     * @throws IllegalArgumentException if period is not positive
     */
    static <T> SequentialScheduledFuture<T> getPeriodic(
            Callable<T> callable, long period,
            TimeUnit timeUnit, TimeController timeController) {
        return new PeriodicSequentialFuture<>(
                callable, period, timeUnit, timeController);
    }

    /**
     * Creates a SequentialScheduledFuture which gets run periodically
     * after an initial delay.
     *
     * @param callable the task to run
     * @param initialDelay the delay before callable is run
     * @param period the period callable is rerun with
     * @param timeUnit the TimeUnit of period
     * @param timeController the timeController which simulates time
     * @param <T> the return type of callable
     * @return a ScheduledFuture with is rerun periodically
     * @throws NullPointerException if callable, timeUnit or
     *         timeController is null
     * @throws IllegalArgumentException if period or initialDelay
     *         is not positive
     */
    static <T> SequentialScheduledFuture<T> getDelayedPeriodic(
            Callable<T> callable, long initialDelay, long period,
            TimeUnit timeUnit, TimeController timeController) {
        return new DelayedPeriodicSequentialFuture<>(
                callable, initialDelay, period, timeUnit, timeController);
    }

    static <T> SequentialCallbackFuture<T> getImmediately(Callable<T> callable) {
        return new ImmediatelyFuture<>(callable);
    }

    static <T> SequentialCallbackFuture<T> getOnCall(Callable<T> callable) {
        return new OnCallFuture<>(callable);
    }

    static <T> SequentialCallbackFuture<T> getNeverDone(Callable<T> callable) {
        return new NeverDoneFuture<>(callable);
    }

}

/* vim:set shiftwidth=4 softtabstop=4 expandtab: */
