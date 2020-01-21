package com.github.antag99.retinazer.systems;

import com.github.antag99.retinazer.EntitySetView;
import com.github.antag99.retinazer.EntitySystem;
import com.github.antag99.retinazer.Family;
import com.github.antag99.retinazer.util.Mask;

public abstract class EntityProcessorSystem extends EntitySystem {
    private final Family family;
    private EntitySetView entities;

    public EntityProcessorSystem(Family family) {
        this.family = family;
    }

    @Override
    public void initialize() {
        super.initialize();
        entities = engine.getEntities(getFamily());
    }

    public final EntitySetView getEntities() {
        return entities;
    }

    public final Family getFamily() {
        return family;
    }

    @Override
    public void update(float delta) {
        final Mask mask = entities.getMask();
        for (int entity = mask.nextSetBit(0); entity != -1; entity = mask.nextSetBit(entity + 1)) {
            process(entity, delta);
        }
    }

    /**
     * Process single entity in the family
     * @param delta time in seconds since last update
     */
    protected abstract void process(int entity, float delta);
}
