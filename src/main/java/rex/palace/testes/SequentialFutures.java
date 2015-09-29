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

/**
 * Static factory class for SequentialFutures.
 */
final class SequentialFutures {

    /**
     * This is a Future, which performs the task it is constructed with immediately.
     *
     * @param <V> the result type of this Future
     */
    private static class ImmediatelyFuture<V> extends AbstractSequentialFuture<V> {

        /**
         * Creates a new ImmediatelyFuture and runs the Callable.
         * @param callable the callable to run
         */
        ImmediatelyFuture(Callable<V> callable) {
            super(callable);
            run();
        }

        @Override
        public final void run() {
            super.run();
        }

    }

    /**
     * A Future implementation for the SequentialExecutionService.
     * @param <T> the type this future holds.
     */
    private static class NeverDoneFuture<T> extends AbstractSequentialFuture<T> {

        /**
         * Creates a new NeverDoneFuture.
         * @param callable the callable to never run
         */
        NeverDoneFuture(Callable<T> callable) {
            super(callable);
        }

        @Override
        public T get() throws ExecutionException {
            throw new ExecutionException("I am never done!", null);
        }

    }

    /**
     * This is a Future, which performs the task it is constructed with when get() is called.
     *
     * @param <V> the result type of this Future
     */
    private static class OnCallFuture<V> extends AbstractSequentialFuture<V> {

        /**
         * Creates a new OnCallFuture.
         * @param callable the callable to run when get() is called.
         */
        OnCallFuture(Callable<V> callable) {
            super(callable);
        }

        @Override
        public V get() throws ExecutionException, InterruptedException {
            run();
            return super.get();
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
     */
    static <T> SequentialFuture<T> getOnCall(Callable<T> callable) {
        return new OnCallFuture<>(callable);
    }

    /**
     * Returns a Future which will be never run.
     *
     * @param callable the task that will never be executed
     * @param <T> the return type of callable
     * @return a Future which will never be ready
     * @throws NullPointerException if callable is null
     */
    static <T> SequentialFuture<T> getNeverDone(Callable<T> callable) {
        return new NeverDoneFuture<>(callable);
    }

}

/* vim:set shiftwidth=4 softtabstop=4 expandtab: */
