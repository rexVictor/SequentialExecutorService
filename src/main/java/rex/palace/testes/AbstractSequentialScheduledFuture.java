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
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * An abstract base implementation of a SequentialScheduledFuture.
 *
 * @param <T> the result type of this Future
 */
public abstract class AbstractSequentialScheduledFuture<T> extends SequentialFuture<T>
        implements SequentialScheduledFuture<T> {

    /**
     * The initial delay for this task. Subclasses may use this as period.
     */
    protected final long initialDelay;

    /**
     * The remaining time before this task gets executed.
     */
    protected long remainingDelay;

    /**
     * Creates a new AbstractSequentialScheduledFuture.
     *
     * @param callable the task to run
     * @param initialDelay the initial delay for this task
     * @param unit the time unit of initialDelay
     */
    public AbstractSequentialScheduledFuture(
            Callable<T> callable, long initialDelay, TimeUnit unit) {
        super(callable);
        this.initialDelay = unit.toNanos(initialDelay);
    }

    @Override
    public boolean isDone() {
        return cancelled;
    }

    @Override
    public void run() {
        super.run();
        if (exception != null) {
            cancel(false);
        }
    }

    @Override
    public boolean cancel(boolean interruptPossible) {
        if (cancelled) {
            return false;
        }
        cancelled = true;
        return true;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(remainingDelay, TimeUnit.NANOSECONDS);
    }

    @Override
    public int compareTo(Delayed other) {
        long diff = remainingDelay - other.getDelay(TimeUnit.NANOSECONDS);
        return diff < 0 ? -1 : diff > 0 ? 1 : 0;
    }

}

/* vim:set shiftwidth=4 softtabstop=4 expandtab: */
