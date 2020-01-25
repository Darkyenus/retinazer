package com.github.antag99.retinazer;

import com.badlogic.gdx.utils.IntArray;
import com.github.antag99.retinazer.util.Mask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.IntConsumer;

public final class EntitySet implements EntitySetView {

    private final Mask entities = new Mask();
    private final IntArray indices = new IntArray();
    private boolean indicesDirty = false;

    public EntitySet() {}

    public EntitySet(@NotNull EntitySet copyEntities) {
        this.entities.set(copyEntities.entities);
        this.indicesDirty = true;
    }

    public EntitySet(@NotNull EntitySetView copyEntities) {
        this.entities.set(copyEntities.getMask());
        this.indicesDirty = true;
    }

    /**
     * Checks if this set contains the given entity.
     *
     * @param entity
     *            the entity to check for.
     * @return whether this set contains the given entity.
     */
    public boolean contains(int entity) {
        return this.entities.get(entity);
    }

    public void addEntity(int entity) {
        indicesDirty |= entities.setChanged(entity);
    }

    public void addEntities(@NotNull EntitySetView entities) {
        addEntities(entities.getMask());
    }

    public void addEntities(@NotNull Mask entities) {
        if (!this.entities.isSupersetOf(entities)) {
            this.indicesDirty = true;
            this.entities.or(entities);
        }
    }

    public void setEntities(@NotNull EntitySetView entities) {
        setEntities(entities.getMask());
    }

    public void setEntities(@NotNull Mask entities) {
        if (!this.entities.equals(entities)) {
            this.indicesDirty = true;
            this.entities.set(entities);
        }
    }

    public void removeEntity(int entity) {
        if (entities.clearChanged(entity)) {
            this.indicesDirty = true;
        }
    }

    public void removeEntities(@NotNull EntitySetView entities) {
        removeEntities(entities.getMask());
    }

    public void removeEntities(@NotNull Mask entities) {
        if (this.entities.intersects(entities)) {
            this.indicesDirty = true;
            this.entities.andNot(entities);
        }
    }

    public void clear() {
        this.entities.clear();
        this.indices.clear();
        this.indicesDirty = false;
    }

    /**
     * Returns the entities contained in this entity set.
     * Do <b>not</b> modify this.
     *
     * @return the entities contained in this set.
     */
    @NotNull
    public Mask getMask() {
        return this.entities;
    }

    /**
     * Similar behavior to {@link #getMask()}, but the mask may be modified.
     * After any other call to any of this instance's methods, the mask may no longer be modified.
     */
    @NotNull
    public Mask getMaskForModification() {
        this.indicesDirty = true;
        return this.entities;
    }

    /**
     * Returns an array containing the indices of all entities in this set.
     * Note that whenever the entity set changes, this array must be
     * reconstructed. Do <b>not</b> modify this.
     *
     * @return the indices of all entities in this set.
     */
    @NotNull
    public IntArray getIndices() {
        if (indicesDirty) {
            indices.clear();
            entities.getIndices(indices);
            indicesDirty = false;
        }
        return indices;
    }

    /** For each entity in this set, call the action consumer with the entity ID. */
    @Override
    public void forEach(@NotNull IntConsumer action) {
        final IntArray indices = this.indices;
        if (indicesDirty) {
            // Build indices
            // After some benchmarking, this seems to be pretty fast, even though it calls IntArray.add repeatedly
            indices.clear();
            final Mask entities = this.entities;
            for (int entity = entities.nextSetBit(0); entity != -1; entity = entities.nextSetBit(entity + 1)) {
                indices.add(entity);
                action.accept(entity);
            }
            // Done at the end, because action may throw, which would leave us in a wrong state
            indicesDirty = false;
        } else {
            // Use indices
            final int[] items = indices.items;
            final int size = indices.size;
            for (int i = 0; i < size; i++) {
                action.accept(items[i]);
            }
        }
    }

    public int size() {
        return indicesDirty ? entities.cardinality() : indices.size;
    }

    public boolean isEmpty() {
        return indicesDirty ? entities.nextSetBit(0) == -1 : indices.size == 0;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return obj instanceof EntitySet && ((EntitySet) obj).entities.equals(entities);
    }

    @Override
    public int hashCode() {
        return entities.hashCode();
    }

    @NotNull
    @Override
    public String toString() {
        IntArray indices = getIndices();
        if (indices.size == 0) {
            return "[]";
        }
        StringBuilder builder = new StringBuilder();
        builder.append('[');
        int[] items = indices.items;
        builder.append(items[0]);
        for (int i = 1, n = indices.size; i < n; i++) {
            builder.append(',');
            builder.append(' ');
            builder.append(items[i]);
        }
        builder.append(']');
        return builder.toString();
    }
}
