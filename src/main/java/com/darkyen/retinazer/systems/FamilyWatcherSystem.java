package com.darkyen.retinazer.systems;

import com.darkyen.retinazer.EntitySet;
import com.darkyen.retinazer.EntitySetView;
import com.darkyen.retinazer.EntitySystem;
import com.darkyen.retinazer.Family;
import com.darkyen.retinazer.util.Mask;
import org.jetbrains.annotations.NotNull;

/** {@link EntitySystem} which watches for entities to be added or removed to/from a {@link Family}. */
public abstract class FamilyWatcherSystem extends EntitySystem {

	private final Mask      lastEntities = new Mask();
	private final EntitySet workingSet   = new EntitySet();

	protected FamilyWatcherSystem(@NotNull Family family) {
		super(family);
	}

	@Override
	public void update() {
		final EntitySet workingSet = this.workingSet;
		final Mask lastEntities = this.lastEntities;
		final Mask currentEntities = this.getEntities().getMask();
		// Get added entities
		workingSet.getMaskForModification().set(currentEntities).andNot(lastEntities);
		insertedEntities(workingSet);
		// Get removed entities
		workingSet.getMaskForModification().set(lastEntities).andNot(currentEntities);
		removedEntities(workingSet);

		lastEntities.set(currentEntities);
	}

	/** Called on each update. {@code entities} contains all removed entities in this step, if any. */
	protected abstract void removedEntities(@NotNull EntitySetView entities);

	/** Called on each update. {@code entities} contains all added entities in this step, if any. */
	protected abstract void insertedEntities(@NotNull EntitySetView entities);

	/** Simplified {@link FamilyWatcherSystem} which gets the notification per-entity, not in bulk. */
	public static abstract class Single extends FamilyWatcherSystem {

		protected Single(@NotNull Family family) {
			super(family);
		}

		@Override
		protected final void insertedEntities(@NotNull EntitySetView entities) {
			final Mask mask = entities.getMask();
			// Using simple iteration without rebuilding indices, because it is unlikely that they will be needed again
			for (int entity = mask.nextSetBit(0); entity != -1; entity = mask.nextSetBit(entity + 1)) {
				insertedEntity(entity);
			}
		}

		@Override
		protected final void removedEntities(@NotNull EntitySetView entities) {
			final Mask mask = entities.getMask();
			// Using simple iteration without rebuilding indices, because it is unlikely that they will be needed again
			for (int entity = mask.nextSetBit(0); entity != -1; entity = mask.nextSetBit(entity + 1)) {
				removedEntity(entity);
			}
		}

		/** Called for each added entity. */
		protected abstract void insertedEntity(int entity);

		/** Called for each removed entity. */
		protected abstract void removedEntity(int entity);
	}
}
