package com.github.antag99.retinazer;

import org.junit.jupiter.api.Test;

import static com.github.antag99.retinazer.Components.FULL_SET;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MapperTest {

    // This should usually not be done... bad.
    public static final class BadComponent implements Component {
        public BadComponent(@SuppressWarnings("unused") int requiresAnArgument) {
        }
    }

    @Test()
    public void testNoConstructor() {
        Engine engine = new Engine(new ComponentSet(BadComponent.class));
        int entity = engine.createEntity();
        Mapper<BadComponent> mBad = engine.getMapper(BadComponent.class);
        assertThrows(UnsupportedOperationException.class, () -> mBad.create(entity));
    }

    // This should *never* be done
    public static final class ReallyBadComponent implements Component {
        public ReallyBadComponent() throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }
    }

    @Test()
    public void testErrorConstructor() {
        Engine engine = new Engine(new ComponentSet(ReallyBadComponent.class));
        int entity = engine.createEntity();
        Mapper<ReallyBadComponent> mReallyBad = engine.getMapper(ReallyBadComponent.class);
        assertThrows(RuntimeException.class, () -> mReallyBad.create(entity));
    }

    @Test
    public void testRemoveNothing() {
        Engine engine = new Engine(FULL_SET);
        Mapper<Components.FlagComponentA> mFlagA = engine.getMapper(Components.FlagComponentA.class);
        int entity = engine.createEntity();
        mFlagA.remove(entity); // nothing should happen
        engine.update();
        mFlagA.create(entity);
        mFlagA.remove(entity);
        mFlagA.remove(entity);
        engine.update();
    }

    @Test()
    public void testAddTwice() {
        Engine engine = new Engine(FULL_SET);
        Mapper<Components.FlagComponentA> mFlagA = engine.getMapper(Components.FlagComponentA.class);
        int entity = engine.createEntity();
        mFlagA.add(entity, new Components.FlagComponentA());
        assertThrows(IllegalArgumentException.class, () -> mFlagA.add(entity, new Components.FlagComponentA()));
    }
}
