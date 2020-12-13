package com.darkyen.retinazer.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ShortBagTest {

	/**
	 * Ensures that the elements of a bag are actually stored
	 */
	@Test
	public void testStorage() {
		ShortBag bag = new ShortBag();

		bag.set(0, (short) 0);
		assertEquals((short) 0, bag.get(0));
		assertEquals((short) 0, bag.get(1));
		assertEquals((short) 0, bag.get(2));
		assertEquals((short) 0, bag.get(3));
		assertEquals((short) 0, bag.get(4));
		assertEquals((short) 0, bag.get(5));
		assertEquals((short) 0, bag.get(6));
		assertEquals((short) 0, bag.get(7));

		bag.set(1, (short) 1);
		assertEquals((short) 0, bag.get(0));
		assertEquals((short) 1, bag.get(1));
		assertEquals((short) 0, bag.get(2));
		assertEquals((short) 0, bag.get(3));
		assertEquals((short) 0, bag.get(4));
		assertEquals((short) 0, bag.get(5));
		assertEquals((short) 0, bag.get(6));
		assertEquals((short) 0, bag.get(7));

		bag.set(2, (short) 2);
		assertEquals((short) 0, bag.get(0));
		assertEquals((short) 1, bag.get(1));
		assertEquals((short) 2, bag.get(2));
		assertEquals((short) 0, bag.get(3));
		assertEquals((short) 0, bag.get(4));
		assertEquals((short) 0, bag.get(5));
		assertEquals((short) 0, bag.get(6));
		assertEquals((short) 0, bag.get(7));

		bag.set(3, (short) 3);
		assertEquals((short) 0, bag.get(0));
		assertEquals((short) 1, bag.get(1));
		assertEquals((short) 2, bag.get(2));
		assertEquals((short) 3, bag.get(3));
		assertEquals((short) 0, bag.get(4));
		assertEquals((short) 0, bag.get(5));
		assertEquals((short) 0, bag.get(6));
		assertEquals((short) 0, bag.get(7));

		bag.set(4, (short) 4);
		assertEquals((short) 0, bag.get(0));
		assertEquals((short) 1, bag.get(1));
		assertEquals((short) 2, bag.get(2));
		assertEquals((short) 3, bag.get(3));
		assertEquals((short) 4, bag.get(4));
		assertEquals((short) 0, bag.get(5));
		assertEquals((short) 0, bag.get(6));
		assertEquals((short) 0, bag.get(7));

		bag.set(5, (short) 5);
		assertEquals((short) 0, bag.get(0));
		assertEquals((short) 1, bag.get(1));
		assertEquals((short) 2, bag.get(2));
		assertEquals((short) 3, bag.get(3));
		assertEquals((short) 4, bag.get(4));
		assertEquals((short) 5, bag.get(5));
		assertEquals((short) 0, bag.get(6));
		assertEquals((short) 0, bag.get(7));

		bag.set(6, (short) 6);
		assertEquals((short) 0, bag.get(0));
		assertEquals((short) 1, bag.get(1));
		assertEquals((short) 2, bag.get(2));
		assertEquals((short) 3, bag.get(3));
		assertEquals((short) 4, bag.get(4));
		assertEquals((short) 5, bag.get(5));
		assertEquals((short) 6, bag.get(6));
		assertEquals((short) 0, bag.get(7));

		bag.set(7, (short) 7);
		assertEquals((short) 0, bag.get(0));
		assertEquals((short) 1, bag.get(1));
		assertEquals((short) 2, bag.get(2));
		assertEquals((short) 3, bag.get(3));
		assertEquals((short) 4, bag.get(4));
		assertEquals((short) 5, bag.get(5));
		assertEquals((short) 6, bag.get(6));
		assertEquals((short) 7, bag.get(7));

		bag.clear();

		assertEquals((short) 0, bag.get(0));
		assertEquals((short) 0, bag.get(1));
		assertEquals((short) 0, bag.get(2));
		assertEquals((short) 0, bag.get(3));
		assertEquals((short) 0, bag.get(4));
		assertEquals((short) 0, bag.get(5));
		assertEquals((short) 0, bag.get(6));
		assertEquals((short) 0, bag.get(7));
	}

	/**
	 * Ensures that the bag contains the default value by default
	 */
	@Test
	public void testDefault() {
		ShortBag bag = new ShortBag();
		assertEquals((short) 0, bag.get(0));
		bag.set(0, (short) 1);
		assertEquals((short) 0, bag.get(1));
		assertEquals((short) 0, bag.get(2));
		assertEquals((short) 0, bag.get(3));
	}

	/**
	 * When a negative index is used, an {@link IndexOutOfBoundsException} should be thrown.
	 */
	@Test
	public void testIndexOutOfBoundsException() {
		ShortBag bag = new ShortBag();
		for (int i = 0; i < 32; i++) {
			final int i_ = i;
			assertThrows(IndexOutOfBoundsException.class, () -> bag.set(-(1 << i_), (short) 0));
		}

		for (int i = 0; i < 32; i++) {
			final int i_ = i;
			assertThrows(IndexOutOfBoundsException.class, () -> bag.get(-(1 << i_)));
		}
	}
}
