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

import com.badlogic.gdx.utils.ObjectIntMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.github.antag99.retinazer.util.Bag;
import com.github.antag99.retinazer.util.Mask;

final class FamilyManager {

    private static final class Key {
        ObjectSet<Class<? extends Component>> components = null;
        ObjectSet<Class<? extends Component>> excludedComponents = null;

        @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
        @Override
        public boolean equals(Object obj) {
            if (obj == null)
                return false;
            // No need for a type check; this class is only used internally
            Key key = (Key) obj;
            return key.excludedComponents.equals(excludedComponents) &&
                    key.components.equals(components);
        }

        @Override
        public int hashCode() {
            // Excluded components are rarer than required components; prioritize
            // the components hashCode over excluded components hashCode.
            return 31 * excludedComponents.hashCode() + components.hashCode();
        }
    }

    private final ObjectIntMap<Key> familyIndices = new ObjectIntMap<>();
    private final Bag<Family> families = new Bag<>();
    private final Engine engine;
    private EntitySetView entities;

    public FamilyManager(Engine engine) {
        this.engine = engine;
    }

    public EntitySetView getEntities() {
        if (entities == null) {
            entities = getFamily(new FamilyConfig()).getEntities();
        }
        return entities;
    }

    private final Key getFamily_lookup = new Key();

    /**
     *  @return existing or new family conforming to given configuration
     */
    public Family getFamily(FamilyConfig config) {
        final Key lookup = this.getFamily_lookup;
        lookup.components = config.components;
        lookup.excludedComponents = config.excludedComponents;
        final int index = familyIndices.get(lookup, familyIndices.size);

        if (index == familyIndices.size) {
            int i;
            int[] components = new int[config.components.size];
            int[] excludedComponents = new int[config.excludedComponents.size];

            i = 0;
            for (Class<? extends Component> componentType : config.components)
                components[i++] = engine.componentManager.getIndex(componentType);

            i = 0;
            for (Class<? extends Component> componentType : config.excludedComponents)
                excludedComponents[i++] = engine.componentManager.getIndex(componentType);

            Family family = new Family(engine, components, excludedComponents, index);
            Key key = new Key();
            key.components = config.components;
            key.excludedComponents = config.excludedComponents;
            familyIndices.put(key, index);
            families.set(index, family);

            // Find matching entities, and add them to the new family set.
            Mapper<?>[] mappers = engine.componentManager.array;
            Mask matchedEntities = new Mask().set(engine.entities);

            for (int component : components) {
                matchedEntities.and(mappers[component].componentsMask);
            }

            for (int excludedComponent : excludedComponents) {
                matchedEntities.andNot(mappers[excludedComponent].componentsMask);
            }

            // No notifications to dispatch here
            family.entities.addEntities(matchedEntities);
        }

        return families.get(index);
    }

    private final Mask updateFamilyMembership_tmpMask = new Mask();
    private final Mask updateFamilyMembership_tmpMatchedEntities = new Mask();
    private final EntitySet updateFamilyMembership_argument = new EntitySet();

    /**
     * Updates family membership for all entities. This will insert/remove entities
     * to/from family sets.
     */
    void updateFamilyMembership() {
        Mapper<?>[] mappers = engine.componentManager.array;

        final Mask tmpMask = this.updateFamilyMembership_tmpMask;
        @SuppressWarnings("UnnecessaryLocalVariable")
        final Mask tmpMatchedEntities = this.updateFamilyMembership_tmpMatchedEntities;

        for (int i = 0, n = familyIndices.size; i < n; i++) {
            Family family = families.get(i);
            assert family != null;
            EntitySet entities = family.entities;

            Mask matchedEntities = tmpMatchedEntities.set(engine.entities);
            matchedEntities.andNot(engine.remove);

            int[] components = family.components;
            for (int component : components) {
                Mapper<?> mapper = mappers[component];
                tmpMask.set(mapper.componentsMask);
                tmpMask.andNot(mapper.removeMask);
                matchedEntities.and(tmpMask);
            }

            int[] excludedComponents = family.excludedComponents;
            for (int excludedComponent : excludedComponents) {
                Mapper<?> mapper = mappers[excludedComponent];
                tmpMask.set(mapper.componentsMask);
                tmpMask.andNot(mapper.removeMask);
                matchedEntities.andNot(tmpMask);
            }

            family.insertEntities.set(matchedEntities);
            family.insertEntities.andNot(entities.getMask());
            entities.addEntities(family.insertEntities);

            family.removeEntities.set(entities.getMask());
            family.removeEntities.andNot(matchedEntities);
            entities.removeEntities(family.removeEntities);
        }

        final EntitySet argument = this.updateFamilyMembership_argument;

        for (int i = 0, n = familyIndices.size; i < n; i++) {
            Family family = families.get(i);
            assert family != null;

            if (!family.insertEntities.isEmpty()) {
                argument.addEntities(family.insertEntities);
                for (EntityListener listener : family.listeners) {
                    listener.inserted(argument.view());
                }
                argument.clear();
            }

            if (!family.removeEntities.isEmpty()) {
                argument.addEntities(family.removeEntities);
                for (EntityListener listener : family.listeners) {
                    listener.removed(argument.view());
                }
                argument.clear();
            }
        }
    }
}
