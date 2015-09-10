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

import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * A ScheduledFuture which does not run parallel.
 *
 * @param <T> the return type of this Future
 */
public interface SequentialScheduledFuture<T> extends RunnableFuture<T>, ScheduledFuture<T> {

    /**
     * Notifies this task about passed time. It simulates real passed time for
     * testing purposes.
     *
     * @param time the amount of time that has been passed
     * @param unit the TimeUnit of time
     */
    void timePassed(long time, TimeUnit unit);

}

/* vim:set shiftwidth=4 softtabstop=4 expandtab: */
