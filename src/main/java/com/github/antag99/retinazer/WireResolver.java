package com.github.antag99.retinazer;

import java.lang.reflect.Field;

/**
 * WireResolver is used for wiring/un-wiring fields marked with {@link Wire}.
 * Multiple resolvers can be registered to an {@link EngineConfig}.
 *
 * @see Wire
 */
public interface WireResolver {

    /**
     * Wires the field of the given object.
     *
     * @param engine The engine instance.
     * @param object The object to wire.
     * @param field The field of the object.
     * @return Whether this resolver handled the given field.
     */
    boolean wire(Engine engine, Object object, Field field) throws IllegalAccessException;

}
