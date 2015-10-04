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
import rex.palace.testhelp.ArgumentConverter;
import rex.palace.testhelp.UtilityCheck;

import java.util.Iterator;

/**
 * Tests if classes are utility classes.
 */
public class UtilityTest {

    @DataProvider(name = "utilityClasses")
    public Iterator<Object[]> getUtilityClasses() {
        return ArgumentConverter.convert(
                ExecutorServiceHelper.class,
                SequentialFutures.class,
                SequentialScheduledFutures.class,
                TimeControllers.class);
    }

    @Test(dataProvider = "utilityClasses")
    public void testIfUtility(Class<?> clazz) {
        Assert.assertTrue(UtilityCheck.isUtilityClass(clazz));
    }

}
