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
 * PeriodicSequentialFuture is a task which gets run
 * periodically.
 *
 * <p>Note: After creation this task has never been run.
 *
 * @param <T> the return type
 */
class PeriodicSequentialFuture<T> extends AbstractSequentialScheduledFuture<T> {

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
    protected PeriodicSequentialFuture(
            Callable<T> callable, long period,
            TimeUnit timeUnit, TimeController timeController) {
        super(callable, period, timeUnit, timeController);
        this.period = remainingDelay;
    }

    @Override
    public boolean timePassed(long time, TimeUnit unit) {
        super.timePassed(time, unit);
        long delay;
        while ((delay = getDelay(TimeUnit.NANOSECONDS)) <= 0) {
            run();
            if (didExceptionHappen()) {
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
        if (!isCancelled() && !didExceptionHappen()) {
            remainingDelay = period;
            ran = false;
        }
    }

}

/* vim:set shiftwidth=4 softtabstop=4 expandtab: */
