package com.github.antag99.retinazer.systems;

import com.github.antag99.retinazer.EntitySystem;
import com.github.antag99.retinazer.Family;
import com.github.antag99.retinazer.util.Mask;

/** {@link EntitySystem} which watches for entities to be added or removed to/from a {@link Family}. */
public abstract class FamilyWatcherSystem extends EntitySystem {

	private final Mask lastEntities = new Mask();
	private final Mask workingSet = new Mask();

	protected FamilyWatcherSystem(Family family) {
		super(family);
	}

	@Override
	public void update(float delta) {
		final Mask workingSet = this.workingSet;
		final Mask lastEntities = this.lastEntities;
		final Mask currentEntities = this.getEntities().getMask();
		// Get added entities
		workingSet.set(currentEntities).andNot(lastEntities);
		insertedEntities(workingSet, delta);
		// Get removed entities
		workingSet.set(lastEntities).andNot(currentEntities);
		removedEntities(workingSet, delta);

		lastEntities.set(currentEntities);
	}

	/** Called on each update. {@code entities} contains all removed entities in this step, if any. */
	protected abstract void removedEntities(Mask entities, float delta);

	/** Called on each update. {@code entities} contains all added entities in this step, if any. */
	protected abstract void insertedEntities(Mask entities, float delta);

	/** Simplified {@link FamilyWatcherSystem} which gets the notification per-entity, not in bulk. */
	public static abstract class Single extends FamilyWatcherSystem {

		protected Single(Family family) {
			super(family);
		}

		@Override
		protected final void insertedEntities(Mask entities, float delta) {
			for (int entity = entities.nextSetBit(0); entity != -1; entity = entities.nextSetBit(entity + 1)) {
				insertedEntity(entity, delta);
			}
		}

		@Override
		protected final void removedEntities(Mask entities, float delta) {
			for (int entity = entities.nextSetBit(0); entity != -1; entity = entities.nextSetBit(entity + 1)) {
				removedEntity(entity, delta);
			}
		}

		/** Called for each added entity. */
		protected abstract void insertedEntity(int entity, float delta);
		/** Called for each removed entity. */
		protected abstract void removedEntity(int entity, float delta);
	}
}
