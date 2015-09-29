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
 * A utility class for thee SequentialExecutorService.
 */
final class ExecutorServiceHelper {

    /**
     * Empty constructor since this is a utility class.
     */
    private ExecutorServiceHelper() {
        super();
    }

    /**
     * Converts the Runnable to a Callable with specific result.
     *
     * @param runnable the runnable to convert
     * @param result the result the callable shall return
     * @param <T> the type of result
     * @return a callable calling runnable and returning result
     */
    static <T> Callable<T> convert(Runnable runnable, T result) {
        return () -> {
            runnable.run();
            return result;
        };
    }

    /**
     * Checks if future terminated regularly.
     * @param future the future to test for regularly completion.
     * @return false if and only if calling get() on future results in an exception.
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
