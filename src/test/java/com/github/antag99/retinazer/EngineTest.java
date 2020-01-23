package com.github.antag99.retinazer;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;

import static com.github.antag99.retinazer.Components.FULL_SET;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class EngineTest {
    private Array<EngineService> initializedSystems = new Array<>();
    private Array<EngineService> updatedSystems = new Array<>();

    public abstract class OrderSystem implements EngineService {

        @Override
        public final void initialize() {
            initializedSystems.add(this);
        }

        @Override
        public final void update(float delta) {
            updatedSystems.add(this);
        }
    }

    @Test
    public void testEngine() {
        Engine engine = new Engine(ComponentSet.EMPTY);
        engine.update(0f);
        engine.update(0f);
        engine.update(0f);
        engine.update(0f);
        engine.update(0f);
        engine.update(0f);
        engine.update(0f);
        engine.update(0f);
    }

    @Test
    public void testEntitySystemPriority() {
        EngineService serviceA = new OrderSystem() {
            @Override
            public String toString() {
                return "A";
            }
        };

        EngineService serviceB = new OrderSystem() {
            @Override
            public String toString() {
                return "B";
            }
        };

        EngineService serviceC = new OrderSystem() {
            @Override
            public String toString() {
                return "C";
            }
        };

        EngineService serviceD = new OrderSystem() {
            @Override
            public String toString() {
                return "D";
            }
        };

        EngineService serviceE = new OrderSystem() {
            @Override
            public String toString() {
                return "E";
            }
        };

        Engine engine = new Engine(ComponentSet.EMPTY,
                serviceC,
                serviceA,
                serviceB,
                serviceE,
                serviceD
        );
        assertEquals(Array.with(serviceC,
                serviceA,
                serviceB,
                serviceE,
                serviceD), initializedSystems);
        initializedSystems.clear();
        engine.update(0f);
        assertEquals(Array.with(serviceC,
                serviceA,
                serviceB,
                serviceE,
                serviceD), updatedSystems);
        updatedSystems.clear();
    }

    private Set<Integer> asSet(int... entities) {
        HashSet<Integer> set = new HashSet<>();
        for (int entity : entities)
            set.add(entity);
        return set;
    }

    private Set<Integer> asSet(IntArray entities) {
        return asSet(entities.toArray());
    }

    @Test
    public void testEntityRetrieval() {
        Engine engine = new Engine(FULL_SET);
        Mapper<Components.FlagComponentA> mFlagA = engine.getMapper(Components.FlagComponentA.class);
        Mapper<Components.FlagComponentB> mFlagB = engine.getMapper(Components.FlagComponentB.class);
        Mapper<Components.FlagComponentC> mFlagC = engine.getMapper(Components.FlagComponentC.class);

        int entity0 = engine.createEntity();

        int entity1 = engine.createEntity();
        mFlagA.create(entity1);

        int entity2 = engine.createEntity();
        mFlagB.create(entity2);

        int entity3 = engine.createEntity();
        mFlagC.create(entity3);

        int entity4 = engine.createEntity();
        mFlagA.create(entity4);
        mFlagB.create(entity4);

        int entity5 = engine.createEntity();
        mFlagB.create(entity5);
        mFlagC.create(entity5);

        int entity6 = engine.createEntity();
        mFlagA.create(entity6);
        mFlagC.create(entity6);

        int entity7 = engine.createEntity();
        mFlagA.create(entity7);
        mFlagB.create(entity7);
        mFlagC.create(entity7);

        engine.update(0f);

        assertEquals(
                asSet(entity0, entity1, entity2, entity3, entity4, entity5, entity6, entity7),
                asSet(engine.getEntities().getIndices()));

        assertEquals(
                asSet(entity0, entity1, entity2, entity3, entity4, entity5, entity6, entity7),
                asSet(engine.getEntities(FULL_SET.familyWith()).getIndices()));

        assertEquals(
                asSet(entity0, entity1, entity2, entity3, entity4, entity5, entity6, entity7),
                asSet(engine.getEntities(FULL_SET.familyWithout()).getIndices()));

        assertEquals(
                asSet(entity0, entity1, entity2, entity3, entity4, entity5, entity6, entity7),
                asSet(engine.getEntities(FULL_SET.familyWith().without()).getIndices()));

        assertEquals(
                asSet(entity1, entity4, entity6, entity7),
                asSet(engine.getEntities(FULL_SET.familyWith(Components.FlagComponentA.class)).getIndices()));

        assertEquals(
                asSet(entity2, entity4, entity5, entity7),
                asSet(engine.getEntities(FULL_SET.familyWith(Components.FlagComponentB.class)).getIndices()));

        assertEquals(
                asSet(entity3, entity5, entity6, entity7),
                asSet(engine.getEntities(FULL_SET.familyWith(Components.FlagComponentC.class)).getIndices()));

        assertEquals(
                asSet(entity4, entity7),
                asSet(engine.getEntities(FULL_SET.familyWith(Components.FlagComponentA.class, Components.FlagComponentB.class)).getIndices()));

        assertEquals(
                asSet(entity6, entity7),
                asSet(engine.getEntities(FULL_SET.familyWith(Components.FlagComponentA.class, Components.FlagComponentC.class)).getIndices()));

        assertEquals(
                asSet(entity5, entity7),
                asSet(engine.getEntities(FULL_SET.familyWith(Components.FlagComponentB.class, Components.FlagComponentC.class)).getIndices()));

        assertEquals(
                asSet(entity7),
                asSet(engine.getEntities(FULL_SET.familyWith(Components.FlagComponentA.class, Components.FlagComponentB.class, Components.FlagComponentC.class)).getIndices()));

        assertEquals(
                asSet(entity0, entity2, entity3, entity5),
                asSet(engine.getEntities(FULL_SET.familyWithout(Components.FlagComponentA.class)).getIndices()));

        assertEquals(
                asSet(entity0, entity1, entity3, entity6),
                asSet(engine.getEntities(FULL_SET.familyWithout(Components.FlagComponentB.class)).getIndices()));

        assertEquals(
                asSet(entity0, entity1, entity2, entity4),
                asSet(engine.getEntities(FULL_SET.familyWithout(Components.FlagComponentC.class)).getIndices()));

        assertEquals(
                asSet(entity0, entity3),
                asSet(engine.getEntities(FULL_SET.familyWithout(Components.FlagComponentA.class, Components.FlagComponentB.class)).getIndices()));

        assertEquals(
                asSet(entity0, entity2),
                asSet(engine.getEntities(FULL_SET.familyWithout(Components.FlagComponentA.class, Components.FlagComponentC.class)).getIndices()));

        assertEquals(
                asSet(entity0, entity1),
                asSet(engine.getEntities(FULL_SET.familyWithout(Components.FlagComponentB.class, Components.FlagComponentC.class)).getIndices()));

        assertEquals(
                asSet(entity0),
                asSet(engine.getEntities(
                        FULL_SET.familyWithout(Components.FlagComponentA.class, Components.FlagComponentB.class, Components.FlagComponentC.class)).getIndices()));

        assertEquals(
                asSet(entity1, entity6),
                asSet(engine.getEntities(FULL_SET.familyWith(Components.FlagComponentA.class).without(Components.FlagComponentB.class)).getIndices()));

        assertEquals(
                asSet(entity1, entity4),
                asSet(engine.getEntities(FULL_SET.familyWith(Components.FlagComponentA.class).without(Components.FlagComponentC.class)).getIndices()));

        assertEquals(
                asSet(entity2, entity5),
                asSet(engine.getEntities(FULL_SET.familyWith(Components.FlagComponentB.class).without(Components.FlagComponentA.class)).getIndices()));

        assertEquals(
                asSet(entity2, entity4),
                asSet(engine.getEntities(FULL_SET.familyWith(Components.FlagComponentB.class).without(Components.FlagComponentC.class)).getIndices()));

        assertEquals(
                asSet(entity3, entity5),
                asSet(engine.getEntities(FULL_SET.familyWith(Components.FlagComponentC.class).without(Components.FlagComponentA.class)).getIndices()));

        assertEquals(
                asSet(entity3, entity6),
                asSet(engine.getEntities(FULL_SET.familyWith(Components.FlagComponentC.class).without(Components.FlagComponentB.class)).getIndices()));

        assertEquals(
                asSet(entity1),
                asSet(engine.getEntities(
                        FULL_SET.familyWith(Components.FlagComponentA.class).without(Components.FlagComponentB.class, Components.FlagComponentC.class)).getIndices()));

        assertEquals(
                asSet(entity2),
                asSet(engine.getEntities(
                        FULL_SET.familyWith(Components.FlagComponentB.class).without(Components.FlagComponentA.class, Components.FlagComponentC.class)).getIndices()));

        assertEquals(
                asSet(entity3),
                asSet(engine.getEntities(
                        FULL_SET.familyWith(Components.FlagComponentC.class).without(Components.FlagComponentA.class, Components.FlagComponentB.class)).getIndices()));
    }

    public static class MissingService {
    }

    public static class MissingServiceConsumer {
        @SuppressWarnings("unused")
        private @Wire MissingService service;
    }

    @Test
    public void testMissingDependencyInjection() {
        MissingServiceConsumer consumer = new MissingServiceConsumer();
        final Engine engine = new Engine(ComponentSet.EMPTY);
        assertThrows(RuntimeException.class, () -> engine.wire(consumer));
    }

    public static class ExampleSystem implements EngineService {
        public @Wire Engine engine;
        public @Wire
        Components.FlagSystemA flagSystemA;
        public @Wire
        Components.FlagSystemB flagSystemB;
        public @Wire
        Components.FlagSystemC flagSystemC;
        public @Wire Mapper<Components.FlagComponentA> mFlagA;
        public @Wire Mapper<Components.FlagComponentB> mFlagB;
        public @Wire Mapper<Components.FlagComponentC> mFlagC;
    }

    @Test
    public void testEngineDependencyInjection() {
        ExampleSystem system = new ExampleSystem();
        Components.FlagSystemA flagSystemA = new Components.FlagSystemA();
        Components.FlagSystemB flagSystemB = new Components.FlagSystemB();
        Components.FlagSystemC flagSystemC = new Components.FlagSystemC();
        Engine engine = new Engine(FULL_SET, system, flagSystemA, flagSystemB, flagSystemC);
        assertSame(engine, system.engine);
        assertSame(flagSystemA, system.flagSystemA);
        assertSame(flagSystemB, system.flagSystemB);
        assertSame(flagSystemC, system.flagSystemC);
        assertSame(engine.getMapper(Components.FlagComponentA.class), system.mFlagA);
        assertSame(engine.getMapper(Components.FlagComponentB.class), system.mFlagB);
        assertSame(engine.getMapper(Components.FlagComponentC.class), system.mFlagC);
    }
}
