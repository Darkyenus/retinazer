package com.github.antag99.retinazer.util;

import org.jetbrains.annotations.NotNull;

/** Generic dynamically sized container for shorts. */
public final class ShortBag {
    private short[] buffer = EMPTY;
    private static final short[] EMPTY = new short[0];

    /** Ensure that the internal buffer has at least the given capacity.
     * Returns internal buffer. */
    @NotNull
    public short[] ensureCapacity(int capacity) {
        final short[] buffer = this.buffer;
        final int oldLength = buffer.length;
        if (oldLength >= capacity)
            return buffer;
        short[] newBuffer = new short[Bag.capacityFor(capacity)];
        System.arraycopy(buffer, 0, newBuffer, 0, oldLength);
        return this.buffer = newBuffer;
    }

    /** Get the value at given index.
     * If the value was never assigned, it will be zero. */
    public short get(int index) {
        final short[] buffer = this.buffer;
        if (index >= buffer.length) {
            return 0;
        }
        return buffer[index];
    }

    /** Set the value at given index. */
    public void set(int index, short value) {
        ensureCapacity(index + 1)[index] = value;
    }

    /** Set all values to zero. */
    public void clear() {
        final short[] buffer = this.buffer;
        for (int i = buffer.length - 1; i >= 0; i--) {
            buffer[i] = 0;
        }
    }
}
