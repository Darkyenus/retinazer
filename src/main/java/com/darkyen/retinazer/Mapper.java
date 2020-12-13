package com.darkyen.retinazer;

import com.badlogic.gdx.utils.Pool;
import com.darkyen.retinazer.util.Bag;
import com.darkyen.retinazer.util.Mask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.NoSuchElementException;

/**
 * {@link Mapper} stores and manages components of a specific type for an {@link Engine}.
 * This is the primary access point for component retrieval and modification.
 * <p>
 * It also handles component pooling.
 *
 * @param <T> the component type.
 */
public final class Mapper<T extends Component> {
	/** The engine instance this mapper is tied to */
	@NotNull
	public final Engine   engine;
	/** The component type */
	@NotNull
	public final Class<T> type;

	/** Zero-arg constructor for the component */
	@Nullable
	private final Constructor<T> constructor;
	/** Pool of components this mapper maps or null if my component type is not poolable. */
	@Nullable
	private final Pool<T>        componentPool;

	/** Stores components */
	@NotNull
	private final Bag<T> components     = new Bag<>();
	/** Mask of current components */
	@NotNull
	final         Mask   componentsMask = new Mask();

	/** Mask of components to be removed on next flush */
	@NotNull
	private final Mask scheduledForRemoval = new Mask();

	Mapper(@NotNull Engine engine, @NotNull Class<T> type) {
		this.engine = engine;
		this.type = type;
		Constructor<T> constructor;
		try {
			constructor = type.getConstructor();
			constructor.setAccessible(true);
		} catch (NoSuchMethodException ex) {
			constructor = null;
		}
		this.constructor = constructor;
		if (Component.Pooled.class.isAssignableFrom(type)) {
			assert constructor != null : "Pooled component MUST have a no-arg constructor! (" + type + ")";
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
	 * @param entity the index of the entity.
	 * @return the component; may be {@code null}.
	 */
	@Nullable
	public T getOrNull(int entity) {
		return components.get(entity);
	}

	/**
	 * Retrieves a component of the type handled by this mapper.
	 * @param entity the index of the entity.
	 * @return the component
	 * @throws java.util.NoSuchElementException when the entity does not have that component
	 */
	@NotNull
	public T get(int entity) {
		final T component = components.get(entity);
		if (component == null) {
			throw new NoSuchElementException("Entity "+entity+" does not have a component of type "+type.getName());
		}
		return component;
	}

	/**
	 * Checks whether the specified entity has a component of the type handled by
	 * this mapper.
	 *
	 * @param entity the index of the entity.
	 * @return whether the entity has the component of the type handled by this mapper.
	 */
	public boolean has(int entity) {
		return componentsMask.get(entity);
	}

	/**
	 * Creates a component of the type handled by this mapper for the given entity.
	 *
	 * @param entity the index of the entity.
	 * @return the created component.
	 */
	@NotNull
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
	 * <p>
	 * NOTE: For pooled components, use {@link #create(int)} instead.
	 * Manually added instances will otherwise end up in the pool.
	 *
	 * @param entity the index of the entity.
	 * @param instance the component instance.
	 */
	public void add(int entity, @NotNull T instance) {
		if (!componentsMask.setChanged(entity)) {
			throw new IllegalArgumentException("Cannot insert a component that "
					+ "already exists: " + instance.getClass().getName());
		}
		engine.dirty = true;
		components.set(entity, instance);
	}

	/**
	 * Removes a component of the type represented by this mapper. Calling this
	 * method multiple times does not result in an exception, neither does
	 * attempting to remove a component that does not exist. Removal operations
	 * will be delayed until the next call to {@link Engine#flush()}.
	 *
	 * @param entity the index of the entity.
	 */
	public void remove(int entity) {
		if (componentsMask.get(entity) && scheduledForRemoval.setChanged(entity)) {
			engine.dirty = true;
		}
	}

	void removeScheduled(@NotNull Mask globallyRemoved) {
		final Bag<T> components = this.components;
		final Mask componentsMask = this.componentsMask;
		final Mask scheduledForRemoval = this.scheduledForRemoval;

		scheduledForRemoval.or(globallyRemoved);
		scheduledForRemoval.and(componentsMask);

		final Pool<T> pool = this.componentPool;
		for (int entity = scheduledForRemoval.nextSetBit(0); entity != -1; entity = scheduledForRemoval.nextSetBit(entity + 1)) {
			final T component = components.remove(entity);
			assert component != null;
			if (pool != null) pool.free(component);
		}

		componentsMask.andNot(scheduledForRemoval);
		scheduledForRemoval.clear();
	}

	/**
	 * Returns a component instance which is safe to keep around, outside of the entity system.
	 * No guarantees are placed on which data does the component obtain.
	 * Component must have no-arg constructor for this to work.
	 */
	@NotNull
	public T createComponent() {
		final Pool<T> pool = this.componentPool;
		if (pool != null) {
			return pool.obtain();
		}

		if (constructor == null) {
			throw new UnsupportedOperationException("Can't create component " + type.getName() + " - zero-argument constructor is missing");
		}

		return newComponent();
	}

	private static final Object[] NO_ARGS = new Object[0];

	@NotNull
	private T newComponent() {
		try {
			final Constructor<T> constructor = this.constructor;
			assert constructor != null;
			return constructor.newInstance(NO_ARGS);
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException("Could not create a new instance of " + this.type, e);
		}
	}

	/**
	 * Should be called on components created by {@link #createComponent()} which do not participate in
	 * entity system and are no longer needed.
	 * For pooled components, this returns them into the pool.
	 * For non-pooled components this is a no-op, which can be safely omitted.
	 */
	public void destroyComponent(@NotNull T component) {
		final Pool<T> pool = this.componentPool;
		if (pool != null) {
			pool.free(component);
		}
	}

	@NotNull
	@Override
	public String toString() {
		return type.getSimpleName() + " Mapper";
	}
}
