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

package rex.palace.sequentialexecutor;

import java.util.concurrent.TimeUnit;

/**
 * Classes implementing this interface can register to a {@link TimeController}
 * and get notified if simulated time passed.
 */
@FunctionalInterface
public interface TimeListener {

    /**
     * Callback method when time passed.
     *
     * @param time the simulated amount of time that has passed
     * @param unit the TimeUnit of time
     * @return if this TimeListener shall be unregistered of the calling TimeController
     * @throws NullPointerException if unit is null
     * @throws java.util.ConcurrentModificationException if this TimeListener
     *         tries to unregister from the calling TimeController via the
     *         {@link TimeController#unregister(TimeListener)} method. The
     *         correct way to unregister during the execution of this method,
     *         is to simply return false. The TimeController will unregister
     *         this listener then.
     */
    boolean timePassed(long time, TimeUnit unit);

}

/* vim:set shiftwidth=4 softtabstop=4 expandtab: */
