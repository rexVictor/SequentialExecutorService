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

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;

/**
 * A Future implementation for the SequentialExecutionService.
 * @param <T> the type this future holds.
 */
public abstract class SequentialFuture<T> implements RunnableFuture<T> {

    /**
     * Indicates if this task has been cancelled.
     */
    protected boolean cancelled = false;

    /**
     * Indicates if this task has been run.
     */
    protected boolean ran = false;

    /**
     * The result this future holds.
     */
    private T result;

    /**
     * The Exception which occurred during the calculation.
     */
    private Exception exception;

    /**
     * The Callable creating this Future.
     */
    private final Callable<T> callable;

    /**
     * Creates a new SequentialFuture.
     *
     * @param callable the task to run
     */
    public SequentialFuture(Callable<T> callable) {
        this.callable = Objects.requireNonNull(callable);
    }

    @Override
    public T get(long timeOut, TimeUnit unit) throws ExecutionException {
        return get();
    }

    @Override
    public T get() throws ExecutionException {
        if (exception == null) {
            return result;
        }
        throw new ExecutionException(exception);
    }

    @Override
    public void run() {
        if (cancelled) {
            throw new CancellationException();
        }
        ran = true;
        try {
            result = callable.call();
        } catch (Exception e) {
            exception = e;
        }
    }

    @Override
    public boolean isDone() {
        return ran || cancelled;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public boolean cancel(boolean interruptPossible) {
        if (cancelled || ran) {
            return false;
        }
        cancelled = true;
        return true;
    }
}

/* vim:set shiftwidth=4 softtabstop=4 expandtab: */
