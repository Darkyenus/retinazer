/******************************************************************************
 Copyright (C) 2015 Anton Gustafsson

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in
 all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 SOFTWARE.
 */
package com.github.antag99.retinazer.util;

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
