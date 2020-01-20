/*******************************************************************************
 * Copyright (C) 2015 Anton Gustafsson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package com.github.antag99.retinazer;

import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.Pool;
import com.github.antag99.retinazer.util.Bag;
import com.github.antag99.retinazer.util.Mask;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * {@code Mapper} is used for accessing the components of a specific type. This
 * offers better performance than looking up the type of a component on the fly.
 *
 * @param <T> the component type.
 */
public final class Mapper<T extends Component> {
    /** The engine instance this mapper is tied to */
    private final Engine engine;
    /** The component type */
    public final Class<T> type;
    /** A unique index for the component type */
    final int typeIndex;

    /** Zero-arg constructor for the component */
    private final Constructor<T> constructor;
    /** Pool of components this mapper maps or null if my component type is not poolable. */
    private final Pool<T> componentPool;

    /** Stores components */
    final Bag<T> components = new Bag<>();
    /** Mask of current components */
    final Mask componentsMask = new Mask();

    /** Indices of components to be removed later */
    final IntArray remove = new IntArray();
    /** Amount of components that will be removed */
    int removeCount = 0;
    /** Mask of components to be removed later */
    final Mask removeQueueMask = new Mask();
    /** Mask of components that will be removed */
    final Mask removeMask = new Mask();

    Mapper(Engine engine, Class<T> type, int typeIndex) {
        this.engine = engine;
        this.type = type;
        this.typeIndex = typeIndex;
        Constructor<T> constructor;
        try {
            constructor = type.getConstructor();
            constructor.setAccessible(true);
        } catch (NoSuchMethodException ex) {
            constructor = null;
        }
        this.constructor = constructor;
        if(Component.Pooled.class.isAssignableFrom(type)){
            assert constructor != null : "Pooled component MUST have a no-arg constructor! ("+type+")";
            componentPool = new Pool<T>() {
                @Override
                protected T newObject() {
                    return newComponent();
                }
            };
        } else {
            componentPool = null;
        }
    }

    /**
     * Retrieves a component of the type handled by this mapper. Returns {@code null}
     * if the specified entity does not have a component of the type.
     *
     * @param entity
     *            the index of the entity.
     * @return the component; may be {@code null}.
     */
    public T get(int entity) {
        return components.get(entity);
    }

    /**
     * Checks whether the specified entity has a component of the type handled by
     * this mapper.
     *
     * @param entity
     *            the index of the entity.
     * @return whether the entity has the component of the type handled by this mapper.
     */
    public boolean has(int entity) {
        return components.get(entity) != null;
    }

    /**
     * Creates a component of the type handled by this mapper for the given entity.
     *
     * @param entity
     *            the index of the entity.
     * @return the created component.
     */
    public T create(int entity) {
        final T component = createComponent();
        add(entity, component);
        return component;
    }

    /**
     * Adds a component of the type represented by this mapper. The operation
     * will take effect immediately, but notifications will be delayed until
     * the next call to {@link Engine#flush()}. Note that it is <b>not</b>
     * permitted to replace an existing component; {@link #remove(int)} must
     * be called first (and bear in mind that removals are delayed).
     *
     * NOTE: For pooled components, use {@link #create(int)} instead.
     * Manually added instances will otherwise end up in the pool.
     *
     * @param entity
     *            the index of the entity.
     * @param instance
     *            the component instance.
     */
    public void add(int entity, T instance) {
        if (has(entity)) {
            throw new IllegalArgumentException("Cannot insert a component that "
                    + "already exists: " + instance.getClass().getName());
        }

        engine.dirty = true;
        components.set(entity, instance);
        componentsMask.set(entity);
    }

    /**
     * Removes a component of the type represented by this mapper. Calling this
     * method multiple times does not result in an exception, neither does
     * attempting to remove a component that does not exist. Removal operations
     * will be delayed until the next call to {@link Engine#flush()}.
     *
     * @param entity
     *            the index of the entity.
     */
    public void remove(int entity) {
        if (!has(entity)) {
            return;
        }

        if (removeQueueMask.get(entity)) {
            return;
        }

        engine.dirty = true;
        remove.add(entity);
        removeQueueMask.set(entity);
    }

    void flushComponentRemoval() {
        final Bag<T> components = this.components;
        final IntArray remove = this.remove;
        final Mask componentsMask = this.componentsMask;
        final Mask removeMask = this.removeMask;
        final int removeCount = this.removeCount;

        final Pool<T> pool = this.componentPool;
        if(pool == null){
            //Version for standard components
            for (int i = 0; i < removeCount; i++) {
                final int entity = remove.get(i);
                components.remove(entity);
                componentsMask.clear(entity);
                removeMask.clear(entity);
            }
        } else {
            //Version for pooled components
            for (int i = 0; i < removeCount; i++) {
                final int entity = remove.get(i);

                final T component = components.remove(entity);
                if(component != null) pool.free(component);

                componentsMask.clear(entity);
                removeMask.clear(entity);
            }
        }

        if (removeCount > 0)
            remove.removeRange(0, removeCount - 1);
    }

    /** Returns a component instance which is safe to keep around, outside of the entity system.
     * No guarantees are placed on which data does the component obtain.
     * Component must have no-arg constructor for this to work. */
    public T createComponent(){
        final Pool<T> pool = this.componentPool;
        if(pool != null){
            return pool.obtain();
        }

        if (constructor == null) {
            throw new UnsupportedOperationException("Can't create component "+type.getName()+" - zero-argument constructor is missing");
        }

        return newComponent();
    }

    private static final Object[] NO_ARGS = new Object[0];

    private T newComponent() {
        try {
            return constructor.newInstance(NO_ARGS);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Could not create a new instance of "+this.type, e);
        }
    }

    /** Should be called on components created by {@link #createComponent()} which do not participate in
     * entity system and are no longer needed.
     * For pooled components, this returns them into the pool.
     * For non-pooled components this is a no-op, which can be safely omitted. */
    public void destroyComponent(T component){
        final Pool<T> pool = this.componentPool;
        if(pool != null){
            pool.free(component);
        }
    }

    @Override
    public String toString() {
        return type.getSimpleName()+" Mapper";
    }
}
