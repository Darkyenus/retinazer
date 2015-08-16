package com.github.antag99.retinazer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

final class WireManager {
    private Engine engine;
    private WireResolver[] wireResolvers;
    private Map<Class<?>, WireCache> wireCaches;

    public WireManager(Engine engine) {
        this.engine = engine;
        wireResolvers = ((Collection<WireResolver>) engine
                .getConfig().getWireResolvers()).toArray(new WireResolver[0]);
        wireCaches = new HashMap<>();
    }

    private WireCache getCache(Class<?> type) {
        WireCache cache = wireCaches.get(type);
        if (cache == null) {
            wireCaches.put(type, cache =
                    new WireCache(engine, type, wireResolvers));
        }
        return cache;
    }

    public void wire(Object object) {
        if (object == null) {
            throw new NullPointerException("object must not be null");
        }

        getCache(object.getClass()).wire(object);
    }

    public void unwire(Object object) {
        if (object == null) {
            throw new NullPointerException("object must not be null");
        }

        getCache(object.getClass()).unwire(object);
    }
}
