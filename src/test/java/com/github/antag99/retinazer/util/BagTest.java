package com.github.antag99.retinazer.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class BagTest {

    /**
     * Ensures that the elements of a bag are actually stored
     */
    @Test
    public void testStorage() {
        Bag<Object> bag = new Bag<>();
        Object element0 = new Object();
        Object element1 = new Object();
        Object element2 = new Object();
        Object element3 = new Object();
        Object element4 = new Object();
        Object element5 = new Object();
        Object element6 = new Object();
        Object element7 = new Object();

        bag.set(0, element0);
        assertSame(element0, bag.get(0));
        assertSame(null, bag.get(1));
        assertSame(null, bag.get(2));
        assertSame(null, bag.get(3));
        assertSame(null, bag.get(4));
        assertSame(null, bag.get(5));
        assertSame(null, bag.get(6));
        assertSame(null, bag.get(7));

        bag.set(1, element1);
        assertSame(element0, bag.get(0));
        assertSame(element1, bag.get(1));
        assertSame(null, bag.get(2));
        assertSame(null, bag.get(3));
        assertSame(null, bag.get(4));
        assertSame(null, bag.get(5));
        assertSame(null, bag.get(6));
        assertSame(null, bag.get(7));

        bag.set(2, element2);
        assertSame(element0, bag.get(0));
        assertSame(element1, bag.get(1));
        assertSame(element2, bag.get(2));
        assertSame(null, bag.get(3));
        assertSame(null, bag.get(4));
        assertSame(null, bag.get(5));
        assertSame(null, bag.get(6));
        assertSame(null, bag.get(7));

        bag.set(3, element3);
        assertSame(element0, bag.get(0));
        assertSame(element1, bag.get(1));
        assertSame(element2, bag.get(2));
        assertSame(element3, bag.get(3));
        assertSame(null, bag.get(4));
        assertSame(null, bag.get(5));
        assertSame(null, bag.get(6));
        assertSame(null, bag.get(7));

        bag.set(4, element4);
        assertSame(element0, bag.get(0));
        assertSame(element1, bag.get(1));
        assertSame(element2, bag.get(2));
        assertSame(element3, bag.get(3));
        assertSame(element4, bag.get(4));
        assertSame(null, bag.get(5));
        assertSame(null, bag.get(6));
        assertSame(null, bag.get(7));

        bag.set(5, element5);
        assertSame(element0, bag.get(0));
        assertSame(element1, bag.get(1));
        assertSame(element2, bag.get(2));
        assertSame(element3, bag.get(3));
        assertSame(element4, bag.get(4));
        assertSame(element5, bag.get(5));
        assertSame(null, bag.get(6));
        assertSame(null, bag.get(7));

        bag.set(6, element6);
        assertSame(element0, bag.get(0));
        assertSame(element1, bag.get(1));
        assertSame(element2, bag.get(2));
        assertSame(element3, bag.get(3));
        assertSame(element4, bag.get(4));
        assertSame(element5, bag.get(5));
        assertSame(element6, bag.get(6));
        assertSame(null, bag.get(7));

        bag.set(7, element7);
        assertSame(element0, bag.get(0));
        assertSame(element1, bag.get(1));
        assertSame(element2, bag.get(2));
        assertSame(element3, bag.get(3));
        assertSame(element4, bag.get(4));
        assertSame(element5, bag.get(5));
        assertSame(element6, bag.get(6));
        assertSame(element7, bag.get(7));

        bag.clear();

        assertSame(null, bag.get(0));
        assertSame(null, bag.get(1));
        assertSame(null, bag.get(2));
        assertSame(null, bag.get(3));
        assertSame(null, bag.get(4));
        assertSame(null, bag.get(5));
        assertSame(null, bag.get(6));
        assertSame(null, bag.get(7));
    }

    /**
     * Ensures that the bag contains the default value by default
     */
    @Test
    public void testDefault() {
        Bag<Object> bag = new Bag<>();
        assertNull(bag.get(0));
        bag.set(0, new Object());
        assertNull(bag.get(1));
        assertNull(bag.get(2));
        assertNull(bag.get(3));
    }

    @Test
    public void testCapacityFor() {
        assertEquals(16, Bag.capacityFor(0));
        assertEquals(16, Bag.capacityFor(1));
        assertEquals(16, Bag.capacityFor(2));
        assertEquals(16, Bag.capacityFor(3));
        assertEquals(16, Bag.capacityFor(16));
        assertEquals(32, Bag.capacityFor(17));
        assertEquals(32, Bag.capacityFor(32));
        assertEquals(64, Bag.capacityFor(33));
        assertEquals(512, Bag.capacityFor(512));
        assertEquals(1024, Bag.capacityFor(513));
        assertEquals(1024, Bag.capacityFor(1000));
        assertEquals(1024, Bag.capacityFor(1024));
        assertEquals(2048, Bag.capacityFor(1025));
        assertEquals(5120, Bag.capacityFor(5000));
    }

    /**
     * When a negative index is used, an {@link ArrayIndexOutOfBoundsException} should be thrown.
     */
    @Test
    public void testIndexOutOfBoundsException() {
        Bag<Object> bag = new Bag<>();
        for (int i = 0; i < 32; i++) {
            final int i_ = i;
            assertThrows(IndexOutOfBoundsException.class, () -> bag.set(-(1 << i_), new Object()));
        }
        for (int i = 0; i < 32; i++) {
            final int i_ = i;
            assertThrows(IndexOutOfBoundsException.class, () -> bag.get(-(1 << i_)));
        }
        for (int i = 0; i < 32; i++) {
            final int i_ = i;
            assertThrows(IndexOutOfBoundsException.class, () -> bag.remove(-(1 << i_)));
        }
    }
}
