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

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;

/**
 * Static factory class for TimeControllers.
 */
public final class TimeControllers {

    /**
     * A NOOP implementation of TimeController.
     */
    private static final class NopTimeController implements TimeController {

        /**
         * Creates a new NopTimeController.
         */
        private NopTimeController() {
            super();
        }

        @Override
        public void letTimePass(long time, TimeUnit unit) {
            //does nothing
        }

        @Override
        public void register(TimeListener listener) {
            //does nothing
        }

        @Override
        public void unregister(TimeListener listener) {
            //does nothing
        }

        @Override
        public void letTimePassUntil(BooleanSupplier condition) {
            //does nothing
        }

        @Override
        public void letTimePassUntil(
                BooleanSupplier condition, long time, TimeUnit unit) {
            //does nothing
        }

    }

    /**
     * An implementation of the TimeController interface.
     */
    private static final class TimeControllerImpl implements TimeController {

        /**
         * The registered TimeListeners.
         */
        private final Collection<TimeListener> listeners = new HashSet<>();

        /**
         * Creates a new TimeController.
         */
        private TimeControllerImpl() {
            super();
        }

        @Override
        public void letTimePass(long time, TimeUnit unit) {
            Set<TimeListener> toRemove =
                    listeners.stream().filter(
                            listener -> listener.timePassed(time, unit)
                    ).collect(Collectors.toSet());
            listeners.removeAll(toRemove);
        }

        @Override
        public void register(TimeListener listener) {
            listeners.add(Objects.requireNonNull(listener));
        }

        @Override
        public void unregister(TimeListener listener) {
            listeners.remove(Objects.requireNonNull(listener));
        }

        @Override
        public String toString() {
            return super.toString() + "[TimeListener=" + listeners + ']';
        }

    }

    /**
     * The single instance of the NopTimeController.
     */
    private static final TimeController NOP_TIME_CONTROLLER
            = new NopTimeController();

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
        return NOP_TIME_CONTROLLER;
    }

    /**
     * Returns a TimeController not optimized for speed.
     * @return an API conform implementation of TimeController
     * @see TimeControllerImpl
     */
    public static TimeController getInstance() {
        return new TimeControllerImpl();
    }

}

/* vim:set shiftwidth=4 softtabstop=4 expandtab: */
