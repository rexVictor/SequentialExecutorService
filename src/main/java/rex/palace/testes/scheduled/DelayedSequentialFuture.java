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

package rex.palace.testes.scheduled;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * A Future which is run after an initial delay.
 *
 * @param <T> the return type of this Future.
 */
public class DelayedSequentialFuture<T> extends AbstractSequentialScheduledFuture<T> {

    /**
     * Creates a new DelayedSequentialFuture.
     *
     * @param callable the task to run
     * @param initialDelay the initial delay to wait
     * @param unit the TimeUnit of initialDelay
     * @param timeController the timeController to register to
     * @throws NullPointerException if callable, unit or timeController is null
     * @throws IllegalArgumentException if initialDelay is not positive
     */
    public DelayedSequentialFuture(
            Callable<T> callable, long initialDelay,
            TimeUnit unit, TimeController timeController) {
        super(callable, initialDelay, unit, timeController);
    }

    @Override
    public boolean timePassed(long time, TimeUnit unit) {
        super.timePassed(time, unit);
        if (remainingDelay <= 0) {
            run();
            return true;
        }
        return false;
    }

}

/* vim:set shiftwidth=4 softtabstop=4 expandtab: */
