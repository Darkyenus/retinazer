package com.github.antag99.retinazer;

import com.github.antag99.retinazer.util.Mask;

public final class Family {
    final Mask requiredComponents;
    final Mask excludedComponents;
    private final int index;

    final EntitySet entities = new EntitySet();
    private EntityListener[] listeners = EntityListener.EMPTY_ARRAY;

    final Mask removeEntities = new Mask();
    final Mask insertEntities = new Mask();

    Family(Mask requiredComponents, Mask excludedComponents, int index) {
        this.requiredComponents = requiredComponents;
        this.excludedComponents = excludedComponents;
        this.index = index;
    }

    /**
     * Adds a listener to this entity set.
     *
     * @param listener The listener to add.
     */
    public void addListener(EntityListener listener) {
        final EntityListener[] listeners = this.listeners;
        int n = listeners.length;
        for (int i = 0; i < n; i++) {
            if (listeners[i] == listener) {
                // Move to front
                System.arraycopy(listeners, 0, listeners, 1, i);
                listeners[0] = listener;
                return;
            }
        }
        EntityListener[] newListeners = new EntityListener[n + 1];
        System.arraycopy(listeners, 0, newListeners, 1, n);
        newListeners[0] = listener;
        this.listeners = newListeners;
    }

    /**
     * Removes a listener from this entity set.
     *
     * @param listener The listener to remove.
     */
    public void removeListener(EntityListener listener) {
        for (int i = 0, n = listeners.length; i < n; i++) {
            if (listeners[i] == listener) {
                EntityListener[] newListeners = new EntityListener[listeners.length - 1];
                System.arraycopy(listeners, 0, newListeners, 0, i);
                System.arraycopy(listeners, i + 1, newListeners, i, listeners.length - i - 1);
                this.listeners = newListeners;
                return;
            }
        }
    }

    void onInserted(EntitySetView inserted) {
        for (EntityListener listener : listeners) {
            listener.inserted(inserted);
        }
    }

    void onRemoved(EntitySetView removed) {
        for (EntityListener listener : listeners) {
            listener.removed(removed);
        }
    }

    public EntitySetView getEntities() {
        return entities;
    }

    @Override
    public int hashCode() {
        return index;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }
}
