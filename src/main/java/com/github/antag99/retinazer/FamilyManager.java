package com.github.antag99.retinazer;

import com.badlogic.gdx.utils.ObjectIntMap;
import com.github.antag99.retinazer.util.Bag;
import com.github.antag99.retinazer.util.Mask;
import org.jetbrains.annotations.NotNull;

final class FamilyManager {

    private final ObjectIntMap<Family> familyIndices = new ObjectIntMap<>();
    private final Bag<FamilyHolder> families = new Bag<>();
    private final Engine engine;

    FamilyManager(@NotNull Engine engine) {
        this.engine = engine;
    }

    private transient final Mask _matchedEntities = new Mask();

    /** @return {@link EntitySet} conforming to the given configuration backed by the family */
    @NotNull
    public EntitySet getFamily(@NotNull Family spec) {
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

            // Faster than addEntities, because we skip the modification check which almost always fails anyway
            family.entities.getMaskForModification().set(matchedEntities);
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

            final Mask requiredComponents = family.requiredComponents;
            for (int componentI = requiredComponents.nextSetBit(0); componentI != -1; componentI = requiredComponents.nextSetBit(componentI + 1)) {
                matchedEntities.and(mappers[componentI].componentsMask);
            }

            final Mask excludedComponents = family.excludedComponents;
            for (int componentI = excludedComponents.nextSetBit(0); componentI != -1; componentI = excludedComponents.nextSetBit(componentI + 1)) {
                matchedEntities.andNot(mappers[componentI].componentsMask);
            }

            // It is likely that no modification happened here
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
