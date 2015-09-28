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

import java.util.concurrent.Callable;

/**
 * This is a Future, which performs the task it is constructed with immediately.
 *
 * @param <V> the result type of this Future
 */
class ImmediatelyFuture<V> extends AbstractSequentialFuture<V> {

    /**
     * Creates a new ImmediatelyFuture and runs the Callable.
     * @param callable the callable to run
     */
    ImmediatelyFuture(Callable<V> callable) {
        super(callable);
        run();
    }

    @Override
    public final void run() {
        super.run();
    }

}

/* vim:set shiftwidth=4 softtabstop=4 expandtab: */
