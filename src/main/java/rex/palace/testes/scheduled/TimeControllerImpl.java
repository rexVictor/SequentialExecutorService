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

package rex.palace.testes.scheduled;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by rex on 9/11/15.
 */
public class TimeControllerImpl implements TimeController {

    /**
     * The registered TimeListeners.
     */
    private final Set<TimeListener> listeners = new HashSet<>();

    /**
     * Creates a new TimeController.
     */
    public TimeControllerImpl() {
        super();
    }

    @Override
    public void letTimePass(long time, TimeUnit unit) {
        listeners.removeAll(
                listeners.stream().filter(
                        listener -> listener.timePassed(time, unit)
                ).collect(Collectors.toSet()));
    }

    @Override
    public void register(TimeListener listener) {
        listeners.add(Objects.requireNonNull(listener));
    }

    @Override
    public void unregister(TimeListener listener) {
        listeners.remove(Objects.requireNonNull(listener));
    }

}

/* vim:set shiftwidth=4 softtabstop=4 expandtab: */
