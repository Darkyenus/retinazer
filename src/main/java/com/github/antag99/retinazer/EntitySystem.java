package com.github.antag99.retinazer;

/** Base class for system implementations. */
public abstract class EntitySystem implements EngineService {

    static final EntitySystem[] EMPTY_ARRAY = new EntitySystem[0];

    /** Engine instance this entity system is added to, for convenience. */
    @Wire
    protected Engine engine;

    /**
     * Updates this system.
     * @param delta time in seconds since last update
     */
    protected void update(float delta) {
    }
}
