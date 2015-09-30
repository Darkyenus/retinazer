/*******************************************************************************
 * Copyright (C) 2015 Anton Gustafsson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package com.github.antag99.retinazer.beam.component;

import com.badlogic.gdx.utils.LongMap;
import com.github.antag99.retinazer.Component;
import com.github.antag99.retinazer.EntitySet;

/**
 * Tracks the properties of a room. This includes the active partitions.
 *
 * Partitions are dynamically created when an entity enters a new region of the
 * room, and destroyed when there are no partitions available.
 */
public final class Room implements Component {

    /**
     * Mapping of partition position to the entities contained in a partition.
     * A position is converted to {@code long} by {@code (x << 32) | y}
     */
    public LongMap<EntitySet> partitions = new LongMap<>();

    private static long toLong(int x, int y) {
        return ((long) x << 32) | (long) y;
    }

    public EntitySet getPartition(int x, int y) {
        return partitions.get(toLong(x, y));
    }

    public void setPartition(int x, int y, EntitySet set) {
        if (set != null)
            partitions.put(toLong(x, y), set);
        else
            partitions.remove(toLong(x, y));

    }
}
