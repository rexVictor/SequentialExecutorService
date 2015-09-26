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
import java.util.concurrent.ExecutionException;

/**
 * This is a Future, which performs the task it is constructed with when get() is called.
 *
 * @param <V> the result type of this Future
 */
public class OnCallFuture<V> extends AbstractSequentialFuture<V> {

    /**
     * Creates a new OnCallFuture.
     * @param callable the callable to run when get() is called.
     */
    public OnCallFuture(Callable<V> callable) {
        super(callable);
    }

    @Override
    public V get() throws ExecutionException, InterruptedException {
        run();
        return super.get();
    }

}

/* vim:set shiftwidth=4 softtabstop=4 expandtab: */
