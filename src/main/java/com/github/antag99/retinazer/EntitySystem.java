package com.github.antag99.retinazer;

/** Base class for system implementations. */
public abstract class EntitySystem implements EngineService {

    /** Engine instance this entity system is added to, for convenience. */
    @Wire
    protected Engine engine;

}
