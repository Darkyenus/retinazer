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
    private final Bag<FamilyHolder> families = new Bag<>();
    private final Engine engine;

    FamilyManager(Engine engine) {
        this.engine = engine;
    }

    private final Key getFamily_lookup = new Key();

    /** @return {@link EntitySet} conforming to the given configuration backed by the family */
    public EntitySet getFamily(FamilySpec spec) {
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
            final FamilyHolder family = new FamilyHolder(spec.requiredComponents, spec.excludedComponents);
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

        final FamilyHolder familyHolder = families.get(index);
        assert familyHolder != null;
        return familyHolder.entities;
    }

    private final Mask updateFamilyMembership_tmpMask = new Mask();
    private final Mask updateFamilyMembership_tmpMatchedEntities = new Mask();

    /**
     * Updates family membership for all entities. This will insert/remove entities
     * to/from family sets.
     */
    void updateFamilyMembership() {
        final Engine engine = this.engine;
        final Bag<FamilyHolder> families = this.families;
        final int familyCount = familyIndices.size;

        final Mapper<?>[] mappers = engine.componentMappers;
        final Mask tmpMask = this.updateFamilyMembership_tmpMask;
        final Mask matchedEntities = this.updateFamilyMembership_tmpMatchedEntities;
        for (int i = 0; i < familyCount; i++) {
            FamilyHolder family = families.get(i);
            assert family != null;

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

            family.entities.setEntities(matchedEntities);
        }
    }

    private static final class FamilyHolder {
        final Mask requiredComponents;
        final Mask excludedComponents;
        final EntitySet entities = new EntitySet();

        FamilyHolder(Mask requiredComponents, Mask excludedComponents) {
            this.requiredComponents = requiredComponents;
            this.excludedComponents = excludedComponents;
        }
    }
}
