package com.darkyen.retinazer.resolvers;

import com.darkyen.retinazer.WireResolver;
import org.jetbrains.annotations.NotNull;

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
    public boolean wire(@NotNull Object object, @NotNull Field field) throws IllegalAccessException {
        if (field.getType().isInstance(value)) {
            field.set(object, value);
            return true;
        } else {
            return false;
        }
    }
}
