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

public abstract class EntityProcessorSystem extends EntitySystem {
    private FamilyConfig family;
    private EntitySet entities;

    public EntityProcessorSystem(FamilyConfig family) {
        this.family = family;
    }

    @Override
    public void setup() {
        super.setup();

        entities = engine.getFamily(getFamily()).getEntities();
    }

    public final EntitySet getEntities() {
        return entities;
    }

    public final FamilyConfig getFamily() {
        return family;
    }

    @Override
    public final void update(float delta) {
        processEntities(delta);
    }

    protected void processEntities(float delta) {
        IntArray indices = getEntities().getIndices();
        int[] items = indices.items;
        for (int i = 0, n = indices.size; i < n; i++) {
            process(items[i], delta);
        }
    }

    /**
     * Process single entity in the family
     * @param delta time in seconds since last update
     */
    protected abstract void process(int entity, float delta);
}
