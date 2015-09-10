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

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * This class decorates a DelayedSequentialFuture when used in a
 * DelayedPeriodicSequentialFuture.
 *
 * <p>It substitutes itself with a PeriodicSequentialFuture
 * after the initial delay has passed.
 *
 * @param <T> the result type of this Future.
 */
class DelayedDecorator<T> extends DelayedSequentialFuture<T> {

    /**
     * The DelayedPeriodicSequentialFuture where to replace this Future
     * with a PeriodicSequentialFuture after the initial delay.
     */
    private final DelayedPeriodicSequentialFuture<T> future;

    /**
     * The callable to call.
     */
    private final Callable<T> callable;

    /**
     * The period for rescheduling after initial delay in nanoseconds.
     */
    private final long period;

    /**
     * Creates a new DelayedDecorator.
     *
     * @param callable the task to run
     * @param initialDelay the initial delay in unit for this task
     * @param period the period for rescheduling
     * @param unit the unit of initialDelay and period
     * @param future the DelayedPeriodicSequentialFuture, which created
     *        this future.
     */
    DelayedDecorator(Callable<T> callable, long initialDelay,
                            long period, TimeUnit unit,
                            DelayedPeriodicSequentialFuture<T> future) {
        super(callable, initialDelay, unit);
        this.callable = callable;
        this.future = future;
        this.period = unit.toNanos(period);
    }

    @Override
    public void timePassed(long time, TimeUnit unit) {
        super.timePassed(time, unit);
        long delay = getDelay(TimeUnit.NANOSECONDS);
        if (delay <= 0) {
            SequentialPeriodicFuture<T> periodicFuture =
                    new SequentialPeriodicFuture<>(callable, period, TimeUnit.NANOSECONDS);
            future.setFuture(periodicFuture);
            periodicFuture.timePassed(-delay, TimeUnit.NANOSECONDS);
        }
    }
}

/* vim:set shiftwidth=4 softtabstop=4 expandtab: */
