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

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class EngineTest {
    private Array<EntitySystem> initializedSystems = new Array<>();
    private Array<EntitySystem> updatedSystems = new Array<>();

    public abstract class OrderSystem extends EntitySystem {
        @Override
        protected final void initialize() {
            initializedSystems.add(this);
        }

        @Override
        protected final void update(float delta) {
            updatedSystems.add(this);
        }
    }

    @Test
    public void testEngine() {
        Engine engine = new Engine(new EngineConfig());
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
        EntitySystem entitySystemA = new OrderSystem() {
            @Override
            public String toString() {
                return "A";
            }
        };

        EntitySystem entitySystemB = new OrderSystem() {
            @Override
            public String toString() {
                return "B";
            }
        };

        EntitySystem entitySystemC = new OrderSystem() {
            @Override
            public String toString() {
                return "C";
            }
        };

        EntitySystem entitySystemD = new OrderSystem() {
            @Override
            public String toString() {
                return "D";
            }
        };

        EntitySystem entitySystemE = new OrderSystem() {
            @Override
            public String toString() {
                return "E";
            }
        };

        Engine engine = new Engine(new EngineConfig()
                .addSystem(entitySystemA, Order.DEFAULT)
                .addSystem(entitySystemB, Order.DEFAULT)
                .addSystem(entitySystemC, Order.EARLY)
                .addSystem(entitySystemD, Order.LATEST)
                .addSystem(entitySystemE, Order.DEFAULT));
        assertEquals(Array.with(entitySystemC,
                entitySystemA,
                entitySystemB,
                entitySystemE,
                entitySystemD), initializedSystems);
        initializedSystems.clear();
        engine.update(0f);
        assertEquals(Array.with(entitySystemC,
                entitySystemA,
                entitySystemB,
                entitySystemE,
                entitySystemD), updatedSystems);
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
        Engine engine = new Engine(new EngineConfig());
        Mapper<FlagComponentA> mFlagA = engine.getMapper(FlagComponentA.class);
        Mapper<FlagComponentB> mFlagB = engine.getMapper(FlagComponentB.class);
        Mapper<FlagComponentC> mFlagC = engine.getMapper(FlagComponentC.class);

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
                asSet(engine.getFamily(Family.with()).getEntities().getIndices()));

        assertEquals(
                asSet(entity0, entity1, entity2, entity3, entity4, entity5, entity6, entity7),
                asSet(engine.getFamily(Family.exclude()).getEntities().getIndices()));

        assertEquals(
                asSet(entity0, entity1, entity2, entity3, entity4, entity5, entity6, entity7),
                asSet(engine.getFamily(Family.with().exclude()).getEntities().getIndices()));

        assertEquals(
                asSet(entity1, entity4, entity6, entity7),
                asSet(engine.getFamily(Family.with(FlagComponentA.class)).getEntities().getIndices()));

        assertEquals(
                asSet(entity2, entity4, entity5, entity7),
                asSet(engine.getFamily(Family.with(FlagComponentB.class)).getEntities().getIndices()));

        assertEquals(
                asSet(entity3, entity5, entity6, entity7),
                asSet(engine.getFamily(Family.with(FlagComponentC.class)).getEntities().getIndices()));

        assertEquals(
                asSet(entity4, entity7),
                asSet(engine.getFamily(Family.with(FlagComponentA.class, FlagComponentB.class)).getEntities().getIndices()));

        assertEquals(
                asSet(entity6, entity7),
                asSet(engine.getFamily(Family.with(FlagComponentA.class, FlagComponentC.class)).getEntities().getIndices()));

        assertEquals(
                asSet(entity5, entity7),
                asSet(engine.getFamily(Family.with(FlagComponentB.class, FlagComponentC.class)).getEntities().getIndices()));

        assertEquals(
                asSet(entity7),
                asSet(engine.getFamily(Family.with(FlagComponentA.class, FlagComponentB.class, FlagComponentC.class)).getEntities().getIndices()));

        assertEquals(
                asSet(entity0, entity2, entity3, entity5),
                asSet(engine.getFamily(Family.exclude(FlagComponentA.class)).getEntities().getIndices()));

        assertEquals(
                asSet(entity0, entity1, entity3, entity6),
                asSet(engine.getFamily(Family.exclude(FlagComponentB.class)).getEntities().getIndices()));

        assertEquals(
                asSet(entity0, entity1, entity2, entity4),
                asSet(engine.getFamily(Family.exclude(FlagComponentC.class)).getEntities().getIndices()));

        assertEquals(
                asSet(entity0, entity3),
                asSet(engine.getFamily(Family.exclude(FlagComponentA.class, FlagComponentB.class)).getEntities().getIndices()));

        assertEquals(
                asSet(entity0, entity2),
                asSet(engine.getFamily(Family.exclude(FlagComponentA.class, FlagComponentC.class)).getEntities().getIndices()));

        assertEquals(
                asSet(entity0, entity1),
                asSet(engine.getFamily(Family.exclude(FlagComponentB.class, FlagComponentC.class)).getEntities().getIndices()));

        assertEquals(
                asSet(entity0),
                asSet(engine.getFamily(
                        Family.exclude(FlagComponentA.class, FlagComponentB.class, FlagComponentC.class)).getEntities().getIndices()));

        assertEquals(
                asSet(entity1, entity6),
                asSet(engine.getFamily(Family.with(FlagComponentA.class).exclude(FlagComponentB.class)).getEntities().getIndices()));

        assertEquals(
                asSet(entity1, entity4),
                asSet(engine.getFamily(Family.with(FlagComponentA.class).exclude(FlagComponentC.class)).getEntities().getIndices()));

        assertEquals(
                asSet(entity2, entity5),
                asSet(engine.getFamily(Family.with(FlagComponentB.class).exclude(FlagComponentA.class)).getEntities().getIndices()));

        assertEquals(
                asSet(entity2, entity4),
                asSet(engine.getFamily(Family.with(FlagComponentB.class).exclude(FlagComponentC.class)).getEntities().getIndices()));

        assertEquals(
                asSet(entity3, entity5),
                asSet(engine.getFamily(Family.with(FlagComponentC.class).exclude(FlagComponentA.class)).getEntities().getIndices()));

        assertEquals(
                asSet(entity3, entity6),
                asSet(engine.getFamily(Family.with(FlagComponentC.class).exclude(FlagComponentB.class)).getEntities().getIndices()));

        assertEquals(
                asSet(entity1),
                asSet(engine.getFamily(
                        Family.with(FlagComponentA.class).exclude(FlagComponentB.class, FlagComponentC.class)).getEntities().getIndices()));

        assertEquals(
                asSet(entity2),
                asSet(engine.getFamily(
                        Family.with(FlagComponentB.class).exclude(FlagComponentA.class, FlagComponentC.class)).getEntities().getIndices()));

        assertEquals(
                asSet(entity3),
                asSet(engine.getFamily(
                        Family.with(FlagComponentC.class).exclude(FlagComponentA.class, FlagComponentB.class)).getEntities().getIndices()));
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
        final Engine engine = new Engine(new EngineConfig());
        assertThrows(RuntimeException.class, () -> engine.wire(consumer));
    }

    public static class ExampleSystem extends EntitySystem {
        public @Wire Engine engine;
        public @Wire FlagSystemA flagSystemA;
        public @Wire FlagSystemB flagSystemB;
        public @Wire FlagSystemC flagSystemC;
        public @Wire Mapper<FlagComponentA> mFlagA;
        public @Wire Mapper<FlagComponentB> mFlagB;
        public @Wire Mapper<FlagComponentC> mFlagC;
    }

    @Test
    public void testEngineDependencyInjection() {
        ExampleSystem system = new ExampleSystem();
        FlagSystemA flagSystemA = new FlagSystemA();
        FlagSystemB flagSystemB = new FlagSystemB();
        FlagSystemC flagSystemC = new FlagSystemC();
        Engine engine = new Engine(new EngineConfig()
                .addSystem(system)
                .addSystem(flagSystemA)
                .addSystem(flagSystemB)
                .addSystem(flagSystemC));
        assertSame(engine, system.engine);
        assertSame(flagSystemA, system.flagSystemA);
        assertSame(flagSystemB, system.flagSystemB);
        assertSame(flagSystemC, system.flagSystemC);
        assertSame(engine.getMapper(FlagComponentA.class), system.mFlagA);
        assertSame(engine.getMapper(FlagComponentB.class), system.mFlagB);
        assertSame(engine.getMapper(FlagComponentC.class), system.mFlagC);
    }
}
