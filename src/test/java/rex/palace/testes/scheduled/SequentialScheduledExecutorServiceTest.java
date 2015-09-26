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

package rex.palace.testes.scheduled;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Tests the SequentialScheduledExecutorService class.
 */
public class SequentialScheduledExecutorServiceTest {

    /**
     * The TimeController this' tests can use.
     */
    private TimeController timeController;

    /**
     * The SequentialScheduledExecutorService to be tested.
     */
    private SequentialScheduledExecutorService service;

    /**
     * Empty Constructor.
     */
    public SequentialScheduledExecutorServiceTest() {
        super();
    }

    /**
     * Initializes the instance variables.
     */
    @BeforeMethod
    public void initializeInstanceVariables() {
        timeController = new TimeControllerImpl();
        service = new SequentialScheduledExecutorService(timeController);
    }

    @Test
    public void schedule_Callable() {
        ScheduledFuture<Void> future = service.schedule(() -> null, 10L, TimeUnit.NANOSECONDS);

        Assert.assertTrue(future instanceof DelayedSequentialFuture);
        Assert.assertEquals(future.getDelay(TimeUnit.NANOSECONDS), 10L);
    }

    @Test(expectedExceptions = RejectedExecutionException.class)
    public void schedule_Callable_shutdown() {
        service.shutdown();
        service.schedule(() -> null, 10L, TimeUnit.NANOSECONDS);
    }

    @Test(expectedExceptions = RejectedExecutionException.class)
    public void schedule_Runnable_shutdown() {
        service.shutdown();
        service.schedule(() -> { }, 10L, TimeUnit.NANOSECONDS);
    }

    @Test
    public void schedule_Runnable() {
        ScheduledFuture<?> future = service.schedule(() -> { } , 10L, TimeUnit.NANOSECONDS);

        Assert.assertTrue(future instanceof DelayedSequentialFuture);
        Assert.assertEquals(future.getDelay(TimeUnit.NANOSECONDS), 10L);
    }

    @Test
    public void scheduleAtFixedRate() {
        ScheduledFuture<?> future
                = service.scheduleAtFixedRate(() -> { }, 5L,
                10L, TimeUnit.NANOSECONDS);

        Assert.assertTrue(future instanceof DelayedPeriodicSequentialFuture);
        Assert.assertEquals(future.getDelay(TimeUnit.NANOSECONDS), 5L);

        timeController.letTimePass(5L, TimeUnit.NANOSECONDS);

        Assert.assertEquals(future.getDelay(TimeUnit.NANOSECONDS), 10L);
    }

    @Test
    public void scheduleAtFixedRate_noDelay() {
        ScheduledFuture<?> future
                = service.scheduleAtFixedRate(() -> { }, 0L,
                10L, TimeUnit.NANOSECONDS);

        Assert.assertFalse(future instanceof DelayedPeriodicSequentialFuture);
        Assert.assertTrue(future instanceof PeriodicSequentialFuture);
        Assert.assertEquals(future.getDelay(TimeUnit.NANOSECONDS), 10L);

    }

    @Test
    public void scheduleWithFixedDelay() {
        ScheduledFuture<?> future
                = service.scheduleWithFixedDelay(() -> { }, 5L,
                10L, TimeUnit.NANOSECONDS);

        Assert.assertTrue(future instanceof DelayedPeriodicSequentialFuture);
        Assert.assertEquals(future.getDelay(TimeUnit.NANOSECONDS), 5L);

        timeController.letTimePass(5L, TimeUnit.NANOSECONDS);

        Assert.assertEquals(future.getDelay(TimeUnit.NANOSECONDS), 10L);
    }

    @Test(expectedExceptions = RejectedExecutionException.class)
    public void scheduleWithFixedDelay_shutdown() {
        service.shutdown();
        service.scheduleWithFixedDelay(() -> { }, 5L, 10L, TimeUnit.NANOSECONDS);
    }

    @Test(expectedExceptions = RejectedExecutionException.class)
    public void scheduleAtFixedRate_shutdown() {
        service.shutdown();
        service.scheduleAtFixedRate(() -> { }, 5L, 10L, TimeUnit.NANOSECONDS);
    }

}

/* vim:set shiftwidth=4 softtabstop=4 expandtab: */
