package com.github.antag99.retinazer;

import com.badlogic.gdx.utils.ObjectIntMap;

/**
 * A set of components, which assigns each component a unique ID.
 * Used for optimizations as a form of <a href="https://en.wikipedia.org/wiki/Perfect_hash_function">perfect hashing</a>.
 *
 * Immutable.
 *
 * It is expected that the application will create at most one instance of {@link ComponentSet} per {@link Engine}.
 *
 * {@link #equals(Object)} uses default identity comparison for performance.
 */
public final class ComponentSet {

	/** Set of no components. */
	public static final ComponentSet EMPTY = new ComponentSet();

	private final Class<? extends Component>[] components;
	private final ObjectIntMap<Class<? extends Component>> componentToId = new ObjectIntMap<>();

	/** Create a new set of components.
	 * @param components each non-null and unique from the others */
	@SafeVarargs
	public ComponentSet(final Class<? extends Component>...components) {
		this.components = components;
		for (int i = 0; i < components.length; i++) {
			componentToId.put(components[i], i);
		}
		if (componentToId.size != components.length) {
			throw new IllegalArgumentException("Set of components contains duplicates");
		}
	}

	/** @return Amount of registered components */
	public int size() {
		return components.length;
	}

	/** @return index assigned to given component
	 * @throws IllegalArgumentException if the component is not in the set */
	public int index(final Class<? extends Component> component) {
		final int index = componentToId.get(component, -1);
		if (index < 0) {
			throw new IllegalArgumentException("Component "+component.getName()+" not in the set");
		}
		return index;
	}

	/** @return component with given index
	 * @throws IndexOutOfBoundsException when the index does not belong to any component */
	public Class<? extends Component> component(final int index) {
		return components[index];
	}

	/** Check if this is a compatible subset of the other set.
	 * A compatible subset is a subset where each component from the subset has the same index in both sets. */
	public boolean isSubsetOf(ComponentSet other) {
		final Class<? extends Component>[] myComponents = this.components;
		final Class<? extends Component>[] otherComponents = other.components;
		final int mySize = myComponents.length;
		if (mySize > otherComponents.length) {
			return false;
		}

		for (int i = 0; i < mySize; i++) {
			if (!myComponents[i].equals(otherComponents[i])) {
				return false;
			}
		}
		return true;
	}

	/** Create a new {@link FamilySpec} which contains all entities. */
	public final FamilySpec family() {
		return new FamilySpec(this);
	}

	/** Create a new {@link FamilySpec} which requires given components. */
	@SafeVarargs
	public final FamilySpec familyWith(Class<? extends Component>... components) {
		return new FamilySpec(this, true, components);
	}

	/** Create a new {@link FamilySpec} which excludes given components. */
	@SafeVarargs
	public final FamilySpec familyWithout(Class<? extends Component>... components) {
		return new FamilySpec(this, false, components);
	}

	Mapper<?>[] buildComponentMappers(Engine engine) {
		final Class<? extends Component>[] components = this.components;
		final Mapper<?>[] mappers = new Mapper[components.length];
		for (int i = 0; i < components.length; i++) {
			mappers[i] = new Mapper<>(engine, components[i], i);
		}
		return mappers;
	}
}
