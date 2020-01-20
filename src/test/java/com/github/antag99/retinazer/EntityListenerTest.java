package com.github.antag99.retinazer;

import static com.github.antag99.retinazer.Components.FULL_SET;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.antag99.retinazer.systems.FamilyPresenceWatcherSystem;
import com.github.antag99.retinazer.util.Mask;
import org.junit.jupiter.api.Test;

public class EntityListenerTest {
    private static class EntityListenerMock extends FamilyPresenceWatcherSystem {
        private final EntitySet insertedEntities = new EntitySet();
        private final EntitySet removedEntities = new EntitySet();

        protected EntityListenerMock(FamilySpec familySpec) {
            super(familySpec);
        }

        @Override
        protected void insertedEntities(Mask entities, float delta) {
            assertEquals(0, insertedEntities.size(), "Insertion without verification");
            insertedEntities.addEntities(entities);
        }

        @Override
        protected void removedEntities(Mask entities, float delta) {
            assertEquals(0, removedEntities.size(), "Removal without verification");
            removedEntities.addEntities(entities);
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
        EntityListenerMock listener = new EntityListenerMock(ComponentSet.EMPTY.family());
        Engine engine = new Engine(ComponentSet.EMPTY, listener);
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
        EntityListenerMock listenerB = new EntityListenerMock(FULL_SET.familyWith(Components.FlagComponentB.class));
        EntityListenerMock listenerC = new EntityListenerMock(FULL_SET.familyWith(Components.FlagComponentC.class));
        Engine engine = new Engine(FULL_SET, listenerB, listenerC);
        Mapper<Components.FlagComponentB> mFlagB = engine.getMapper(Components.FlagComponentB.class);
        Mapper<Components.FlagComponentC> mFlagC = engine.getMapper(Components.FlagComponentC.class);
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
