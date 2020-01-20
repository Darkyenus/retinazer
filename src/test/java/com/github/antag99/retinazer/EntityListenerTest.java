/******************************************************************************
 Copyright (C) 2015 Anton Gustafsson

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in
 all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 SOFTWARE.
 */
package com.github.antag99.retinazer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.antag99.retinazer.util.Mask;
import org.junit.jupiter.api.Test;

public class EntityListenerTest {
    private static class EntityListenerMock implements EntityListener {
        private final EntitySet insertedEntities = new EntitySet();
        private final EntitySet removedEntities = new EntitySet();

        @Override
        public void inserted(EntitySetView entities) {
            assertEquals(0, insertedEntities.size(), "Insertion without verification");
            insertedEntities.addEntities(entities.getMask());
        }

        @Override
        public void removed(EntitySetView entities) {
            assertEquals(0, removedEntities.size(), "Removal without verification");
            removedEntities.addEntities(entities.getMask());
        }

        public void verifyInserted(int... entities) {
            final Mask shouldBePresent = new Mask();
            for (int e : entities)
                shouldBePresent.set(e);
            assertEquals(shouldBePresent, insertedEntities.getMask(), "Insert verification failed");
            insertedEntities.clear();
        }

        public void verifyRemoved(int... entities) {
            final Mask shouldBePresent = new Mask();
            for (int e : entities)
                shouldBePresent.set(e);
            assertEquals(shouldBePresent, removedEntities.getMask(), "Remove verification failed");
            removedEntities.clear();
        }
    }

    @Test
    public void testEntityListener() {
        EntityListenerMock listener = new EntityListenerMock();
        Engine engine = new Engine(new EngineConfig());
        engine.addEntityListener(listener);
        int entity = engine.createEntity();
        listener.verifyInserted();
        listener.verifyRemoved();
        engine.update(0f);
        listener.verifyInserted(entity);
        listener.verifyRemoved();
        engine.destroyEntity(entity);
        listener.verifyInserted();
        listener.verifyRemoved();
        engine.update(0f);
        listener.verifyInserted();
        listener.verifyRemoved(entity);
    }

    @Test
    public void testFamilyListener() {
        EntityListenerMock listenerB = new EntityListenerMock();
        EntityListenerMock listenerC = new EntityListenerMock();
        Engine engine = new Engine(new EngineConfig());
        engine.getFamily(Family.with(FlagComponentB.class)).addListener(listenerB);
        engine.getFamily(Family.with(FlagComponentC.class)).addListener(listenerC);
        Mapper<FlagComponentB> mFlagB = engine.getMapper(FlagComponentB.class);
        Mapper<FlagComponentC> mFlagC = engine.getMapper(FlagComponentC.class);
        int entity = engine.createEntity();
        engine.update(0f);
        listenerB.verifyInserted();
        listenerB.verifyRemoved();
        listenerC.verifyInserted();
        listenerC.verifyRemoved();
        mFlagB.create(entity);
        listenerB.verifyInserted();
        listenerB.verifyRemoved();
        listenerC.verifyInserted();
        listenerC.verifyRemoved();
        engine.update(0f);
        listenerB.verifyInserted(entity);//
        listenerB.verifyRemoved();
        listenerC.verifyInserted();
        listenerC.verifyRemoved();
        mFlagB.remove(entity);
        engine.update(0f);
        listenerB.verifyInserted();
        listenerB.verifyRemoved(entity);
        listenerC.verifyInserted();
        listenerC.verifyRemoved();
        mFlagC.create(entity);
        engine.update(0f);
        listenerB.verifyInserted();
        listenerB.verifyRemoved();
        listenerC.verifyInserted(entity);
        listenerC.verifyRemoved();
        engine.destroyEntity(entity);
        engine.update(0f);
        listenerB.verifyInserted();
        listenerB.verifyRemoved();
        listenerC.verifyInserted();
        listenerC.verifyRemoved(entity);
    }
}
