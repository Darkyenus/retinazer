package com.github.antag99.retinazer;

import com.badlogic.gdx.utils.ObjectIntMap;
import com.github.antag99.retinazer.util.Bag;
import com.github.antag99.retinazer.util.Mask;

final class FamilyManager {

    private final ObjectIntMap<Family> familyIndices = new ObjectIntMap<>();
    private final Bag<FamilyHolder> families = new Bag<>();
    private final Engine engine;

    FamilyManager(Engine engine) {
        this.engine = engine;
    }

    private transient final Mask _matchedEntities = new Mask();

    /** @return {@link EntitySet} conforming to the given configuration backed by the family */
    public EntitySet getFamily(Family spec) {
        assert spec.domain.isSubsetOf(engine.componentDomain);
        final ObjectIntMap<Family> familyIndices = this.familyIndices;
        final int index;
        {
            index = familyIndices.get(spec, familyIndices.size);
        }

        if (index == familyIndices.size) {
            final FamilyHolder family = new FamilyHolder(spec.requiredComponents, spec.excludedComponents);
            familyIndices.put(spec, index);
            families.set(index, family);

            // Find matching entities, and add them to the new family set.
            final Mapper<?>[] mappers = engine.componentMappers;
            final Mask matchedEntities = this._matchedEntities;
            matchedEntities.set(engine.entities);
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

    /**
     * Updates family membership for all entities. This will insert/remove entities
     * to/from family sets.
     */
    void updateFamilyMembership() {
        final Engine engine = this.engine;
        final Bag<FamilyHolder> families = this.families;
        final int familyCount = familyIndices.size;

        final Mapper<?>[] mappers = engine.componentMappers;
        final Mask matchedEntities = this._matchedEntities;
        for (int i = 0; i < familyCount; i++) {
            FamilyHolder family = families.get(i);
            assert family != null;

            matchedEntities.set(engine.entities);

            for (int componentI = family.requiredComponents.nextSetBit(0); componentI != -1; componentI = family.requiredComponents.nextSetBit(componentI + 1)) {
                matchedEntities.and(mappers[componentI].componentsMask);
            }

            for (int componentI = family.excludedComponents.nextSetBit(0); componentI != -1; componentI = family.excludedComponents.nextSetBit(componentI + 1)) {
                matchedEntities.andNot(mappers[componentI].componentsMask);
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
