package com.github.antag99.retinazer;

import com.badlogic.gdx.utils.ObjectIntMap;
import com.github.antag99.retinazer.util.Bag;
import com.github.antag99.retinazer.util.Mask;

final class FamilyManager {

    private static final class Key {
        Mask requiredComponents = null;
        Mask excludedComponents = null;

        @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
        @Override
        public boolean equals(Object obj) {
            if (obj == null)
                return false;
            // No need for a type check; this class is only used internally
            Key key = (Key) obj;
            return key.excludedComponents.equals(excludedComponents) &&
                    key.requiredComponents.equals(requiredComponents);
        }

        @Override
        public int hashCode() {
            // Excluded components are rarer than required components; prioritize
            // the components hashCode over excluded components hashCode.
            return 31 * excludedComponents.hashCode() + requiredComponents.hashCode();
        }
    }

    private final ObjectIntMap<Key> familyIndices = new ObjectIntMap<>();
    private final Bag<Family> families = new Bag<>();
    private final Engine engine;

    FamilyManager(Engine engine) {
        this.engine = engine;
    }

    private final Key getFamily_lookup = new Key();

    /** @return existing or new family conforming to the given configuration */
    public Family getFamily(FamilySpec spec) {
        assert spec.domain.isSubsetOf(engine.componentDomain);
        final ObjectIntMap<Key> familyIndices = this.familyIndices;
        final int index;
        {
            final Key lookup = this.getFamily_lookup;
            lookup.requiredComponents = spec.requiredComponents;
            lookup.excludedComponents = spec.excludedComponents;
            index = familyIndices.get(lookup, familyIndices.size);
        }

        if (index == familyIndices.size) {
            final Family family = new Family(spec.requiredComponents, spec.excludedComponents, index);
            Key key = new Key();
            key.requiredComponents = spec.requiredComponents;
            key.excludedComponents = spec.excludedComponents;
            familyIndices.put(key, index);
            families.set(index, family);

            // Find matching entities, and add them to the new family set.
            final Mapper<?>[] mappers = engine.componentMappers;
            Mask matchedEntities = new Mask().set(engine.entities);
            for (int componentI = spec.requiredComponents.nextSetBit(0); componentI != -1; componentI = spec.requiredComponents.nextSetBit(componentI + 1)) {
                matchedEntities.and(mappers[componentI].componentsMask);
            }

            for (int componentI = spec.excludedComponents.nextSetBit(0); componentI != -1; componentI = spec.excludedComponents.nextSetBit(componentI + 1)) {
                matchedEntities.andNot(mappers[componentI].componentsMask);
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
        final Engine engine = this.engine;
        final Bag<Family> families = this.families;
        final int familyCount = familyIndices.size;

        {
            final Mapper<?>[] mappers = engine.componentMappers;
            final Mask tmpMask = this.updateFamilyMembership_tmpMask;
            final Mask matchedEntities = this.updateFamilyMembership_tmpMatchedEntities;
            for (int i = 0; i < familyCount; i++) {
                Family family = families.get(i);
                assert family != null;
                EntitySet entities = family.entities;

                matchedEntities.set(engine.entities);
                matchedEntities.andNot(engine.remove);

                for (int componentI = family.requiredComponents.nextSetBit(0); componentI != -1; componentI = family.requiredComponents.nextSetBit(componentI + 1)) {
                    final Mapper<?> mapper = mappers[componentI];
                    tmpMask.set(mapper.componentsMask);
                    tmpMask.andNot(mapper.removeMask);
                    matchedEntities.and(tmpMask);
                }

                for (int componentI = family.excludedComponents.nextSetBit(0); componentI != -1; componentI = family.excludedComponents.nextSetBit(componentI + 1)) {
                    final Mapper<?> mapper = mappers[componentI];
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
        }

        {
            final EntitySet argument = this.updateFamilyMembership_argument;
            for (int i = 0; i < familyCount; i++) {
                Family family = families.get(i);
                assert family != null;

                if (!family.insertEntities.isEmpty()) {
                    argument.addEntities(family.insertEntities);
                    family.onInserted(argument);
                    argument.clear();
                }

                if (!family.removeEntities.isEmpty()) {
                    argument.addEntities(family.removeEntities);
                    family.onRemoved(argument);
                    argument.clear();
                }
            }
        }
    }
}
