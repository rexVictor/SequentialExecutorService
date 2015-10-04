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

/**
 * This package provides API breaking implementations of
 * {@link java.util.concurrent.ExecutorService} and
 * {@link java.util.concurrent.ScheduledExecutorService}.
 *
 * <p>The implementations don't do the tasks parallel but sequential.
 *
 * <p>These implementations can be used to test implementations using
 * instances of ExecutorService or ScheduledExecutorService under non-parallel,
 * deterministic conditions.
 *
 * <p>Although this undermines the purpose of these interfaces, this
 * implementation tries to be very accurate regarding handling of
 * interruptions, so that every scenario can be triggered and tested.
 *
 * <p>The classes concerning ExecutorService are
 * {@link rex.palace.sequentialexecutor.SequentialExecutorService} and
 * {@link rex.palace.sequentialexecutor.ExecutorServiceState}. The former
 * adds also a few methods which can be used for mocks. The latter is used
 * to simulate real conditions.
 *
 * <p>The classes concerning ScheduledExecutorService are
 * {@link rex.palace.sequentialexecutor.SequentialScheduledExecutorService} and
 * {@link rex.palace.sequentialexecutor.TimeController}. The former also has
 * the mock methods. The latter is an Interface, for which instances
 * can be obtained by {@link rex.palace.sequentialexecutor.TimeControllers}, which
 * simulates a real time flow deterministically.
 *
 * @see rex.palace.sequentialexecutor.SequentialExecutorService
 * @see rex.palace.sequentialexecutor.SequentialScheduledExecutorService
 * @see rex.palace.sequentialexecutor.TimeController
 * @see rex.palace.sequentialexecutor.TimeControllers
 * @see rex.palace.sequentialexecutor.ExecutorServiceState
 */
package rex.palace.sequentialexecutor;

/* vim:set shiftwidth=4 softtabstop=4 expandtab: */
