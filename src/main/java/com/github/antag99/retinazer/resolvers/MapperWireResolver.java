package com.github.antag99.retinazer.resolvers;

import com.github.antag99.retinazer.Component;
import com.github.antag99.retinazer.Engine;
import com.github.antag99.retinazer.Mapper;
import com.github.antag99.retinazer.WireResolver;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/** Wires {@link Mapper} instances by their generic parameter. */
public final class MapperWireResolver implements WireResolver {

    @SuppressWarnings("unchecked")
    private static <T> Class<? extends T> getTypeArgument(Type genericType, @SuppressWarnings("SameParameterValue") int index) {
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

    private static Class<? extends Component> getType(Field field) {
        if (field.getType() != Mapper.class)
            return null;

        return getTypeArgument(field.getGenericType(), 0);
    }

    @Override
    public boolean wire(Engine engine, Object object, Field field) throws IllegalAccessException {
        Class<? extends Component> type = getType(field);
        if (type != null) {
            field.set(object, engine.getMapper(type));
            return true;
        }
        return false;
    }

}
