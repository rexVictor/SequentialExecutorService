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
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * An API breaking implementation of ScheduledExecutorService.
 */
public class SequentialScheduledExecutorService
         extends SequentialExecutorService
         implements ScheduledExecutorService {

    /**
     * How often periodic tasks are executed.
     */
    private int callCount = 0;

    /**
     * Creates a new SequentialScheduledExecutorService.
     */
    public SequentialScheduledExecutorService() {
    }

    @Override
    public <V> ScheduledFuture<V> schedule(Callable<V> callable,
            long delay, TimeUnit unit) {
        return null;
    }

    @Override
    public ScheduledFuture<?> schedule(Runnable command,
            long delay, TimeUnit unit) {
        return null;
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command,
            long initialDelay, long period, TimeUnit unit) {
        return scheduleWithFixedDelay(command, initialDelay, period, unit);
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command,
            long initialDelay, long delay, TimeUnit unit) {
        for (int i = 0; i < callCount; i++) {
            try {
                command.run();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    /**
     * Sets the count how often periodic schedules are called.
     *
     * @param callCount how often periodic schedules are called
     * @throws IllegalArgumentException if callCount is negative
     */
    public void setCallCount(int callCount) {
        if (callCount < 0) {
            throw new IllegalArgumentException();
        }
        this.callCount = callCount;
    }


}

/* vim:set shiftwidth=4 softtabstop=4 expandtab: */
