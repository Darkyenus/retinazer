package com.darkyen.retinazer.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class FloatBagTest {

    /**
     * Ensures that the elements of a bag are actually stored
     */
    @Test
    public void testStorage() {
        FloatBag bag = new FloatBag();

        bag.set(0, 0f);
        assertEquals(0f, bag.get(0), 0f);
        assertEquals(0f, bag.get(1), 0f);
        assertEquals(0f, bag.get(2), 0f);
        assertEquals(0f, bag.get(3), 0f);
        assertEquals(0f, bag.get(4), 0f);
        assertEquals(0f, bag.get(5), 0f);
        assertEquals(0f, bag.get(6), 0f);
        assertEquals(0f, bag.get(7), 0f);

        bag.set(1, 1f);
        assertEquals(0f, bag.get(0), 0f);
        assertEquals(1f, bag.get(1), 0f);
        assertEquals(0f, bag.get(2), 0f);
        assertEquals(0f, bag.get(3), 0f);
        assertEquals(0f, bag.get(4), 0f);
        assertEquals(0f, bag.get(5), 0f);
        assertEquals(0f, bag.get(6), 0f);
        assertEquals(0f, bag.get(7), 0f);

        bag.set(2, 2f);
        assertEquals(0f, bag.get(0), 0f);
        assertEquals(1f, bag.get(1), 0f);
        assertEquals(2f, bag.get(2), 0f);
        assertEquals(0f, bag.get(3), 0f);
        assertEquals(0f, bag.get(4), 0f);
        assertEquals(0f, bag.get(5), 0f);
        assertEquals(0f, bag.get(6), 0f);
        assertEquals(0f, bag.get(7), 0f);

        bag.set(3, 3f);
        assertEquals(0f, bag.get(0), 0f);
        assertEquals(1f, bag.get(1), 0f);
        assertEquals(2f, bag.get(2), 0f);
        assertEquals(3f, bag.get(3), 0f);
        assertEquals(0f, bag.get(4), 0f);
        assertEquals(0f, bag.get(5), 0f);
        assertEquals(0f, bag.get(6), 0f);
        assertEquals(0f, bag.get(7), 0f);

        bag.set(4, 4f);
        assertEquals(0f, bag.get(0), 0f);
        assertEquals(1f, bag.get(1), 0f);
        assertEquals(2f, bag.get(2), 0f);
        assertEquals(3f, bag.get(3), 0f);
        assertEquals(4f, bag.get(4), 0f);
        assertEquals(0f, bag.get(5), 0f);
        assertEquals(0f, bag.get(6), 0f);
        assertEquals(0f, bag.get(7), 0f);

        bag.set(5, 5f);
        assertEquals(0f, bag.get(0), 0f);
        assertEquals(1f, bag.get(1), 0f);
        assertEquals(2f, bag.get(2), 0f);
        assertEquals(3f, bag.get(3), 0f);
        assertEquals(4f, bag.get(4), 0f);
        assertEquals(5f, bag.get(5), 0f);
        assertEquals(0f, bag.get(6), 0f);
        assertEquals(0f, bag.get(7), 0f);

        bag.set(6, 6f);
        assertEquals(0f, bag.get(0), 0f);
        assertEquals(1f, bag.get(1), 0f);
        assertEquals(2f, bag.get(2), 0f);
        assertEquals(3f, bag.get(3), 0f);
        assertEquals(4f, bag.get(4), 0f);
        assertEquals(5f, bag.get(5), 0f);
        assertEquals(6f, bag.get(6), 0f);
        assertEquals(0f, bag.get(7), 0f);

        bag.set(7, 7f);
        assertEquals(0f, bag.get(0), 0f);
        assertEquals(1f, bag.get(1), 0f);
        assertEquals(2f, bag.get(2), 0f);
        assertEquals(3f, bag.get(3), 0f);
        assertEquals(4f, bag.get(4), 0f);
        assertEquals(5f, bag.get(5), 0f);
        assertEquals(6f, bag.get(6), 0f);
        assertEquals(7f, bag.get(7), 0f);

        bag.clear();

        assertEquals(0f, bag.get(0), 0f);
        assertEquals(0f, bag.get(1), 0f);
        assertEquals(0f, bag.get(2), 0f);
        assertEquals(0f, bag.get(3), 0f);
        assertEquals(0f, bag.get(4), 0f);
        assertEquals(0f, bag.get(5), 0f);
        assertEquals(0f, bag.get(6), 0f);
        assertEquals(0f, bag.get(7), 0f);
    }

    /**
     * Ensures that the bag contains the default value by default
     */
    @Test
    public void testDefault() {
        FloatBag bag = new FloatBag();
        assertEquals(0f, bag.get(0), 0f);
        bag.set(0, 1f);
        assertEquals(0f, bag.get(1), 0f);
        assertEquals(0f, bag.get(2), 0f);
        assertEquals(0f, bag.get(3), 0f);
    }

    /**
     * When a negative index is used, an {@link IndexOutOfBoundsException} should be thrown.
     */
    @Test
    public void testIndexOutOfBoundsException() {
        FloatBag bag = new FloatBag();
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
