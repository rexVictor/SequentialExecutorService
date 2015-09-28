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

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BooleanSupplier;

/**
 * This describes how to simulate a real time flow.
 *
 * <p>A TimeController is used by SequentialFutures, to perform tasks
 * when the correct amount of "time" has been passed according to the TimeController.
 */
public interface TimeController {

    /**
     * Calls all TimeListeners to notify them about passed time.
     *
     * @param time the simulated time that passes
     * @param unit the TimeUnit of time
     * @throws NullPointerException if unit is null
     */
    void letTimePass(long time, TimeUnit unit);

    /**
     * Registers a TimeListener to this TimeController.
     *
     * @param listener the TimeListener to be registered
     * @throws NullPointerException if listener is null
     */
    void register(TimeListener listener);

    /**
     * Unregisters a TimeListener from this TimeController.
     *
     * <p>This is usually done if a task finished or got cancelled.
     * @param listener the listener to be unregistered
     * @throws NullPointerException if listener is null
     * @throws IllegalArgumentException if listener is not registered (optional)
     */
    void unregister(TimeListener listener);

    /**
     * Lets the time pass until the condition evaluates to true.
     *
     * <p>Note: The calling Thread may be blocked until the condition
     * evaluates to true. If this never happens then this method
     * will never return. Calling tests should consider using a time out
     * to avoid a non termination.
     *
     * <p>Default Implementation: This method lets the time pass per
     * simulated nano second, so if the registered tasks needs much
     * more time (like milli seconds or even longer), the
     * test may run very long. Consider Overriding this method
     * if this is the case.
     *
     * @param condition the condition to wait until it evaluates to true
     * @throws NullPointerException if condition is null
     */
    default void letTimePassUntil(BooleanSupplier condition) {
        while (!condition.getAsBoolean()) {
            letTimePass(1L, TimeUnit.NANOSECONDS);
        }
    }

    /**
     * Lets the time pass until the condition evaluates to true or
     * the time out occurs.
     *
     * <p>Note: The calling Thread may be blocked until the condition
     * evaluates to true or the timeout occurs.
     *
     * <p>Default Implementation: This method lets the time pass per
     * simulated nano second, so if the registered tasks needs much
     * more time (like milli seconds or even longer), the
     * test may run very long. Consider Overriding this method
     * if this is the case.
     *
     * @param condition the condition to wait until it evaluates to true
     * @param time the maximum time to wait
     * @param unit the TimeUnit of time
     * @throws NullPointerException if condition or unit is null
     * @throws TimeoutException if the condition is still false after time passed
     */
    default void letTimePassUntil(
            BooleanSupplier condition, long time, TimeUnit unit)
            throws TimeoutException {
        long timeInNanos = unit.toNanos(time);
        long passedNanos = 0L;
        while (!condition.getAsBoolean()) {
            if (passedNanos == timeInNanos) {
                throw new TimeoutException(
                        "The condition did not evaluate to true in " + time
                                + ' ' + unit);
            }
            letTimePass(1L, TimeUnit.NANOSECONDS);
            passedNanos++;
        }
    }

}

/* vim:set shiftwidth=4 softtabstop=4 expandtab: */
