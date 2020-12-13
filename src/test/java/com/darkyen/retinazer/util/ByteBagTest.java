package com.darkyen.retinazer.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class ByteBagTest {

    /**
     * Ensures that the elements of a bag are actually stored
     */
    @Test
    public void testStorage() {
        ByteBag bag = new ByteBag();

        bag.set(0, (byte) 0);
        assertEquals((byte) 0, bag.get(0));
        assertEquals((byte) 0, bag.get(1));
        assertEquals((byte) 0, bag.get(2));
        assertEquals((byte) 0, bag.get(3));
        assertEquals((byte) 0, bag.get(4));
        assertEquals((byte) 0, bag.get(5));
        assertEquals((byte) 0, bag.get(6));
        assertEquals((byte) 0, bag.get(7));

        bag.set(1, (byte) 1);
        assertEquals((byte) 0, bag.get(0));
        assertEquals((byte) 1, bag.get(1));
        assertEquals((byte) 0, bag.get(2));
        assertEquals((byte) 0, bag.get(3));
        assertEquals((byte) 0, bag.get(4));
        assertEquals((byte) 0, bag.get(5));
        assertEquals((byte) 0, bag.get(6));
        assertEquals((byte) 0, bag.get(7));

        bag.set(2, (byte) 2);
        assertEquals((byte) 0, bag.get(0));
        assertEquals((byte) 1, bag.get(1));
        assertEquals((byte) 2, bag.get(2));
        assertEquals((byte) 0, bag.get(3));
        assertEquals((byte) 0, bag.get(4));
        assertEquals((byte) 0, bag.get(5));
        assertEquals((byte) 0, bag.get(6));
        assertEquals((byte) 0, bag.get(7));

        bag.set(3, (byte) 3);
        assertEquals((byte) 0, bag.get(0));
        assertEquals((byte) 1, bag.get(1));
        assertEquals((byte) 2, bag.get(2));
        assertEquals((byte) 3, bag.get(3));
        assertEquals((byte) 0, bag.get(4));
        assertEquals((byte) 0, bag.get(5));
        assertEquals((byte) 0, bag.get(6));
        assertEquals((byte) 0, bag.get(7));

        bag.set(4, (byte) 4);
        assertEquals((byte) 0, bag.get(0));
        assertEquals((byte) 1, bag.get(1));
        assertEquals((byte) 2, bag.get(2));
        assertEquals((byte) 3, bag.get(3));
        assertEquals((byte) 4, bag.get(4));
        assertEquals((byte) 0, bag.get(5));
        assertEquals((byte) 0, bag.get(6));
        assertEquals((byte) 0, bag.get(7));

        bag.set(5, (byte) 5);
        assertEquals((byte) 0, bag.get(0));
        assertEquals((byte) 1, bag.get(1));
        assertEquals((byte) 2, bag.get(2));
        assertEquals((byte) 3, bag.get(3));
        assertEquals((byte) 4, bag.get(4));
        assertEquals((byte) 5, bag.get(5));
        assertEquals((byte) 0, bag.get(6));
        assertEquals((byte) 0, bag.get(7));

        bag.set(6, (byte) 6);
        assertEquals((byte) 0, bag.get(0));
        assertEquals((byte) 1, bag.get(1));
        assertEquals((byte) 2, bag.get(2));
        assertEquals((byte) 3, bag.get(3));
        assertEquals((byte) 4, bag.get(4));
        assertEquals((byte) 5, bag.get(5));
        assertEquals((byte) 6, bag.get(6));
        assertEquals((byte) 0, bag.get(7));

        bag.set(7, (byte) 7);
        assertEquals((byte) 0, bag.get(0));
        assertEquals((byte) 1, bag.get(1));
        assertEquals((byte) 2, bag.get(2));
        assertEquals((byte) 3, bag.get(3));
        assertEquals((byte) 4, bag.get(4));
        assertEquals((byte) 5, bag.get(5));
        assertEquals((byte) 6, bag.get(6));
        assertEquals((byte) 7, bag.get(7));

        bag.clear();

        assertEquals((byte) 0, bag.get(0));
        assertEquals((byte) 0, bag.get(1));
        assertEquals((byte) 0, bag.get(2));
        assertEquals((byte) 0, bag.get(3));
        assertEquals((byte) 0, bag.get(4));
        assertEquals((byte) 0, bag.get(5));
        assertEquals((byte) 0, bag.get(6));
        assertEquals((byte) 0, bag.get(7));
    }

    /** Ensures that the bag contains the default value by default */
    @Test
    public void testDefault() {
        ByteBag bag = new ByteBag();
        assertEquals((byte) 0, bag.get(0));
        bag.set(0, (byte) 1);
        assertEquals((byte) 0, bag.get(1));
        assertEquals((byte) 0, bag.get(2));
        assertEquals((byte) 0, bag.get(3));
    }

    /** When a negative index is used, an {@link IndexOutOfBoundsException} should be thrown. */
    @Test
    public void testIndexOutOfBoundsException() {
        ByteBag bag = new ByteBag();
        for (int i = 0; i < 32; i++) {
            final int i_ = i;
            assertThrows(IndexOutOfBoundsException.class, () -> bag.set(-(1 << i_), (byte) 0));
        }

        for (int i = 0; i < 32; i++) {
            final int i_ = i;
            assertThrows(IndexOutOfBoundsException.class, () -> bag.get(-(1 << i_)));
        }
    }
}
