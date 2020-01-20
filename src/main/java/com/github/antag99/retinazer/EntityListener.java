package com.github.antag99.retinazer;

import com.badlogic.gdx.utils.IntArray;

/** Listener for a {@link Family} or {@link Engine}. */
public interface EntityListener {

    EntityListener[] EMPTY_ARRAY = new EntityListener[0];

    /**
     * Called when entities are inserted.
     *
     * @param entities
     *            the entities that were inserted.
     */
    void inserted(EntitySetView entities);

    /**
     * Called when entities are removed.
     *
     * @param entities
     *            the entities that were removed.
     */
    void removed(EntitySetView entities);

    /** Interface adapter implementing most common EntityListener operation - iterating over inserted/removed entities. */
    interface EntityListenerAdapter extends EntityListener {

        /**
         * Called for each inserted entity.
         *
         * @param entity inserted
         */
        void inserted(int entity);

        /**
         * Called for each removed entity.
         *
         * @param entity removed
         */
        void removed(int entity);

        @Override
        default void inserted(EntitySetView entities) {
            final IntArray indices = entities.getIndices();
            final int size = indices.size;
            final int[] items = indices.items;
            for (int i = 0; i < size; i++) {
                inserted(items[i]);
            }
        }

        @Override
        default void removed(EntitySetView entities) {
            final IntArray indices = entities.getIndices();
            final int size = indices.size;
            final int[] items = indices.items;
            for (int i = 0; i < size; i++) {
                removed(items[i]);
            }
        }
    }
}
