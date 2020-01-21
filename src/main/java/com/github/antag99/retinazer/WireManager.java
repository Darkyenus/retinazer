package com.github.antag99.retinazer;

import com.badlogic.gdx.utils.ObjectMap;

final class WireManager {
    private final WireResolver[] wireResolvers;
    private final ObjectMap<Class<?>, WireCache> wireCaches = new ObjectMap<>();

    public WireManager(WireResolver[] wireResolvers) {
        this.wireResolvers = wireResolvers;
    }

    private WireCache getCache(Class<?> type) {
        WireCache cache = wireCaches.get(type);
        if (cache == null) {
            wireCaches.put(type, cache = new WireCache(type, wireResolvers));
        }
        return cache;
    }

    public void wire(Object object) {
        if (object == null) {
            throw new NullPointerException("object must not be null");
        }

        getCache(object.getClass()).wire(object);
    }

}
