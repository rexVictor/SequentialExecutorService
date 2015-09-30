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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * An extension of Future used for testing under non parallel conditions.
 *
 * @param <T> the type of the result
 */
interface SequentialFuture<T> extends RunnableFuture<T> {

    /**
     * Returns if this task has already been run.
     * @return true if and only if has been run.
     */
    boolean hasRun();

    /**
     * Returns if an exception occurred during the run.
     * @return false if this task has never been run or has
     *         been run without exceptions and true otherwise
     */
    boolean isExceptionHappened();

    /**
     * Callback method used by {@link CallableWrapper}, which is
     * called if an exception occurred during the run.
     *
     * @param exception the exception which occurred. It is never null.
     */
    void setException(Exception exception);

    /**
     * Callback method used by {@link CallableWrapper}, which is
     * called if the run was successful.
     *
     * @param result the result of this task
     */
    void setResult(T result);

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation just delegates to {@link #get()}.
     *
     */
    @Override
    default T get(long timeout, TimeUnit unit)
            throws ExecutionException, InterruptedException, TimeoutException {
        return get();
    }

}

/* vim:set shiftwidth=4 softtabstop=4 expandtab: */
