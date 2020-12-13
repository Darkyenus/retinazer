package com.darkyen.retinazer.util;

import org.jetbrains.annotations.NotNull;

/** Generic dynamically sized container for doubles. */
public final class DoubleBag {

	private static final double[] EMPTY = new double[0];

	private double[] buffer = EMPTY;

	/**
	 * Ensure that the internal buffer has at least the given capacity.
	 * Returns internal buffer.
	 */
	@NotNull
	public double[] ensureCapacity(int capacity) {
		final double[] buffer = this.buffer;
		final int oldLength = buffer.length;
		if (oldLength >= capacity)
			return buffer;
		double[] newBuffer = new double[Bag.capacityFor(capacity)];
		System.arraycopy(buffer, 0, newBuffer, 0, oldLength);
		return this.buffer = newBuffer;
	}

	/**
	 * Get the value at given index.
	 * If the value was never assigned, it will be zero.
	 */
	public double get(int index) {
		final double[] buffer = this.buffer;
		if (index >= buffer.length) {
			return 0;
		}
		return buffer[index];
	}

	/** Set the value at given index. */
	public void set(int index, double value) {
		ensureCapacity(index + 1)[index] = value;
	}

	/** Set all values to zero. */
	public void clear() {
		final double[] buffer = this.buffer;
		for (int i = buffer.length - 1; i >= 0; i--) {
			buffer[i] = 0;
		}
	}
}
