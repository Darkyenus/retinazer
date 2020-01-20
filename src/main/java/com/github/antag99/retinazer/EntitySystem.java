package com.github.antag99.retinazer;

/**
 * Base class for system implementations.
 */
public abstract class EntitySystem {

    /**
     * Engine instance this entity system is added to, for convenience.
     */
    @Wire
    protected Engine engine;

    /**
     * Framework-side initialization method. End users should not override
     * this method. Always call {@code super.setup()} when overriding this.
     */
    protected void setup() {
    }

    /**
     * Initializes this system. If you override this method, mark it {@code final}.
     */
    protected void initialize() {
    }

    /**
     * Updates this system. If you override this method, mark it {@code final}.
     * @param delta time in seconds since last update
     */
    protected void update(float delta) {
    }
}
