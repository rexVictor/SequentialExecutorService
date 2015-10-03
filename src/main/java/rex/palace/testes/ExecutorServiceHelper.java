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
import java.util.concurrent.Future;

/**
 * A utility class for the SequentialExecutorService.
 */
final class ExecutorServiceHelper {

    static final String CANCELLATION_MESSAGE = "Task was cancelled.";

    static final String INTERUPPTED_MESSAGE
            = "Interrupted before the result was ready.";

    /**
     * Empty constructor since this is a utility class.
     */
    private ExecutorServiceHelper() {
        super();
    }

    /**
     * Converts the specified {@link Runnable} to a {@link Callable} returning
     * the specified result.
     *
     * @param runnable the Runnable to convert
     * @param result the result the returned Callable shall return
     * @param <T> the type of result
     * @return a Callable calling runnable and returning result
     */
    static <T> Callable<T> convert(Runnable runnable, T result) {
        return () -> {
            runnable.run();
            return result;
        };
    }

    /**
     * Checks if the specified {@link Future} terminated regularly, e.g.
     * without throwing an Exception.
     *
     * @param future the Future to test for regularly completion.
     * @return false if and only if calling {@link Future#get()} throws an
     *         exception.
     */
    static boolean isRegularlyDone(Future<?> future) {
        try {
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            return false;
        }
        return true;
    }

}

/* vim:set shiftwidth=4 softtabstop=4 expandtab: */
