package com.github.antag99.retinazer.util;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

public class MaskTest {

    @Test
    public void testLength() {
        Mask mask;

        mask = new Mask();
        for (int i = 0, n = 128; i < n; i++) {
            assertEquals(i, mask.length());
            mask.set(i);
            assertEquals(i + 1, mask.length());
        }

        mask = new Mask();
        mask.set(4);
        assertEquals(5, mask.length());
        mask.set(63);
        assertEquals(64, mask.length());
        mask.set(64);
        assertEquals(65, mask.length());
    }

    @Test
    public void testIndices() {
        Mask mask;

        mask = new Mask();
        mask.set(1);
        mask.set(4);
        mask.set(6);
        mask.set(7);
        assertArrayEquals(new int[]{1, 4, 6, 7}, mask.getIndices());
    }
}
