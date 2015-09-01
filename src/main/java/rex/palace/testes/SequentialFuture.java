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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * A Future implementation for the SequentialExecutionService.
 * @param <T> the type this future holds.
 */
public class SequentialFuture<T> implements Future<T> {

    /**
     * The result this future holds.
     */
    private final T result;

    /**
     * The Exception which occured during the calculation.
     */
    private final Exception exception;

    /**
     * Creates a new SequentialFuture.
     *
     * @param result result to return
     */
    public SequentialFuture(T result) {
        this.result = result;
        this.exception = null;
    }

    /**
     * Creates a new SequentialFuture.
     *
     * @param exception the exception which happended during processing.
     */
    public SequentialFuture(Exception exception) {
        this.result = null;
        this.exception = exception;
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
    public boolean isDone() {
        return true;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean cancel(boolean interruptPossible) {
        return false;
    }
}

/* vim:set shiftwidth=4 softtabstop=4 expandtab: */
