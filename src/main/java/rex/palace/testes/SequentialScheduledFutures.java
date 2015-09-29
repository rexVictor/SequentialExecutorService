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
 * Static factory class for SequentialScheduledFutures.
 */
final class SequentialScheduledFutures {

    /**
     * A Future which is run after an initial delay.
     *
     * @param <T> the return type of this Future.
     */
    private static final class DelayedSequentialFuture<T>
            extends AbstractSequentialScheduledFuture<T> {

        /**
         * Creates a new DelayedSequentialFuture.
         *
         * @param callable the task to run
         * @param initialDelay the initial delay to wait
         * @param unit the TimeUnit of initialDelay
         * @param timeController the timeController to register to
         * @throws NullPointerException if callable, unit or timeController
         *         is null
         * @throws IllegalArgumentException if initialDelay is not positive
         */
        private DelayedSequentialFuture(
                Callable<T> callable, long initialDelay,
                TimeUnit unit, TimeController timeController) {
            super(callable, initialDelay, unit, timeController);
        }

        @Override
        public boolean timePassed(long time, TimeUnit unit) {
            super.timePassed(time, unit);
            if (remainingDelay <= 0L) {
                run();
                return true;
            }
            return false;
        }

    }

    /**
     * PeriodicSequentialFuture is a task which gets run
     * periodically.
     *
     * <p>Note: After creation this task has never been run.
     *
     * @param <T> the return type
     */
    private static class PeriodicSequentialFuture<T>
            extends AbstractSequentialScheduledFuture<T> {

        /**
         * The period in nano seconds, after which this task gets run.
         */
        private final long period;

        /**
         * Creates a new PeriodicSequentialFuture.
         *
         * @param callable the task to be run periodically
         * @param period the time between executions
         * @param timeUnit the TimeUnit of period
         * @param timeController the TimeController this gets registered to
         * @throws NullPointerException if callable, timeUnit or timeController is null
         * @throws IllegalArgumentException if period is not positive.
         */
        PeriodicSequentialFuture(
                Callable<T> callable, long period,
                TimeUnit timeUnit, TimeController timeController) {
            super(callable, period, timeUnit, timeController);
            this.period = remainingDelay;
        }

        @Override
        public boolean timePassed(long time, TimeUnit unit) {
            super.timePassed(time, unit);
            long delay;
            while ((delay = getDelay(TimeUnit.NANOSECONDS)) <= 0L) {
                run();
                if (isExceptionHappened()) {
                    return true;
                }
                resetFuture();
                timePassed(-delay, TimeUnit.NANOSECONDS);
            }
            return false;
        }

        /**
         * Resets this future to its initial state to be rerun.
         */
        private void resetFuture() {
            if (!isCancelled() && !isExceptionHappened()) {
                remainingDelay = period;
                ran = false;
            }
        }

        @Override
        protected String toStringHelper() {
            return super.toStringHelper() + ", period = " + period;
        }

        @Override
        public String toString() {
            return "PeriodicSequentialFuture[" + toStringHelper() + ']';
        }

    }

    /**
     * A task which is periodically run after an initial delay.
     *
     * @param <T> the return type of this Future.
     */
    private static final class DelayedPeriodicSequentialFuture<T>
            extends PeriodicSequentialFuture<T> {

        /**
         * Creates a new DelayedPeriodicSequentialFuture.
         *
         * @param callable the task to be run
         * @param initialDelay the initial delay to wait before the first run
         * @param period the period in which this task shall be run after the
         *               first run
         * @param unit the TimeUnit of initialDelay and period
         * @param timeController the TimeController to be registered to
         * @throws NullPointerException if callable, unit or timeController is
         *         null
         * @throws IllegalArgumentException if initialDelay or period is not
         *         positive
         */
        private DelayedPeriodicSequentialFuture(
                Callable<T> callable, long initialDelay,
                long period, TimeUnit unit,
                TimeController timeController) {
            super(callable, period, unit, timeController);
            if (initialDelay <= 0L) {
                throw new IllegalArgumentException(
                        "The initialDelay must be positive, but was '"
                                + initialDelay + "'.");
            }
            remainingDelay = unit.toNanos(initialDelay);
        }

    }

    /**
     * Private constructor since this is a utility class.
     */
    private SequentialScheduledFutures() {
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

}

/* vim:set shiftwidth=4 softtabstop=4 expandtab: */
