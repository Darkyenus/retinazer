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
import com.github.antag99.retinazer.util.Mask;

public final class EntitySet {

    private final Mask entities = new Mask();
    private final IntArray indices = new IntArray();
    private boolean indicesDirty = false;

    private final EntitySetView view;

    public EntitySet() {
        this.view = new EntitySetView(this);
    }

    public EntitySet(EntitySet copyEntities) {
        this();
        this.entities.set(copyEntities.entities);
        this.indicesDirty = true;
    }

    public EntitySet(EntitySetView copyEntities) {
        this();
        this.entities.set(copyEntities.getMask());
        this.indicesDirty = true;
    }

    /**
     * Returns an unmodifiable view of this entity set.
     *
     * @return Unmodifiable view of this entity set.
     */
    public EntitySetView view() {
        return view;
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
        if (!this.entities.get(entity)) {
            this.indicesDirty = true;
            this.entities.set(entity);
        }
    }

    public void addEntities(Mask entities) {
        if (!this.entities.isSupersetOf(entities)) {
            this.indicesDirty = true;
            this.entities.or(entities);
        }
    }

    public void removeEntity(int entity) {
        if (this.entities.get(entity)) {
            this.indicesDirty = true;
            this.entities.clear(entity);
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
        return size() == 0;
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
