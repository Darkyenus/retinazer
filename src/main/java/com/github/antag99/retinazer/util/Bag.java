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
package com.github.antag99.retinazer.util;

import com.badlogic.gdx.math.MathUtils;

/** Generic dynamically sized container for objects.
 * Accessing negative indices always results in an exception.
 * Accessing zero or positive indices behaves as if this was an infinite array. */
public final class Bag<E> {
    private Object[] buffer = EMPTY;
    private static Object[] EMPTY = new Object[0];

    /** Arrays of smaller size will never be generated. */
    private static int MIN_SIZE = 16;
    private static int INCREMENT_THRESHOLD_POWER = 10;
    /** Sizes larger than this will no longer jump by powers of two, but by adding this increment. */
    private static int INCREMENT_THRESHOLD = 1 << INCREMENT_THRESHOLD_POWER;
    static int capacityFor(int requiredSize) {
        if (requiredSize <= MIN_SIZE) {
            // Never return anything smaller than that, because allocating small arrays is pointless
            return MIN_SIZE;
        } else if (requiredSize >= INCREMENT_THRESHOLD) {
            // 1. Add INCREMENT_THRESHOLD - 1 to force rounding up
            // 2. Divide by INCREMENT_THRESHOLD and then multiply by INCREMENT_THRESHOLD to round down to nearest multiple
            //      of INCREMENT_THRESHOLD.
            //      This is not done through division and multiplication, nor by >> followed by << but by masking.
            //      The used mask is all ones, except for the INCREMENT_THRESHOLD_POWER least significant zeros.
            //      It so happens, that -INCREMENT_THRESHOLD is exactly this mask.
            return (requiredSize + INCREMENT_THRESHOLD - 1) & -INCREMENT_THRESHOLD;
        } else {
            // Get the nearest power of two
            return MathUtils.nextPowerOfTwo(requiredSize);
        }
    }

    /** Ensure that the internal buffer has at least the given capacity.
     * Returns internal buffer. */
    public Object[] ensureCapacity(int capacity) {
        final Object[] oldBuffer = this.buffer;
        final int oldLength = oldBuffer.length;
        if (oldLength >= capacity)
            return oldBuffer;
        Object[] newBuffer = new Object[Bag.capacityFor(capacity)];
        System.arraycopy(oldBuffer, 0, newBuffer, 0, oldLength);
        return this.buffer = newBuffer;
    }

    /** Get the value at given index.
     * If the value was never assigned, it will be null. */
    @SuppressWarnings("unchecked")
    public E get(int index) {
        final Object[] buffer = this.buffer;
        if (index >= buffer.length) {
            return null;
        }
        return (E) buffer[index];
    }

    /** Set the value at given index. */
    public void set(int index, E value) {
        ensureCapacity(index + 1)[index] = value;
    }

    /** Remove value at given index and return the old value. */
    @SuppressWarnings("unchecked")
    public E remove(int index) {
        final Object[] buffer = this.buffer;

        if (index >= buffer.length) {
            return null;
        }

        final E result = (E) buffer[index];
        buffer[index] = null;
        return result;
    }

    /** Set all values to null. */
    public void clear() {
        final Object[] buffer = this.buffer;
        for (int i = buffer.length - 1; i >= 0; i--) {
            buffer[i] = null;
        }
    }
}
