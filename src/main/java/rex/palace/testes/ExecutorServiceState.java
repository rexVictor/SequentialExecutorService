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
import java.util.concurrent.RunnableFuture;

/**
 * The state a SequentialExecutorService can be in.
 */
public enum ExecutorServiceState {

    /**
     * Performs submitted tasks immediately.
     */
    IMMEDIATELY(SequentialFutures::getImmediately),

    /**
     * Performs submitted tasks when get() is called
     * on the Future.
     */
    ONCALL(SequentialFutures::getOnCall),

    /**
     * Never performs the submitted tasks.
     */
    NEVER(SequentialFutures::getNeverDone),

    /**
     * Performs the submitted tasks when awaitTermination() is called.
     */
    AWAIT_TERMINATION(SequentialFutures::getOnCall);

    /**
     * An abstract factory interface for Futures.
     */
    @FunctionalInterface
    private interface FutureFactory {

        /**
         * Creates a RunnableFuture for callable.
         *
         * @param callable the task to construct a future for
         * @param <V> the return type of callable
         * @return a RunnableFuture for callable
         */
        <V> RunnableFuture<V> build(Callable<V> callable);

    }

    /**
     * The FutureFactory to build Futures.
     */
    private final transient FutureFactory factory;

    /**
     * Constructs an ExecutorServiceState.
     *
     * @param factory the FutureFactory to use
     */
    ExecutorServiceState(FutureFactory factory) {
        this.factory = factory;
    }


    /**
     * Returns a RunnableFuture behaving according to this ExecutorServiceState.
     *
     * @param callable the callable this future shall handle
     * @param <V> the return type of callable
     * @return a RunnableFuture handling callable
     */
    <V> RunnableFuture<V> submit(Callable<V> callable) {
        return factory.build(callable);
    }

}

/* vim:set shiftwidth=4 softtabstop=4 expandtab: */