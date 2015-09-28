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

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * An implementation of the TimeController interface.
 *
 * <p>Since the main purpose of this project is to test, this
 * implementation is not optimized for speed. So if you are
 * testing tasks with delays in range of milli seconds or even
 * days, be aware that this implementation may run even longer in
 * real time. If speed is of concern for your tests consider
 * implementing the TimeController interface according to your needs.
 */
public class TimeControllerImpl implements TimeController {

    /**
     * The registered TimeListeners.
     */
    private final Set<TimeListener> listeners = new HashSet<>();

    /**
     * Creates a new TimeController.
     */
    protected TimeControllerImpl() {
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
        return super.toString() + "[TimeListener = " + listeners + "]";
    }

    /**
     * Returns a new instance of TimeControllerImpl.
     * @return a new instance of TimeControllerImpl
     */
    static TimeController newInstance() {
        return new TimeControllerImpl();
    }

}

/* vim:set shiftwidth=4 softtabstop=4 expandtab: */
