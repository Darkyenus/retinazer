/*******************************************************************************
 * Copyright (C) 2015 Anton Gustafsson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package com.github.antag99.retinazer;

import com.badlogic.gdx.utils.IntArray;

/**
 * Listener for a {@link Family} or {@link Engine}.
 */
public interface EntityListener {

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
