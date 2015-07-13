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

import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

import com.github.antag99.retinazer.utils.Bag;
import com.github.antag99.retinazer.utils.Inject;

final class FamilyManager extends EntitySystem {
    private EntityListener[] entityListeners = new EntityListener[0];
    private Bag<BitSet> listenersForFamily = new Bag<>();
    private Map<FamilyConfig, Integer> familyIndexes = new HashMap<>();
    private Bag<Family> families = new Bag<>();

    private Bag<BitSet> entitiesForFamily = new Bag<>();
    private Bag<Iterable<Entity>> iterableForFamily = new Bag<>();

    private @Inject EntityManager entityManager;
    private @Inject ComponentManager componentManager;

    public FamilyManager(EngineConfig config) {
    }

    public Iterable<Entity> getEntities() {
        return getEntitiesFor(Family.EMPTY);
    }

    public Iterable<Entity> getEntitiesFor(FamilyConfig family) {
        Iterable<Entity> entities = iterableForFamily.get(getFamily(family).index);
        return entities;
    }

    public Family getFamily(FamilyConfig config) {
        int index = familyIndexes.getOrDefault(config, familyIndexes.size());
        if (index == familyIndexes.size()) {
            BitSet components = new BitSet();
            BitSet excludedComponents = new BitSet();

            config.getComponents().stream().map(componentManager::getIndex).forEach(components::set);
            config.getExcludedComponents().stream().map(componentManager::getIndex).forEach(excludedComponents::set);

            familyIndexes.put(config.clone(), index);
            families.set(index, new Family(components, excludedComponents, index));
            entitiesForFamily.set(index, new BitSet());
            iterableForFamily.set(index, () -> new EntityIterator(entityManager, entitiesForFamily.get(index)));
            listenersForFamily.set(index, new BitSet());

            entityManager.getEntities().forEach((it) -> updateFamilyMembership(it, false));
        }

        return families.get(index);
    }

    public void addEntityListener(EntityListener listener) {
        addEntityListener(Family.EMPTY, listener);
    }

    public void addEntityListener(FamilyConfig family, EntityListener listener) {
        int index = -1;
        for (int i = 0, n = entityListeners.length; i < n; ++i)
            if (entityListeners[i] == listener)
                index = i;

        if (index == -1) {
            index = entityListeners.length;
            entityListeners = Arrays.copyOf(entityListeners, index + 1);
            entityListeners[index] = listener;
        }

        listenersForFamily.get(getFamily(family).index).set(index);
    }

    public void removeEntityListener(EntityListener listener) {
        for (int index = 0; index < entityListeners.length; ++index) {
            if (entityListeners[index] == listener) {
                int lastIndex = entityListeners.length - 1;
                EntityListener last = entityListeners[lastIndex];
                entityListeners = Arrays.copyOf(entityListeners, lastIndex);
                entityListeners[index] = last;

                for (int i = 0, n = familyIndexes.size(); i < n; ++i) {
                    BitSet listeners = listenersForFamily.get(i);
                    if (listeners.get(lastIndex)) {
                        listeners.set(index);
                        listeners.clear(lastIndex);
                    } else {
                        listeners.clear(index);
                    }
                }
            }
        }
    }

    public void updateFamilyMembership(Entity entity, boolean remove) {
        // Find families that the entity was added to/removed from, and fill
        // the bit sets with corresponding listener bits.
        BitSet addListenerBits = new BitSet();
        BitSet removeListenerBits = new BitSet();

        for (int i = 0, n = this.familyIndexes.size(); i < n; ++i) {
            final BitSet listenersMask = this.listenersForFamily.get(i);
            final BitSet familyEntities = this.entitiesForFamily.get(i);

            boolean belongsToFamily = familyEntities.get(entity.getIndex());
            boolean matches = families.get(i).matches(entity) && !remove;

            if (belongsToFamily != matches) {
                if (matches) {
                    addListenerBits.or(listenersMask);
                    familyEntities.set(entity.getIndex());
                } else {
                    removeListenerBits.or(listenersMask);
                    familyEntities.clear(entity.getIndex());
                }
            }
        }

        // Store the current listeners in a local variable, so they
        // can't be changed (the backing array is copied before modification)
        EntityListener[] items = this.entityListeners;

        for (int i = removeListenerBits.nextSetBit(0); i != -1; i = removeListenerBits.nextSetBit(i + 1)) {
            items[i].entityRemoved(entity);
        }

        for (int i = addListenerBits.nextSetBit(0); i != -1; i = addListenerBits.nextSetBit(i + 1)) {
            items[i].entityAdded(entity);
        }
    }

    public void reset() {
        entityListeners = new EntityListener[0];
        listenersForFamily.clear();
        familyIndexes.clear();
        families.clear();
        entitiesForFamily.clear();
        iterableForFamily.clear();
    }
}
