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
        assertEquals(0, engine.getFamily(FULL_SET.family()).hashCode());
        assertEquals(1, engine.getFamily(FULL_SET.familyWith(Components.FlagComponentA.class)).hashCode());
        assertEquals(2, engine.getFamily(FULL_SET.familyWithout(Components.FlagComponentA.class)).hashCode());
    }

    @Test
    public void testEquals() {
        Engine engine = new Engine(FULL_SET);
        assertEquals(engine.getFamily(FULL_SET.family()), engine.getFamily(FULL_SET.family()));
        assertEquals(engine.getFamily(FULL_SET.familyWith(Components.FlagComponentA.class)),
                engine.getFamily(FULL_SET.familyWith(Components.FlagComponentA.class)));
        assertEquals(engine.getFamily(FULL_SET.familyWithout(Components.FlagComponentA.class)),
                engine.getFamily(FULL_SET.familyWithout(Components.FlagComponentA.class)));
        assertNotEquals(engine.getFamily(FULL_SET.familyWith(Components.FlagComponentA.class)),
                engine.getFamily(FULL_SET.familyWithout(Components.FlagComponentA.class)));
    }

    @Test
    public void testSame() {
        Engine engine = new Engine(FULL_SET);
        assertSame(engine.getFamily(FULL_SET.family()), engine.getFamily(FULL_SET.family()));
        assertSame(engine.getFamily(FULL_SET.familyWith(Components.FlagComponentA.class)),
                engine.getFamily(FULL_SET.familyWith(Components.FlagComponentA.class)));
        assertSame(engine.getFamily(FULL_SET.familyWithout(Components.FlagComponentA.class)),
                engine.getFamily(FULL_SET.familyWithout(Components.FlagComponentA.class)));
        assertNotSame(engine.getFamily(FULL_SET.familyWith(Components.FlagComponentA.class)),
                engine.getFamily(FULL_SET.familyWithout(Components.FlagComponentA.class)));
    }
}
