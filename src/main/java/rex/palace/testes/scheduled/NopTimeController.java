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

import java.util.concurrent.TimeUnit;

/**
 * A NOOP implementation of TimeController.
 */
public final class NopTimeController implements TimeController {

    /**
     * Singleton instance of the NopTimeController.
     */
    public static final NopTimeController nopController = new NopTimeController();

    /**
     * Creates a new NopTimeController.
     */
    private NopTimeController() {
        super();
    }

    @Override
    public void letTimePass(long time, TimeUnit unit) {
        //does nothing
    }

    @Override
    public void register(TimeListener listener) {
        //does nothing
    }

    @Override
    public void unregister(TimeListener listener) {
        //does nothing
    }

}

/* vim:set shiftwidth=4 softtabstop=4 expandtab: */
