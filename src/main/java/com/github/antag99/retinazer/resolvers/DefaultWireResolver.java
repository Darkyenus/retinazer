package com.github.antag99.retinazer.resolvers;

import com.github.antag99.retinazer.Engine;
import com.github.antag99.retinazer.EntitySystem;
import com.github.antag99.retinazer.WireResolver;

import java.lang.reflect.Field;

/** Wires Engine and EntitySystems registered in the engine. */
public final class DefaultWireResolver implements WireResolver {
    @Override
    @SuppressWarnings("unchecked")
    public boolean wire(Engine engine, Object object, Field field) throws IllegalAccessException {
        Class<?> type = field.getType();
        if (type == Engine.class) {
            field.set(object, engine);
        } else if (EntitySystem.class.isAssignableFrom(type)) {
            field.set(object, engine.getSystem((Class<? extends EntitySystem>) type));
        } else {
            return false;
        }
        return true;
    }

}
