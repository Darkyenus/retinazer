package com.github.antag99.retinazer;

import com.badlogic.gdx.utils.IntArray;
import com.github.antag99.retinazer.util.Mask;
import org.jetbrains.annotations.NotNull;

import java.util.function.IntConsumer;

public interface EntitySetView {

    /**
     * Checks if this set contains the given entity.
     *
     * @param entity
     *            the entity to check for.
     * @return whether this set contains the given entity.
     */
    boolean contains(int entity);

    /**
     * Returns the entities contained in this entity set.
     * Do <b>not</b> modify this.
     *
     * @return the entities contained in this set.
     */
    @NotNull
    Mask getMask();

    /**
     * Returns an array containing the indices of all entities in this set.
     * Note that whenever the entity set changes, this array must be
     * reconstructed. Do <b>not</b> modify this.
     *
     * @return the indices of all entities in this set.
     */
    @NotNull
    IntArray getIndices();

    /** For each entity in this set, call the action consumer with the entity ID. */
    void forEach(@NotNull IntConsumer action);

    int size();

    boolean isEmpty();
}
