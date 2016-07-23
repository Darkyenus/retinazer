package com.github.antag99.retinazer.resolvers;

import com.badlogic.gdx.utils.reflect.Field;
import com.github.antag99.retinazer.Engine;
import com.github.antag99.retinazer.WireResolver;

import java.util.Objects;

/**
 * Wires single value to appropriate wired variables based on polymorphism ("where it fits").
 * Does not assign to final variables.
 */
public final class SimpleWireResolver implements WireResolver {

    private final Object value;

    public SimpleWireResolver(Object value) {
        Objects.requireNonNull(value, "value must not be null");
        this.value = value;
    }

    @Override
    public boolean wire(Engine engine, Object object, Field field) throws Throwable {
        if (field.getType().isInstance(value)) {
            if (field.isFinal()) return false;
            field.set(object, value);
            return true;
        } else {
            return false;
        }
    }
}
