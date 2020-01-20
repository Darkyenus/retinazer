package com.github.antag99.retinazer.util;

/** Generic dynamically sized container for floats. */
public final class FloatBag {
    private float[] buffer = EMPTY;
    private static final float[] EMPTY = new float[0];

    /** Ensure that the internal buffer has at least the given capacity.
     * Returns internal buffer. */
    public float[] ensureCapacity(int capacity) {
        final float[] buffer = this.buffer;
        final int oldLength = buffer.length;
        if (oldLength >= capacity)
            return buffer;
        float[] newBuffer = new float[Bag.capacityFor(capacity)];
        System.arraycopy(buffer, 0, newBuffer, 0, oldLength);
        return this.buffer = newBuffer;
    }

    /** Get the value at given index.
     * If the value was never assigned, it will be zero. */
    public float get(int index) {
        final float[] buffer = this.buffer;
        if (index >= buffer.length) {
            return 0;
        }
        return buffer[index];
    }

    /** Set the value at given index. */
    public void set(int index, float value) {
        ensureCapacity(index + 1)[index] = value;
    }

    /** Set all values to zero. */
    public void clear() {
        final float[] buffer = this.buffer;
        for (int i = buffer.length - 1; i >= 0; i--) {
            buffer[i] = 0;
        }
    }
}
