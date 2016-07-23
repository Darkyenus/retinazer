package com.github.antag99.retinazer;

import com.badlogic.gdx.utils.IntArray;
import com.github.antag99.retinazer.util.Mask;

public final class EntitySetView {

    private final EntitySet content;

    EntitySetView(EntitySet content) {
        this.content = content;
    }

    /**
     * Checks if this set contains the given entity.
     *
     * @param entity
     *            the entity to check for.
     * @return whether this set contains the given entity.
     */
    public boolean contains(int entity) {
        return content.contains(entity);
    }

    /**
     * Returns the entities contained in this entity set.
     * Do <b>not</b> modify this.
     *
     * @return the entities contained in this set.
     */
    public Mask getMask() {
        return content.getMask();
    }

    /**
     * Returns an array containing the indices of all entities in this set.
     * Note that whenever the entity set changes, this array must be
     * reconstructed. Do <b>not</b> modify this.
     *
     * @return the indices of all entities in this set.
     */
    public IntArray getIndices() {
        return content.getIndices();
    }

    public int size() {
        return content.size();
    }

    public boolean isEmpty() {
        return content.isEmpty();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof EntitySetView && ((EntitySetView) obj).content.equals(content);
    }

    @Override
    public int hashCode() {
        return ~content.hashCode();
    }

    @Override
    public String toString() {
        return "View "+content.toString();
    }
}
