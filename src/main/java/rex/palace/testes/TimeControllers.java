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

/**
 * Static factory class for TimeControllers.
 */
public final class TimeControllers {

    /**
     * Private constructor since this is a utility class.
     */
    private TimeControllers() {
        super();
    }

    /**
     * Returns a TimeController which does absolutely nothing.
     * @return a nop implementation of the TimeController interface
     */
    public static TimeController getNop() {
        return NopTimeController.nopController;
    }

    /**
     * Returns a TimeController not optimized for speed.
     * @return an API conform implementation of TimeController
     * @see TimeControllerImpl
     */
    public static TimeController getInstance() {
        return TimeControllerImpl.newInstance();
    }
}

/* vim:set shiftwidth=4 softtabstop=4 expandtab: */
