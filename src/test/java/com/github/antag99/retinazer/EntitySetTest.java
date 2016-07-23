package com.github.antag99.retinazer;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class EntitySetTest {

    @Test
    public void testIndices() {
        Engine engine = new Engine(new EngineConfig());
        Mapper<FlagComponentA> mapper = engine.getMapper(FlagComponentA.class);
        int entity0 = engine.createEntity();
        int entity1 = engine.createEntity();
        mapper.create(entity1);
        int entity2 = engine.createEntity();
        int entity3 = engine.createEntity();
        int entity4 = engine.createEntity();
        engine.update(0f);
        assertTrue(Arrays.equals(engine.getEntities().getIndices().toArray(),
                new int[] { entity0, entity1, entity2, entity3, entity4 }));
        assertTrue(Arrays.equals(engine.getFamily(
                Family.with(FlagComponentA.class)).getEntities().getIndices().toArray(),
                new int[] { entity1 }));
    }

    @Test
    public void testEquals() {
        EntitySet a = new EntitySet();
        EntitySet b = new EntitySet();
        assertEquals(a, b);
        assertEquals(a, b);
        a.addEntity(0);
        assertNotEquals(a, b);
        a.addEntity(54);
        assertNotEquals(a, b);
        assertNotEquals(a, new Object());
    }

    @Test
    public void testHashcode() {
        EntitySet a = new EntitySet();
        EntitySet b = new EntitySet();
        assertEquals(a.hashCode(), b.hashCode());
        assertEquals(a.hashCode(), b.hashCode());
        a.addEntity(0);
        assertNotEquals(a.hashCode(), b.hashCode());
        a.addEntity(54);
        assertNotEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void testToString() {
        EntitySet set = new EntitySet();
        assertEquals("[]", set.toString());
        set.addEntity(0);
        assertEquals("[0]", set.toString());
        set.addEntity(4);
        assertEquals("[0, 4]", set.toString());
        set.addEntity(2);
        assertEquals("[0, 2, 4]", set.toString());
    }
}