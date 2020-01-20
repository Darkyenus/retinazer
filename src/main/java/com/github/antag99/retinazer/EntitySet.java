package com.github.antag99.retinazer;

import com.badlogic.gdx.utils.IntArray;
import com.github.antag99.retinazer.util.Mask;

public final class EntitySet implements EntitySetView {

    private final Mask entities = new Mask();
    private final IntArray indices = new IntArray();
    private boolean indicesDirty = false;

    public EntitySet() {}

    public EntitySet(EntitySet copyEntities) {
        this.entities.set(copyEntities.entities);
        this.indicesDirty = true;
    }

    public EntitySet(EntitySetView copyEntities) {
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

    public void addEntities(Mask entities) {
        if (!this.entities.isSupersetOf(entities)) {
            this.indicesDirty = true;
            this.entities.or(entities);
        }
    }

    public void setEntities(Mask entities) {
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

    public void removeEntities(Mask entities) {
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
    public Mask getMask() {
        return this.entities;
    }

    /**
     * Returns an array containing the indices of all entities in this set.
     * Note that whenever the entity set changes, this array must be
     * reconstructed. Do <b>not</b> modify this.
     *
     * @return the indices of all entities in this set.
     */
    public IntArray getIndices() {
        if (indicesDirty) {
            indices.clear();
            entities.getIndices(indices);
            indicesDirty = false;
        }
        return indices;
    }

    public int size() {
        return indicesDirty ? entities.cardinality() : indices.size;
    }

    public boolean isEmpty() {
        return indicesDirty ? entities.nextSetBit(0) == -1 : indices.size == 0;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof EntitySet && ((EntitySet) obj).entities.equals(entities);
    }

    @Override
    public int hashCode() {
        return entities.hashCode();
    }

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
