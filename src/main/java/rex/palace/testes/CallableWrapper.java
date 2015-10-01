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

/**
 * A {@link Runnable} implementation wrapping a {@link Callable} for a
 * {@link SequentialFuture}.
 *
 * @param <T> the result type of the wrapped Callable
 */
class CallableWrapper<T> implements Runnable {

    /**
     * The SequentialFuture callbacks are done to.
     */
    private final SequentialFuture<T> sequentialFuture;

    /**
     * The Callable being wrapped.
     */
    private final Callable<T> callable;

    /**
     * Constructs a new CallableWrapper running the specified {@link Callable}.
     *
     * @param sequentialFuture the {@link SequentialFuture} callbacks
     *                         are done to
     * @param callable the Callable to run
     * @throws NullPointerException if sequentialFuture or callable is null
     */
    CallableWrapper(
            SequentialFuture<T> sequentialFuture,
            Callable<T> callable) {
        this.sequentialFuture = Objects.requireNonNull(
                sequentialFuture,
                "The sequentialFuture must not be null.");
        this.callable = Objects.requireNonNull(callable,
                "The callable must not be null.");
    }

    /**
     * Runs {@link #callable} if {@link #sequentialFuture} is not cancelled and
     * calls the callback commands of sequentialFuture when the result is ready
     * or an exception occurred.
     *
     * @throws java.util.concurrent.CancellationException if sequentialFuture
     *         is cancelled
     */
    @Override
    public void run() {
        if (sequentialFuture.isCancelled()) {
            ExecutorServiceHelper.throwCancellationException();
        }
        try {
            T call = callable.call();
            sequentialFuture.setResult(call);
        } catch (Exception e) {
            sequentialFuture.setException(e);
        }
    }

    /**
     * Returns the String representation of {@link #callable}.
     *
     * @return {@code callable.toString()}
     */
    @Override
    public String toString() {
        return callable.toString();
    }

}

/* vim:set shiftwidth=4 softtabstop=4 expandtab: */
