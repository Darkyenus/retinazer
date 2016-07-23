package com.github.antag99.retinazer;

import static org.junit.Assert.*;

import org.junit.Test;

import com.github.antag99.retinazer.util.Mask;

public class EntityProcessorSystemTest {
    public static final class TestEntityProcessorSystem extends EntityProcessorSystem {
        public EntitySet processedEntities = new EntitySet();

        public TestEntityProcessorSystem() {
            super(Family.create());
        }

        @Override
        protected void process(int entity, float delta) {
            if (processedEntities.contains(entity))
                throw new AssertionError("entity already processed: " + entity);
            processedEntities.addEntity(entity);
        }
    }

    @Test
    public void testEntityProcessorSystem() {
        TestEntityProcessorSystem system = new TestEntityProcessorSystem();
        Engine engine = new Engine(new EngineConfig().addSystem(system));
        Mask entities = new Mask();
        int a, b, c;
        entities.set(a = engine.createEntity());
        entities.set(b = engine.createEntity());
        entities.set(c = engine.createEntity());
        engine.update(0f);
        assertEquals(system.processedEntities.getMask(), entities);
        system.processedEntities.clear();
        engine.destroyEntity(b);
        entities.clear(b);
        engine.update(0f);
        assertEquals(system.processedEntities.getMask(), entities);
        system.processedEntities.clear();
        entities.set(b = engine.createEntity());
        engine.update(0f);
        assertEquals(system.processedEntities.getMask(), entities);
        system.processedEntities.clear();
        engine.destroyEntity(a);
        engine.destroyEntity(b);
        engine.destroyEntity(c);
        entities.clear(a);
        entities.clear(b);
        entities.clear(c);
    }
}
