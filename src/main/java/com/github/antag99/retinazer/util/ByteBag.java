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

/** Generic dynamically sized container for bytes. */
public final class ByteBag {
    private byte[] buffer = EMPTY;
    private static final byte[] EMPTY = new byte[0];

    /** Ensure that the internal buffer has at least the given capacity.
     * Returns internal buffer. */
    public byte[] ensureCapacity(int capacity) {
        final byte[] buffer = this.buffer;
        final int oldLength = buffer.length;
        if (oldLength >= capacity)
            return buffer;
        byte[] newBuffer = new byte[Bag.capacityFor(capacity)];
        System.arraycopy(buffer, 0, newBuffer, 0, oldLength);
        return this.buffer = newBuffer;
    }

    /** Get the value at given index.
     * If the value was never assigned, it will be zero. */
    public byte get(int index) {
        final byte[] buffer = this.buffer;
        if (index >= buffer.length) {
            return 0;
        }
        return buffer[index];
    }

    /** Set the value at given index. */
    public void set(int index, byte value) {
        ensureCapacity(index + 1)[index] = value;
    }

    /** Set all values to zero. */
    public void clear() {
        final byte[] buffer = this.buffer;
        for (int i = buffer.length - 1; i >= 0; i--) {
            buffer[i] = 0;
        }
    }
}
