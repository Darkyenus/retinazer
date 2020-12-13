package com.darkyen.retinazer;

import com.darkyen.retinazer.systems.EntityProcessorSystem;
import com.darkyen.retinazer.util.Mask;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EntityProcessorSystemTest {
	public static final class TestEntityProcessorSystem extends EntityProcessorSystem {
		public EntitySet processedEntities = new EntitySet();

		public TestEntityProcessorSystem() {
			super(ComponentSet.EMPTY.family());
		}

		@Override
		protected void process(int entity) {
			if (processedEntities.contains(entity))
				throw new AssertionError("entity already processed: " + entity);
			processedEntities.addEntity(entity);
		}
	}

	@Test
	public void testEntityProcessorSystem() {
		TestEntityProcessorSystem system = new TestEntityProcessorSystem();
		Engine engine = new Engine(ComponentSet.EMPTY, system);
		Mask entities = new Mask();
		int a, b, c;
		entities.set(a = engine.createEntity());
		entities.set(b = engine.createEntity());
		entities.set(c = engine.createEntity());
		engine.update();
		assertEquals(system.processedEntities.getMask(), entities);
		system.processedEntities.clear();
		engine.destroyEntity(b);
		entities.clear(b);
		engine.update();
		assertEquals(system.processedEntities.getMask(), entities);
		system.processedEntities.clear();
		entities.set(b = engine.createEntity());
		engine.update();
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
