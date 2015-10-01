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
 * Static factory class for SequentialFutures.
 */
final class SequentialFutures {

    /**
     * This is a {@link java.util.concurrent.RunnableFuture}, which performs
     * the task, it is constructed with, immediately in the constructing Thread.
     *
     * <p>Because the associated task is already done after construction, the
     * following applies:
     * <ul>
     *     <li>
     *         {@link #get()} and
     *         {@link #get(long, TimeUnit)}
     *         will immediately return the result.
     *     </li>
     *     <li>
     *         {@link #cancel(boolean)} returns always false.
     *     </li>
     *     <li>
     *         {@link #isCancelled()} returns always false.
     *     </li>
     *     <li>
     *         {@link #isDone()} returns always true.
     *     </li>
     *     <li>
     *         {@link #hasRun()} returns always true.
     *     </li>
     * </ul>
     *
     * @param <T> the result type of this Future
     */
    private static final class ImmediatelyFuture<T> extends AbstractSequentialFuture<T> {

        /**
         * Constructs a new ImmediatelyFuture and runs the specified task.
         *
         * @param callable the {@link Callable} which shall be run
         * @throws NullPointerException if callable is null
         */
        private ImmediatelyFuture(Callable<T> callable) {
            super(callable);
            run();
        }

    }

    /**
     * This is a {@link java.util.concurrent.RunnableFuture}, which will
     * never perform any task.
     *
     * <p>Because the associated task is never run, the following applies:
     * <ul>
     *     <li>
     *         If this task was not cancelled
     *         {@link #get()} will cause the current Thread to sleep until
     *         it is interrupted.
     *     </li>
     *     <li>
     *         If this task was not cancelled {@link #get(long, TimeUnit)}
     *         will immediately throw a {@link TimeoutException} or
     *         an {@link InterruptedException} if the current Thread is
     *         interrupted.
     *     </li>
     *     <li>
     *         When called with false,
     *         {@link #cancel(boolean)} returns always false.
     *     </li>
     *     <li>
     *         {@link #isDone()} returns the same as {@link #isCancelled()}.
     *     </li>
     *     <li>
     *         {@link #hasRun()} returns always false.
     *     </li>
     *     <li>
     *         {@link #isExceptionHappened()} returns always false.
     *     </li>
     * </ul>
     *
     * @param <T> the result type of this Future
     */
    private static final class NeverDoneFuture<T>
            extends AbstractSequentialFuture<T> {

        /**
         * Constructs a new NeverDoneFuture.
         *
         * @param callable the {@link Callable} to never run
         * @throws NullPointerException if callable is null
         */
        private NeverDoneFuture(Callable<T> callable) {
            super(callable);
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            if (mayInterruptIfRunning) {
                return super.cancel(true);
            }
            return false;
        }

    }

    /**
     * This is a {@link java.util.concurrent.RunnableFuture}, which will
     * perform its associated tash when {@link #get()} is called.
     *
     * <p>Because the associated task finished "just in time",
     * the following applies:
     * <ul>
     *     <li>
     *         If this task was not cancelled and
     *         {@link #get()} was called, the same things apply as in
     *         {@link ImmediatelyFuture}.
     *     </li>
     *     <li>
     *         When called with false,
     *         {@link #cancel(boolean)} returns always false.
     *     </li>
     *     <li>
     *         If {@link #get()} has not been called,
     *         {@link #isDone()} returns the same as {@link #isCancelled()}.
     *     </li>
     * </ul>
     *
     * @param <T> the result type of this Future
     */
    private static final class OnCallFuture<T>
            extends AbstractSequentialFuture<T> {

        /**
         * Constructs a new OnCallFuture with the specified task.
         *
         * @param callable the {@link Callable} to run when get() is called.
         * @throws NullPointerException if callable is null
         */
        private OnCallFuture(Callable<T> callable) {
            super(callable);
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            if (mayInterruptIfRunning) {
                return super.cancel(true);
            }
            return false;
        }

        @Override
        public T get() throws ExecutionException, InterruptedException {
            run();
            return super.get();
        }

        @Override
        public T get(long timeout, TimeUnit timeUnit)
                throws ExecutionException, InterruptedException {
            return get();
        }

    }

    /**
     * Private constructor since this is a utility class.
     */
    private SequentialFutures() {
        super();
    }

    /**
     * Returns a Future which will have done its task.
     *
     * @param callable the task to execute
     * @param <T> the return type of callable
     * @return a Future containing the result of callable
     * @throws NullPointerException if callable is null
     * @see ImmediatelyFuture
     */
    static <T> SequentialFuture<T> getImmediately(Callable<T> callable) {
        return new ImmediatelyFuture<>(callable);
    }

    /**
     * Returns a Future which will run its task after get() is called.
     *
     * @param callable the task to execute
     * @param <T> the return type of callable
     * @return a Future running callable when get() is called
     * @throws NullPointerException if callable is null
     * @see OnCallFuture
     */
    static <T> SequentialFuture<T> getOnCall(Callable<T> callable) {
        return new OnCallFuture<>(callable);
    }

    /**
     * Returns a Future which will be never run.
     *
     * @param callable the task that will never be executed
     * @param <T> the return type of callable
     * @return a Future which will never be run
     * @throws NullPointerException if callable is null
     * @see NeverDoneFuture
     */
    static <T> SequentialFuture<T> getNeverDone(Callable<T> callable) {
        return new NeverDoneFuture<>(callable);
    }

}

/* vim:set shiftwidth=4 softtabstop=4 expandtab: */
