package com.darkyen.retinazer;

import com.darkyen.retinazer.util.Mask;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class EntitySetTest {

    private static void assertContainsOnly(EntitySet set, int...entities) {
        int[] next = { 0 };
        set.forEach((entity) -> {
            assertEquals(entities[next[0]++], entity);
        });
        assertEquals(entities.length, next[0], "Some entities are missing");
    }

    @Test
    public void testAddingAndRemoval() {
        EntitySet set = new EntitySet();
        set.addEntity(5);
        assertContainsOnly(set, 5);
        set.addEntity(19);
        assertContainsOnly(set, 5, 19);

        {// Full overlap, subset
            final Mask mask = new Mask();
            mask.set(5);
            mask.set(19);
            set.addEntities(mask);
            assertContainsOnly(set, 5, 19);
        }

        {// Partial overlap, subset
            final Mask mask = new Mask();
            mask.set(5);
            set.addEntities(mask);
            assertContainsOnly(set, 5, 19);
        }

        {// No overlap
            final Mask mask = new Mask();
            mask.set(23);
            set.addEntities(mask);
            assertContainsOnly(set, 5, 19, 23);
        }

        {// Full overlap, superset
            final Mask mask = new Mask();
            mask.set(5);
            mask.set(19);
            mask.set(23);
            mask.set(24);
            set.addEntities(mask);
            assertContainsOnly(set, 5, 19, 23, 24);
        }

        {// Partial overlap, superset
            final Mask mask = new Mask();
            mask.set(19);
            mask.set(23);
            mask.set(24);
            set.addEntities(mask);
            assertContainsOnly(set, 5, 19, 23, 24);
        }

        // Removing
        set.removeEntity(19);
        assertContainsOnly(set, 5, 23, 24);

        {// No overlap
            final Mask mask = new Mask();
            mask.set(1);
            mask.set(8);
            mask.set(15);
            set.removeEntities(mask);
            assertContainsOnly(set, 5, 23, 24);
        }

        {// Partial overlap
            final Mask mask = new Mask();
            mask.set(1);
            mask.set(8);
            mask.set(15);
            mask.set(23);
            set.removeEntities(mask);
            assertContainsOnly(set, 5, 24);
        }

        {// Full overlap
            final Mask mask = new Mask();
            mask.set(5);
            mask.set(24);
            set.removeEntities(mask);
            assertContainsOnly(set);
        }
    }

    @Test
    public void testIndices() {
        Engine engine = new Engine(Components.FULL_SET);
        Mapper<Components.FlagComponentA> mapper = engine.getMapper(Components.FlagComponentA.class);
        int entity0 = engine.createEntity();
        int entity1 = engine.createEntity();
        mapper.create(entity1);
        int entity2 = engine.createEntity();
        int entity3 = engine.createEntity();
        int entity4 = engine.createEntity();
        engine.update();
        assertArrayEquals(engine.getEntities().getIndices().toArray(), new int[]{entity0, entity1, entity2, entity3, entity4});
        assertArrayEquals(engine.getEntities(
                Components.FULL_SET.familyWith(Components.FlagComponentA.class)).getIndices().toArray(), new int[]{entity1});
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
