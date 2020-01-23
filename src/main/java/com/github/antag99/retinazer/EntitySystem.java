package com.github.antag99.retinazer;

/** Base class for system implementations. */
public abstract class EntitySystem implements EngineService {

    /** Engine instance this entity system is added to, for convenience. */
    @Wire
    protected Engine engine;

    private final Family family;
    private EntitySetView familyEntities;

    protected EntitySystem(Family family) {
        this.family = family;
    }

    public final Family getFamily() {
        return family;
    }

    public final EntitySetView getEntities() {
        return familyEntities;
    }

    @Override
    public void initialize() {
        familyEntities = engine.getEntities(family);
    }
}
