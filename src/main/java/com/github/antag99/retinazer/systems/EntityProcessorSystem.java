package com.github.antag99.retinazer.systems;

import com.github.antag99.retinazer.EntitySetView;
import com.github.antag99.retinazer.EntitySystem;
import com.github.antag99.retinazer.Family;
import com.github.antag99.retinazer.util.Mask;

/** An {@link EntitySystem} which processes a {@link Family} of entities. */
public abstract class EntityProcessorSystem extends EntitySystem {

    public EntityProcessorSystem(Family family) {
        super(family);
    }

    @Override
    public void update(float delta) {
        final Mask mask = getEntities().getMask();
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
