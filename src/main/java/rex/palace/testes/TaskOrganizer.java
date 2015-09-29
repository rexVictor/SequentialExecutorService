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

import java.util.Collection;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.RunnableFuture;
import java.util.stream.Stream;

/**
 * Organizes submitted tasks.
 */
class TaskOrganizer {

    private final Map<ExecutorServiceState, Collection<RunnableFuture<?>>>
    tasks = new EnumMap<>(ExecutorServiceState.class);

    TaskOrganizer() {
        for (ExecutorServiceState state : ExecutorServiceState.values()) {
            tasks.put(state, new HashSet<>());
        }
    }

    <T> Future<T> submit(
            ExecutorServiceState state, Callable<T> callable) {
        RunnableFuture<T> future = state.submit(callable);
        Collection<RunnableFuture<?>> collection = tasks.get(state);
        collection.add(future);
        return future;
    }

    /**
     * Returns a stream of all unfinished tasks.
     *
     * @return a stream of all unfinished tasks.
     */
    Stream<? extends Runnable> notFinishedTasks() {
        Stream<? extends Runnable> onCallDone
                = tasks.get(ExecutorServiceState.ONCALL).stream()
                .filter(runnableFuture -> !runnableFuture.isDone());
        Stream<? extends Runnable> awaitTermination
                = tasks.get(ExecutorServiceState.AWAIT_TERMINATION).stream()
                .filter(runnableFuture -> !runnableFuture.isDone());
        Stream<? extends Runnable> neverDone
                = tasks.get(ExecutorServiceState.NEVER).stream();
        return Stream.concat(Stream.concat(onCallDone, neverDone),
                awaitTermination);
    }

    /**
     * Returns the number of all submitted tasks.
     *
     * @return the number of all submitted tasks
     */
    int submittedTasksCount() {
        return tasks.values().stream().mapToInt(Collection::size).sum();
    }

    /**
     * Returns the number of all finsihed tasks.
     *
     * @return the number of all finished tasks
     */
    int finishedTasksCount() {
        return submittedTasksCount() - (int) notFinishedTasks().count();
    }

    /**
     * Returns the number of all not finished tasks.
     *
     * @return the number of all not finished tasks
     */
    int notFinishedTasksCount() {
        return (int) notFinishedTasks().count();
    }

    void awaitTermination() {
        tasks.get(ExecutorServiceState.AWAIT_TERMINATION).stream()
                .forEach(ExecutorServiceHelper::isRegularlyDone);
    }

}

/* vim:set shiftwidth=4 softtabstop=4 expandtab: */
