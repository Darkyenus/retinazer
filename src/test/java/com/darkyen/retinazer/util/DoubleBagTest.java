package com.darkyen.retinazer.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DoubleBagTest {

	/**
	 * Ensures that the elements of a bag are actually stored
	 */
	@Test
	public void testStorage() {
		DoubleBag bag = new DoubleBag();

		bag.set(0, 0d);
		assertEquals(0d, bag.get(0), 0d);
		assertEquals(0d, bag.get(1), 0d);
		assertEquals(0d, bag.get(2), 0d);
		assertEquals(0d, bag.get(3), 0d);
		assertEquals(0d, bag.get(4), 0d);
		assertEquals(0d, bag.get(5), 0d);
		assertEquals(0d, bag.get(6), 0d);
		assertEquals(0d, bag.get(7), 0d);

		bag.set(1, 1d);
		assertEquals(0d, bag.get(0), 0d);
		assertEquals(1d, bag.get(1), 0d);
		assertEquals(0d, bag.get(2), 0d);
		assertEquals(0d, bag.get(3), 0d);
		assertEquals(0d, bag.get(4), 0d);
		assertEquals(0d, bag.get(5), 0d);
		assertEquals(0d, bag.get(6), 0d);
		assertEquals(0d, bag.get(7), 0d);

		bag.set(2, 2d);
		assertEquals(0d, bag.get(0), 0d);
		assertEquals(1d, bag.get(1), 0d);
		assertEquals(2d, bag.get(2), 0d);
		assertEquals(0d, bag.get(3), 0d);
		assertEquals(0d, bag.get(4), 0d);
		assertEquals(0d, bag.get(5), 0d);
		assertEquals(0d, bag.get(6), 0d);
		assertEquals(0d, bag.get(7), 0d);

		bag.set(3, 3d);
		assertEquals(0d, bag.get(0), 0d);
		assertEquals(1d, bag.get(1), 0d);
		assertEquals(2d, bag.get(2), 0d);
		assertEquals(3d, bag.get(3), 0d);
		assertEquals(0d, bag.get(4), 0d);
		assertEquals(0d, bag.get(5), 0d);
		assertEquals(0d, bag.get(6), 0d);
		assertEquals(0d, bag.get(7), 0d);

		bag.set(4, 4d);
		assertEquals(0d, bag.get(0), 0d);
		assertEquals(1d, bag.get(1), 0d);
		assertEquals(2d, bag.get(2), 0d);
		assertEquals(3d, bag.get(3), 0d);
		assertEquals(4d, bag.get(4), 0d);
		assertEquals(0d, bag.get(5), 0d);
		assertEquals(0d, bag.get(6), 0d);
		assertEquals(0d, bag.get(7), 0d);

		bag.set(5, 5d);
		assertEquals(0d, bag.get(0), 0d);
		assertEquals(1d, bag.get(1), 0d);
		assertEquals(2d, bag.get(2), 0d);
		assertEquals(3d, bag.get(3), 0d);
		assertEquals(4d, bag.get(4), 0d);
		assertEquals(5d, bag.get(5), 0d);
		assertEquals(0d, bag.get(6), 0d);
		assertEquals(0d, bag.get(7), 0d);

		bag.set(6, 6d);
		assertEquals(0d, bag.get(0), 0d);
		assertEquals(1d, bag.get(1), 0d);
		assertEquals(2d, bag.get(2), 0d);
		assertEquals(3d, bag.get(3), 0d);
		assertEquals(4d, bag.get(4), 0d);
		assertEquals(5d, bag.get(5), 0d);
		assertEquals(6d, bag.get(6), 0d);
		assertEquals(0d, bag.get(7), 0d);

		bag.set(7, 7d);
		assertEquals(0d, bag.get(0), 0d);
		assertEquals(1d, bag.get(1), 0d);
		assertEquals(2d, bag.get(2), 0d);
		assertEquals(3d, bag.get(3), 0d);
		assertEquals(4d, bag.get(4), 0d);
		assertEquals(5d, bag.get(5), 0d);
		assertEquals(6d, bag.get(6), 0d);
		assertEquals(7d, bag.get(7), 0d);

		bag.clear();

		assertEquals(0d, bag.get(0), 0d);
		assertEquals(0d, bag.get(1), 0d);
		assertEquals(0d, bag.get(2), 0d);
		assertEquals(0d, bag.get(3), 0d);
		assertEquals(0d, bag.get(4), 0d);
		assertEquals(0d, bag.get(5), 0d);
		assertEquals(0d, bag.get(6), 0d);
		assertEquals(0d, bag.get(7), 0d);
	}

	/**
	 * Ensures that the bag contains the default value by default
	 */
	@Test
	public void testDefault() {
		DoubleBag bag = new DoubleBag();
		assertEquals(0d, bag.get(0), 0d);
		bag.set(0, 1d);
		assertEquals(0d, bag.get(1), 0d);
		assertEquals(0d, bag.get(2), 0d);
		assertEquals(0d, bag.get(3), 0d);
	}

	/**
	 * When a negative index is used, an {@link IndexOutOfBoundsException} should be thrown.
	 */
	@Test
	public void testIndexOutOfBoundsException() {
		DoubleBag bag = new DoubleBag();
		for (int i = 0; i < 32; i++) {
			final int i_ = i;
			assertThrows(IndexOutOfBoundsException.class, () -> bag.set(-(1 << i_), 0));
		}

		for (int i = 0; i < 32; i++) {
			final int i_ = i;
			assertThrows(IndexOutOfBoundsException.class, () -> bag.get(-(1 << i_)));
		}
	}
}
