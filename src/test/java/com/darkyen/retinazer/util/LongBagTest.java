package com.darkyen.retinazer.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class LongBagTest {

    /**
     * Ensures that the elements of a bag are actually stored
     */
    @Test
    public void testStorage() {
        LongBag bag = new LongBag();

        bag.set(0, 0);
        assertEquals(0, bag.get(0));
        assertEquals(0, bag.get(1));
        assertEquals(0, bag.get(2));
        assertEquals(0, bag.get(3));
        assertEquals(0, bag.get(4));
        assertEquals(0, bag.get(5));
        assertEquals(0, bag.get(6));
        assertEquals(0, bag.get(7));

        bag.set(1, 1);
        assertEquals(0, bag.get(0));
        assertEquals(1, bag.get(1));
        assertEquals(0, bag.get(2));
        assertEquals(0, bag.get(3));
        assertEquals(0, bag.get(4));
        assertEquals(0, bag.get(5));
        assertEquals(0, bag.get(6));
        assertEquals(0, bag.get(7));

        bag.set(2, 2);
        assertEquals(0, bag.get(0));
        assertEquals(1, bag.get(1));
        assertEquals(2, bag.get(2));
        assertEquals(0, bag.get(3));
        assertEquals(0, bag.get(4));
        assertEquals(0, bag.get(5));
        assertEquals(0, bag.get(6));
        assertEquals(0, bag.get(7));

        bag.set(3, 3);
        assertEquals(0, bag.get(0));
        assertEquals(1, bag.get(1));
        assertEquals(2, bag.get(2));
        assertEquals(3, bag.get(3));
        assertEquals(0, bag.get(4));
        assertEquals(0, bag.get(5));
        assertEquals(0, bag.get(6));
        assertEquals(0, bag.get(7));

        bag.set(4, 4);
        assertEquals(0, bag.get(0));
        assertEquals(1, bag.get(1));
        assertEquals(2, bag.get(2));
        assertEquals(3, bag.get(3));
        assertEquals(4, bag.get(4));
        assertEquals(0, bag.get(5));
        assertEquals(0, bag.get(6));
        assertEquals(0, bag.get(7));

        bag.set(5, 5);
        assertEquals(0, bag.get(0));
        assertEquals(1, bag.get(1));
        assertEquals(2, bag.get(2));
        assertEquals(3, bag.get(3));
        assertEquals(4, bag.get(4));
        assertEquals(5, bag.get(5));
        assertEquals(0, bag.get(6));
        assertEquals(0, bag.get(7));

        bag.set(6, 6);
        assertEquals(0, bag.get(0));
        assertEquals(1, bag.get(1));
        assertEquals(2, bag.get(2));
        assertEquals(3, bag.get(3));
        assertEquals(4, bag.get(4));
        assertEquals(5, bag.get(5));
        assertEquals(6, bag.get(6));
        assertEquals(0, bag.get(7));

        bag.set(7, 7);
        assertEquals(0, bag.get(0));
        assertEquals(1, bag.get(1));
        assertEquals(2, bag.get(2));
        assertEquals(3, bag.get(3));
        assertEquals(4, bag.get(4));
        assertEquals(5, bag.get(5));
        assertEquals(6, bag.get(6));
        assertEquals(7, bag.get(7));

        bag.clear();

        assertEquals(0, bag.get(0));
        assertEquals(0, bag.get(1));
        assertEquals(0, bag.get(2));
        assertEquals(0, bag.get(3));
        assertEquals(0, bag.get(4));
        assertEquals(0, bag.get(5));
        assertEquals(0, bag.get(6));
        assertEquals(0, bag.get(7));
    }

    /**
     * Ensures that the bag contains the default value by default
     */
    @Test
    public void testDefault() {
        LongBag bag = new LongBag();
        assertEquals(0, bag.get(0));
        bag.set(0, 1);
        assertEquals(0, bag.get(1));
        assertEquals(0, bag.get(2));
        assertEquals(0, bag.get(3));
    }

    /**
     * When a negative index is used, an {@link IndexOutOfBoundsException} should be thrown.
     */
    @Test
    public void testIndexOutOfBoundsException() {
        LongBag bag = new LongBag();
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
