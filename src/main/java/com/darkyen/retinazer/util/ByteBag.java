package com.darkyen.retinazer.util;

import org.jetbrains.annotations.NotNull;

/** Generic dynamically sized container for bytes. */
public final class ByteBag {
    private byte[] buffer = EMPTY;
    private static final byte[] EMPTY = new byte[0];

    /** Ensure that the internal buffer has at least the given capacity.
     * Returns internal buffer. */
    @NotNull
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

    /** Set the value at given index. */
    public void set(int index, byte value) {
        ensureCapacity(index + 1)[index] = value;
    }

    /** Set all values to zero. */
    public void clear() {
        final byte[] buffer = this.buffer;
        for (int i = buffer.length - 1; i >= 0; i--) {
            buffer[i] = 0;
        }
    }
}
