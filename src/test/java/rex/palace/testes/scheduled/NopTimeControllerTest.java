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

import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Tests the NopTimeController class.
 *
 * <p>Since the NopTimeController does nothing, this class
 * only tests if any exception is thrown.
 */
public class NopTimeControllerTest {

    /**
     * The singleton instance of NopTimeController.
     */
    private final TimeController nopTimeController = NopTimeController.nopController;

    /**
     * Empty Constructor.
     */
    public NopTimeControllerTest() {
        super();
    }

    @Test
    public void test_NoExceptions() throws TimeoutException {
        nopTimeController.letTimePass(0L, TimeUnit.NANOSECONDS);
        nopTimeController.letTimePassUntil(() -> true);
        nopTimeController.letTimePassUntil(() -> true, 0L, TimeUnit.NANOSECONDS);
        nopTimeController.register((time, unit) -> false);
        nopTimeController.unregister((time, unit) -> false);
    }
}

/* vim:set shiftwidth=4 softtabstop=4 expandtab: */
