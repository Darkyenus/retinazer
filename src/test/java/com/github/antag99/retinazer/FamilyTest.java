package com.github.antag99.retinazer;

import org.junit.jupiter.api.Test;

import static com.github.antag99.retinazer.Components.FULL_SET;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

public class FamilyTest {

    @Test
    public void testEquals() {
        Engine engine = new Engine(FULL_SET);
        assertEquals(engine.getEntities(FULL_SET.family()), engine.getEntities(FULL_SET.family()));
        assertEquals(
                engine.getEntities(FULL_SET.familyWith(Components.FlagComponentA.class)),
                engine.getEntities(FULL_SET.familyWith(Components.FlagComponentA.class)));
        assertEquals(
                engine.getEntities(FULL_SET.familyWithout(Components.FlagComponentA.class)),
                engine.getEntities(FULL_SET.familyWithout(Components.FlagComponentA.class)));
    }

    @Test
    public void testSame() {
        Engine engine = new Engine(FULL_SET);
        assertSame(engine.getEntities(FULL_SET.family()), engine.getEntities(FULL_SET.family()));
        assertSame(
                engine.getEntities(FULL_SET.familyWith(Components.FlagComponentA.class)),
                engine.getEntities(FULL_SET.familyWith(Components.FlagComponentA.class)));
        assertSame(
                engine.getEntities(FULL_SET.familyWithout(Components.FlagComponentA.class)),
                engine.getEntities(FULL_SET.familyWithout(Components.FlagComponentA.class)));
        assertNotSame(
                engine.getEntities(FULL_SET.familyWith(Components.FlagComponentA.class)),
                engine.getEntities(FULL_SET.familyWithout(Components.FlagComponentA.class)));
    }
}
