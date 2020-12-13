package com.darkyen.retinazer.systems;

import com.darkyen.retinazer.EntitySystem;
import com.darkyen.retinazer.Family;
import com.darkyen.retinazer.util.Mask;
import org.jetbrains.annotations.NotNull;

/** An {@link EntitySystem} which processes a {@link Family} of entities. */
public abstract class EntityProcessorSystem extends EntitySystem {

    public EntityProcessorSystem(@NotNull Family family) {
        super(family);
    }

    @Override
    public void update() {
        final Mask mask = getEntities().getMask();
        for (int entity = mask.nextSetBit(0); entity != -1; entity = mask.nextSetBit(entity + 1)) {
            process(entity);
        }
    }

    /** Process single entity in the family. */
    protected abstract void process(int entity);
}
