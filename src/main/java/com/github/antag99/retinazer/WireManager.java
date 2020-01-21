package com.github.antag99.retinazer;

import com.badlogic.gdx.utils.ObjectMap;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

final class WireManager {
    private final WireResolver[] wireResolvers;
    private final ObjectMap<Class<?>, WireCache> wireCaches = new ObjectMap<>();

    WireManager(List<WireResolver> wireResolvers) {
        this.wireResolvers = wireResolvers.toArray(new WireResolver[0]);
    }

    /** Wire up given object. */
    void wire(Object object) {
        if (object == null) {
            throw new NullPointerException("object must not be null");
        }

        final Class<?> type = object.getClass();
        WireCache cache = wireCaches.get(type);
        if (cache == null) {
            wireCaches.put(type, cache = new WireCache(type));
        }

        cache.wire(object, wireResolvers);
    }

    /** Remove all wiring cache to free up memory. */
    void flushCache() {
        wireCaches.clear();
    }

    private static final class WireCache {
        private static final Field[] NO_FIELDS = new Field[0];

        private final Field[] fields;

        WireCache(Class<?> type) {
            final List<Field> fields = new ArrayList<>();

            for (Class<?> current = type; current != Object.class; current = current.getSuperclass()){
                for (Field field : current.getDeclaredFields()) {
                    if (field.isSynthetic() || field.getDeclaredAnnotation(Wire.class) == null) {
                        continue;
                    }

                    final int modifiers = field.getModifiers();
                    if (Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers)){
                        throw new IllegalArgumentException("Neither static nor final fields can be wired ("+current.getName()+"#"+field.getName()+")");
                    }

                    field.setAccessible(true);
                    fields.add(field);
                }
            }

            this.fields = fields.toArray(NO_FIELDS);
        }

        public void wire(Object object, WireResolver[] wireResolvers) {
            fields:
            for (final Field field : fields) {
                for (WireResolver wireResolver : wireResolvers) {
                    try {
                        if (wireResolver.wire(object, field)) {
                            continue fields;
                        }
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException("Failed to wire field " +
                                field.getName() + " of " +
                                field.getDeclaringClass().getName() + " of type "+field.getType().getName(), e);
                    }
                }

                throw new RuntimeException("Failed to wire field " +
                        field.getName() + " of " +
                        field.getDeclaringClass().getName() + " of type "+field.getType().getName()+" - no resolver");
            }
        }
    }
}
