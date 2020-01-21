package com.github.antag99.retinazer.resolvers;

import com.github.antag99.retinazer.WireResolver;

import java.lang.reflect.Field;
import java.util.Objects;

/**
 * Wires single value to appropriate wired variables based on polymorphism ("where it fits").
 */
public final class SimpleWireResolver implements WireResolver {

    private final Object value;

    public SimpleWireResolver(Object value) {
        Objects.requireNonNull(value, "value must not be null");
        this.value = value;
    }

    @Override
    public boolean wire(Object object, Field field) throws IllegalAccessException {
        if (field.getType().isInstance(value)) {
            field.set(object, value);
            return true;
        } else {
            return false;
        }
    }
}
