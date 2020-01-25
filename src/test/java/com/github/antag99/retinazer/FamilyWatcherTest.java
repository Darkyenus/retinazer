package com.github.antag99.retinazer;

import com.github.antag99.retinazer.systems.FamilyWatcherSystem;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 */
public class FamilyWatcherTest {

	private static class EntityAdder implements EngineService {

		@Wire
		private Engine engine;

		public final ArrayList<Integer> addedEntities = new ArrayList<>();

		private int blanks;
		private int rounds;

		public EntityAdder(int blanks, int rounds) {
			this.blanks = blanks;
			this.rounds = rounds;
		}

		public EntityAdder() {
			this(0, 1);
		}

		@Override
		public void update() {
			if (blanks > 0) {
				blanks--;
				return;
			}
			if (rounds > 0) {
				addedEntities.add(engine.createEntity());
				rounds--;
			}
		}
	}

	private static class EntityRemover implements EngineService {

		@Wire
		private Engine engine;

		@Wire
		private EntityAdder adder;

		@Override
		public void update() {
			for (Integer entity : adder.addedEntities) {
				engine.destroyEntity(entity);
			}
			adder.addedEntities.clear();
		}
	}

	private static final class EntityWatcher extends FamilyWatcherSystem {

		protected EntityWatcher() {
			super(ComponentSet.EMPTY.family());
		}

		public int added = 0;
		public int removed = 0;

		@Override
		public void update() {
			added = 0;
			removed = 0;
			super.update();
		}

		@Override
		protected void insertedEntities(@NotNull EntitySetView entities) {
			added += entities.getMask().cardinality();
		}

		@Override
		protected void removedEntities(@NotNull EntitySetView entities) {
			removed += entities.getMask().cardinality();
		}
	}

	@Test
	public void familyWatcherTestARW() {
		Engine engineAddRemoveWatch = new Engine(ComponentSet.EMPTY,
				new EntityAdder(),
				new EntityRemover(),
				new EntityWatcher());
		engineAddRemoveWatch.update();
		final EntityWatcher watcher = engineAddRemoveWatch.getService(EntityWatcher.class);
		assertEquals(0, watcher.added, "Added");
		assertEquals(0, watcher.removed, "Removed");
	}

	@Test
	public void familyWatcherTestAWR() {
		Engine engineAddRemoveWatch = new Engine(ComponentSet.EMPTY,
				new EntityAdder(),
				new EntityWatcher(),
				new EntityRemover());
		final EntityWatcher watcher = engineAddRemoveWatch.getService(EntityWatcher.class);
		engineAddRemoveWatch.update();
		assertEquals(1, watcher.added, "Added");
		assertEquals(0, watcher.removed, "Removed");
		engineAddRemoveWatch.update();
		assertEquals(0, watcher.added, "Added");
		assertEquals(1, watcher.removed, "Removed");
	}

	@Test
	public void familyWatcherTestWAR() {
		Engine engineAddRemoveWatch = new Engine(ComponentSet.EMPTY,
				new EntityWatcher(),
				new EntityAdder(),
				new EntityRemover());
		final EntityWatcher watcher = engineAddRemoveWatch.getService(EntityWatcher.class);
		engineAddRemoveWatch.update();
		assertEquals(0, watcher.added, "Added");
		assertEquals(0, watcher.removed, "Removed");
		engineAddRemoveWatch.update();
		assertEquals(0, watcher.added, "Added");
		assertEquals(0, watcher.removed, "Removed");
	}

	@Test
	public void familyWatcherTestRWA() {
		Engine engineAddRemoveWatch = new Engine(ComponentSet.EMPTY,
				new EntityRemover(),
				new EntityWatcher(),
				new EntityAdder());
		final EntityWatcher watcher = engineAddRemoveWatch.getService(EntityWatcher.class);
		engineAddRemoveWatch.update();
		assertEquals(0, watcher.added, "Added");
		assertEquals(0, watcher.removed, "Removed");
		engineAddRemoveWatch.update();
		assertEquals(0, watcher.added, "Added");
		assertEquals(0, watcher.removed, "Removed");
	}

	@Test
	public void familyWatcherTestRAW() {
		Engine engineAddRemoveWatch = new Engine(ComponentSet.EMPTY,
				new EntityRemover(),
				new EntityAdder(),
				new EntityWatcher());
		final EntityWatcher watcher = engineAddRemoveWatch.getService(EntityWatcher.class);
		engineAddRemoveWatch.update();
		assertEquals(1, watcher.added, "Added");
		assertEquals(0, watcher.removed, "Removed");
		engineAddRemoveWatch.update();
		assertEquals(0, watcher.added, "Added");
		assertEquals(1, watcher.removed, "Removed");
	}

	@Test
	public void familyWatcherAliasing() {
		// This used to trigger an ABA problem for watchers
		Engine engineAddRemoveWatch = new Engine(ComponentSet.EMPTY,
				new EntityAdder(1, 1){}, // Fires on the second round
				new EntityAdder(),
				new EntityWatcher(),
				new EntityRemover(),
				new EntityAdder() {}// Second adder, which fills the hole after the removal. Watcher should detect this.
				);
		final EntityWatcher watcher = engineAddRemoveWatch.getService(EntityWatcher.class);
		engineAddRemoveWatch.update();
		assertEquals(1, watcher.added, "Added");
		assertEquals(0, watcher.removed, "Removed");
		engineAddRemoveWatch.update();
		assertEquals(2, watcher.added, "Added");
		assertEquals(1, watcher.removed, "Removed");
	}

}
