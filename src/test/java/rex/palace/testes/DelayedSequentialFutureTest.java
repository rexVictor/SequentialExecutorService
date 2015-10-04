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
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Tests the DelayedSequentialFuture class.
 */
public class DelayedSequentialFutureTest {

    /**
     * The TimeController usable by the tests.
     */
    private TimeController timeController;

    /**
     * The DelayedSequentialFuture to test.
     */
    private SequentialScheduledFuture<Void> future;

    /**
     * Empty constructor.
     */
    public DelayedSequentialFutureTest() {
        super();
    }

    /**
     * Initializes the instance variables.
     */
    @BeforeMethod
    public void initializeInstanceVariables() {
        timeController = TimeControllers.getInstance();
        future = SequentialScheduledFutures.getDelayed(() -> null, 10L,
                TimeUnit.MILLISECONDS, timeController);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void new_nullCallable() {
        SequentialScheduledFutures.getDelayed(
                null, 10L, TimeUnit.NANOSECONDS, timeController);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void new_nullUnit() {
        SequentialScheduledFutures.getDelayed(
                () -> null, 10L, null, timeController);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void new_nullController() {
        SequentialScheduledFutures.getDelayed(
                () -> null, 10L, TimeUnit.NANOSECONDS, null);
    }


    @Test(expectedExceptions = CancellationException.class)
    public void cancel() {
        SequentialScheduledFutureTests.cancel(
                future, 10L, TimeUnit.MILLISECONDS);
    }

    @Test
    public void ranAfterDelay() throws ExecutionException, InterruptedException {
        Assert.assertFalse(future.isDone());

        future.timePassed(9L, TimeUnit.MILLISECONDS);
        Assert.assertFalse(future.isDone());
        future.timePassed(1L, TimeUnit.MILLISECONDS);


        Assert.assertTrue(future.isDone());
        Assert.assertEquals(future.get(), null);

    }

    @Test
    public void getDelay() {
        SequentialScheduledFutureTests.getDelay(
                future, 10L, 1L, TimeUnit.MILLISECONDS);
        SequentialScheduledFutureTests.getDelay(
                future, 9L, 3L, TimeUnit.MILLISECONDS);
        SequentialScheduledFutureTests.getDelay(
                future, 6L, 3L, TimeUnit.MILLISECONDS);
    }

    @Test
    public void compareTo() {
        SequentialScheduledFutureTests.compareTo(
                future, 5L, TimeUnit.MILLISECONDS, 1);

        future.timePassed(5L, TimeUnit.MILLISECONDS);
        SequentialScheduledFutureTests.compareTo(
                future, 5L, TimeUnit.MILLISECONDS, 0);

        future.timePassed(1L, TimeUnit.MILLISECONDS);
        SequentialScheduledFutureTests.compareTo(
                future, 5L, TimeUnit.MILLISECONDS, -1);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void compareTo_null() {
        future.compareTo(null);
    }

    @Test
    public void get_limited() throws InterruptedException, ExecutionException, TimeoutException {
        SequentialScheduledFuture<Integer> integerFuture
                = SequentialScheduledFutures.getDelayed(() -> 5,
                10L, TimeUnit.MICROSECONDS, timeController);
        Assert.assertEquals(integerFuture.get(11L, TimeUnit.MICROSECONDS), Integer.valueOf(5));
    }

    @Test
    public void toString_running() {
        StringBuilder regexPattern = new StringBuilder();
        regexPattern.append("SequentialScheduledFuture\\[")
                .append("TimeController=")
                .append(".*")
                .append(",task=")
                .append(".*")
                .append(",state=")
                .append("running")
                .append(",remainingDelay=")
                .append(".*")
                .append(",initialDelay=")
                .append(".*")
                .append("\\]");
        Pattern pattern
                = Pattern.compile(regexPattern.toString());

        SequentialScheduledFuture<Void> future
                = SequentialScheduledFutures.getDelayed(
                () -> null, 10L, TimeUnit.NANOSECONDS, TimeControllers.getNop());

        Matcher matcher = pattern.matcher(future.toString());

        Assert.assertTrue(matcher.matches());
    }

}

/* vim:set shiftwidth=4 softtabstop=4 expandtab: */
