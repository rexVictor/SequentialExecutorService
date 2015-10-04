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
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class TaskOrganizerTest {

    private TaskOrganizer taskOrganizer; /* = new TaskOrganizer(); */

    @BeforeMethod
    public void initializeInstanceVariables() {
        taskOrganizer = new TaskOrganizer();
    }

    @Test
    public void notFinishedTasks_empty() {
        Assert.assertEquals(taskOrganizer.notFinishedTasks().count(), 0L);
    }

    @Test
    public void netFinishedTasks_notEmpty_notDone() {
        Callable<Void> callable = () -> null;
        taskOrganizer.submit(ExecutorServiceState.ONCALL, callable);
        Assert.assertEquals(taskOrganizer.notFinishedTasks().count(), 1L);
    }

    @Test
    public void netFinishedTasks_notEmpty_done()
            throws ExecutionException, InterruptedException {
        Callable<Void> callable = () -> null;
        Future<Void> future =
                taskOrganizer.submit(ExecutorServiceState.ONCALL, callable);
        future.get();
        Assert.assertEquals(taskOrganizer.notFinishedTasks().count(), 0L);
    }

    @Test
    public void netFinishedTasks_await_notEmpty_notDone() {
        Callable<Void> callable = () -> null;
        taskOrganizer.submit(ExecutorServiceState.AWAIT_TERMINATION, callable);
        Assert.assertEquals(taskOrganizer.notFinishedTasks().count(), 1L);
    }

    @Test
    public void netFinishedTasks_await_notEmpty_done()
            throws ExecutionException, InterruptedException {
        Callable<Void> callable = () -> null;
        Future<Void> future =
                taskOrganizer.submit(
                        ExecutorServiceState.AWAIT_TERMINATION, callable);
        future.get();
        Assert.assertEquals(taskOrganizer.notFinishedTasks().count(), 0L);
    }

}

/* vim:set shiftwidth=4 softtabstop=4 expandtab: */
