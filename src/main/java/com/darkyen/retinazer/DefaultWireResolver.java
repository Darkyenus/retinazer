package com.darkyen.retinazer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/** Wires {@link Engine}, {@link EngineService}s and {@link Mapper}s (by the generic parameter)
 *  registered in the engine. */
final class DefaultWireResolver implements WireResolver {

    private final Engine engine;

    DefaultWireResolver(@NotNull Engine engine) {
        this.engine = engine;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean wire(@NotNull Object object, @NotNull Field field) throws IllegalAccessException {
        Class<?> type = field.getType();
        if (type == Engine.class) {
            field.set(object, engine);
        } else if (type == Mapper.class) {
            final Class<? extends Component> componentType = getTypeArgument(field.getGenericType(), 0);
            if (componentType == null) {
                return false;
            }
            field.set(object, engine.getMapper(componentType));
        } else if (EngineService.class.isAssignableFrom(type)) {
            field.set(object, engine.getService((Class<? extends EngineService>) type));
        } else if (List.class.isAssignableFrom(type)) {
            final Class<? extends Component> componentType = getTypeArgument(field.getGenericType(), 0);
            if (componentType == null) {
                return false;
            }
            field.set(object, engine.getServices(componentType));
        } else {
            return false;
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    private static <T> Class<? extends T> getTypeArgument(@NotNull Type genericType, @SuppressWarnings("SameParameterValue") int index) {
        if (genericType instanceof ParameterizedType) {
            Type[] actualTypes = ((ParameterizedType)genericType).getActualTypeArguments();
            if (index < actualTypes.length) {
                Type actualType = actualTypes[index];
                if (actualType instanceof Class)
                    return (Class<T>)actualType;
                else if (actualType instanceof ParameterizedType)
                    return (Class<T>)((ParameterizedType)actualType).getRawType();
                else if (actualType instanceof GenericArrayType) {
                    Type componentType = ((GenericArrayType)actualType).getGenericComponentType();
                    if (componentType instanceof Class) return (Class<T>) Array.newInstance((Class<?>)componentType, 0).getClass();
                }
            }
        }
        return null;
    }

}
