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

import org.testng.Assert;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * Tests common things to all SequentialScheduledFutures.
 */
public final class SequentialScheduledFutureTests {

    private SequentialScheduledFutureTests() {
        super();
    }

    static void cancel(SequentialScheduledFuture<?> future,
                                 long time, TimeUnit unit) {
        Assert.assertFalse(future.isCancelled());

        Assert.assertTrue(future.cancel(true));
        Assert.assertTrue(future.isCancelled());
        Assert.assertFalse(future.cancel(true));

        future.timePassed(time, unit);
    }

    static void getDelay(
            SequentialScheduledFuture<?> future,
            long initialDelay, long toPass, TimeUnit unit) {
        Assert.assertEquals(future.getDelay(unit), initialDelay);

        future.timePassed(toPass, unit);
        Assert.assertEquals(future.getDelay(unit), initialDelay - toPass);
    }

    static void compareTo(
            Delayed delayed, long delay, TimeUnit unit, int expected) {
        DelayedStub stub = new DelayedStub();
        stub.delayInNanos = unit.toNanos(delay);
        int result = delayed.compareTo(stub);
        Assert.assertEquals(result, expected);
    }

    /**
     * A stub implementation of Delayed.
     */
    private static class DelayedStub implements Delayed {

        /**
         * The current delay in nano seconds.
         */
        public long delayInNanos;

        /**
         * Creates a new DelayedStub.
         */
        DelayedStub() {
            super();
        }

        @Override
        public long getDelay(TimeUnit unit) {
            return unit.convert(delayInNanos, TimeUnit.NANOSECONDS);
        }

        @Override
        public int compareTo(Delayed other) {
            throw new UnsupportedOperationException();
        }
    }
}
