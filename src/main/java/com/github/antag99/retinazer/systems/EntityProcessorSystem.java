package com.github.antag99.retinazer.systems;

import com.badlogic.gdx.utils.IntArray;
import com.github.antag99.retinazer.EntitySetView;
import com.github.antag99.retinazer.EntitySystem;
import com.github.antag99.retinazer.FamilySpec;

public abstract class EntityProcessorSystem extends EntitySystem {
    private final FamilySpec family;
    private EntitySetView entities;

    public EntityProcessorSystem(FamilySpec family) {
        this.family = family;
    }

    @Override
    public void setup() {
        super.setup();

        entities = engine.getEntities(getFamily());
    }

    public final EntitySetView getEntities() {
        return entities;
    }

    public final FamilySpec getFamily() {
        return family;
    }

    @Override
    public void update(float delta) {
        IntArray indices = getEntities().getIndices();
        int[] items = indices.items;
        for (int i = 0, n = indices.size; i < n; i++) {
            process(items[i], delta);
        }
    }

    /**
     * Process single entity in the family
     * @param delta time in seconds since last update
     */
    protected abstract void process(int entity, float delta);
}
