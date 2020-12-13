package com.darkyen.retinazer.util;

import org.jetbrains.annotations.NotNull;

/** Generic dynamically sized container for longs. */
public final class LongBag {

	private static final long[] EMPTY = new long[0];

	private long[] buffer = EMPTY;

	/**
	 * Ensure that the internal buffer has at least the given capacity.
	 * Returns internal buffer.
	 */
	@NotNull
	public long[] ensureCapacity(int capacity) {
		final long[] buffer = this.buffer;
		final int oldLength = buffer.length;
		if (oldLength >= capacity)
			return buffer;
		long[] newBuffer = new long[Bag.capacityFor(capacity)];
		System.arraycopy(buffer, 0, newBuffer, 0, oldLength);
		return this.buffer = newBuffer;
	}

	/**
	 * Get the value at given index.
	 * If the value was never assigned, it will be zero.
	 */
	public long get(int index) {
		final long[] buffer = this.buffer;
		if (index >= buffer.length) {
			return 0;
		}
		return buffer[index];
	}

	/** Set the value at given index. */
	public void set(int index, long value) {
		ensureCapacity(index + 1)[index] = value;
	}

	/** Set all values to zero. */
	public void clear() {
		final long[] buffer = this.buffer;
		for (int i = buffer.length - 1; i >= 0; i--) {
			buffer[i] = 0;
		}
	}
}
