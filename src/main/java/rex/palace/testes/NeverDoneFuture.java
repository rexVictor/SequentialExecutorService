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
import java.util.concurrent.ExecutionException;

/**
 * A Future implementation for the SequentialExecutionService.
 * @param <T> the type this future holds.
 */
public class NeverDoneFuture<T> extends SequentialFuture<T> {

    /**
     * Creates a new NeverDoneFuture.
     * @param callable the callable to never run
     */
    public NeverDoneFuture(Callable<T> callable) {
        super(callable);
    }

    @Override
    public T get() throws ExecutionException {
        throw new ExecutionException("I am never done!", null);
    }

}

/* vim:set shiftwidth=4 softtabstop=4 expandtab: */
