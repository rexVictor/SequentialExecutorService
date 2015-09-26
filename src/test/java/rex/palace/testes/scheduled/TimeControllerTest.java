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

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Tests the TimeControllerImpl class.
 */
public class TimeControllerTest {

    /**
     * A mock TimeListener which just counts the passed time.
     */
    private static class TimeListenerMock implements TimeListener {

        /**
         * The passed time in nano seconds.
         */
        public long passedTimeInNanos = 0L;

        /**
         * Creates a new TimeListenerMock.
         */
        TimeListenerMock() {
            super();
        }

        @Override
        public boolean timePassed(long time, TimeUnit unit) {
            passedTimeInNanos += unit.toNanos(time);
            return false;
        }

    }

    /**
     * The TimeController this' tests can use.
     */
    private TimeController timeController;

    /**
     * The TimeListenerMock this' tests can use.
     */
    private TimeListenerMock timeListenerMock;

    /**
     * Empty constructor.
     */
    public TimeControllerTest() {
        super();
    }

    /**
     * Initializes the instance variables.
     */
    @BeforeMethod
    public void initializeInstanceVariables() {
        timeController = new TimeControllerImpl();
        timeListenerMock = new TimeListenerMock();
        timeController.register(timeListenerMock);
    }

    @Test(timeOut = 1000L)
    public void letTimePass() {
        timeController.letTimePass(10L, TimeUnit.NANOSECONDS);

        Assert.assertEquals(timeListenerMock.passedTimeInNanos, 10L);
    }

    @Test(timeOut = 1000L)
    public void letTimePassUntil() {
        TimeListenerMock timeListenerMock2 = new TimeListenerMock();
        timeController.register(timeListenerMock2);
        timeController.letTimePassUntil(() -> timeListenerMock.passedTimeInNanos == 100L);

        Assert.assertEquals(timeListenerMock.passedTimeInNanos, 100L);
        Assert.assertEquals(timeListenerMock2.passedTimeInNanos, 100L);
    }

    @Test(timeOut = 1000L, expectedExceptions = TimeoutException.class)
    public void letTimePassUntil_limited_TimeOutException() throws TimeoutException {
        timeController.letTimePassUntil(
                () -> timeListenerMock.passedTimeInNanos == 100L,
                99L, TimeUnit.NANOSECONDS);
    }

    @Test(timeOut = 1000L)
    public void letTimePassUntil_limited() throws TimeoutException {
        TimeListenerMock timeListenerMock2 = new TimeListenerMock();
        timeController.register(timeListenerMock2);
        timeController.letTimePassUntil(
                () -> timeListenerMock.passedTimeInNanos == 100L,
                100L, TimeUnit.NANOSECONDS);

        Assert.assertEquals(timeListenerMock.passedTimeInNanos, 100L);
        Assert.assertEquals(timeListenerMock2.passedTimeInNanos, 100L);
    }

    @Test
    public void unregister() {
        timeController.letTimePass(10L, TimeUnit.NANOSECONDS);
        Assert.assertEquals(timeListenerMock.passedTimeInNanos, 10L);
        timeController.unregister(timeListenerMock);

        timeController.letTimePass(10L, TimeUnit.NANOSECONDS);
        Assert.assertEquals(timeListenerMock.passedTimeInNanos, 10L);

    }

}

/* vim:set shiftwidth=4 softtabstop=4 expandtab: */
