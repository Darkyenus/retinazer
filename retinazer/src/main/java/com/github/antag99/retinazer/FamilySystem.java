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

import com.github.antag99.retinazer.utils.Inject;

public abstract class FamilySystem extends EntitySystem {
    private @Inject Engine engine;
    private FamilyConfig family;
    private Iterable<Entity> entities;

    public FamilySystem(FamilyConfig family) {
        this.family = family;
    }

    @Override
    public void initialize() {
        entities = engine.getEntitiesFor(family);
    }

    @Override
    public void destroy() {
        entities = null;
    }

    @Override
    protected final void update() {
        for (Entity entity : entities) {
            process(entity);
        }
    }

    public Iterable<Entity> getEntities() {
        return entities;
    }

    public FamilyConfig getFamily() {
        return family;
    }

    protected abstract void process(Entity entity);
}
