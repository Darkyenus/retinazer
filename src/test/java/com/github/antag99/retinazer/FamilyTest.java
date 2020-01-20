package com.github.antag99.retinazer;

import static com.github.antag99.retinazer.Components.FULL_SET;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

public class FamilyTest {
    @Test
    public void testHashCode() {
        Engine engine = new Engine(FULL_SET);
        assertEquals(0, engine.getFamily(Family.create()).hashCode());
        assertEquals(1, engine.getFamily(Family.with(Components.FlagComponentA.class)).hashCode());
        assertEquals(2, engine.getFamily(Family.exclude(Components.FlagComponentA.class)).hashCode());
    }

    @Test
    public void testEquals() {
        Engine engine = new Engine(FULL_SET);
        assertEquals(engine.getFamily(Family.create()), engine.getFamily(Family.create()));
        assertEquals(engine.getFamily(Family.with(Components.FlagComponentA.class)),
                engine.getFamily(Family.with(Components.FlagComponentA.class)));
        assertEquals(engine.getFamily(Family.exclude(Components.FlagComponentA.class)),
                engine.getFamily(Family.exclude(Components.FlagComponentA.class)));
        assertNotEquals(engine.getFamily(Family.with(Components.FlagComponentA.class)),
                engine.getFamily(Family.exclude(Components.FlagComponentA.class)));
    }

    @Test
    public void testSame() {
        Engine engine = new Engine(FULL_SET);
        assertSame(engine.getFamily(Family.create()), engine.getFamily(Family.create()));
        assertSame(engine.getFamily(Family.with(Components.FlagComponentA.class)),
                engine.getFamily(Family.with(Components.FlagComponentA.class)));
        assertSame(engine.getFamily(Family.exclude(Components.FlagComponentA.class)),
                engine.getFamily(Family.exclude(Components.FlagComponentA.class)));
        assertNotSame(engine.getFamily(Family.with(Components.FlagComponentA.class)),
                engine.getFamily(Family.exclude(Components.FlagComponentA.class)));
    }
}
