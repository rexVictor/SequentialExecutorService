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

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * Tests the ImmediatelyFuture class.
 */
public class ImmediatelyFutureTest extends SequentialFutureTest {

    /**
     * A null returning callable.
     */
    private final Callable<?> callable = () -> null;

    /**
     * Empty constructor.
     */
    public ImmediatelyFutureTest(){
        super();
    }

    @DataProvider(name = "booleans")
    public Object[][] getBooleans() {
        return new Object[][]{
                {true},
                {false}
        };
    }

    @Test(dataProvider = "booleans")
    public void cancel(boolean mayInterrupt) {
        Future<?> future = SequentialFutures.getImmediately(callable);
        Assert.assertFalse(future.isCancelled());
        Assert.assertFalse(future.cancel(mayInterrupt));
    }

}

/* vim:set shiftwidth=4 softtabstop=4 expandtab: */
