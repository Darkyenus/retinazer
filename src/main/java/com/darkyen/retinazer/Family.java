package com.darkyen.retinazer;

import com.darkyen.retinazer.util.Mask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Specifies a set of entities defined by what {@link Component}s they have or don't have. Immutable. */
public final class Family {

	/** Empty {@link Mask} instance. DO NOT MODIFY. */
	@NotNull
	static final Mask   EMPTY_MASK = new Mask();
	/**
	 * Family of all entities.
	 * NOTE: It is impossible to derive anything from it, because it has an empty domain.
	 */
	@NotNull
	static final Family ALL        = new Family(ComponentSet.EMPTY, EMPTY_MASK, EMPTY_MASK);

	@NotNull
	final ComponentSet domain;
	/** Each family member must have all of these components. */
	@NotNull
	final Mask         requiredComponents;
	/** Each family member must not have any of these components. */
	@NotNull
	final Mask         excludedComponents;

	private final transient int hashCode;

	Family(@NotNull ComponentSet domain, @NotNull Mask requiredComponents, @NotNull Mask excludedComponents) {
		this.domain = domain;
		this.requiredComponents = requiredComponents;
		this.excludedComponents = excludedComponents;

		// Eager hash code computation, it will be used at least once and possibly many times
		int result = domain.hashCode();
		result = 31 * result + requiredComponents.hashCode();
		result = 31 * result + excludedComponents.hashCode();
		this.hashCode = result;
	}

	/**
	 * @return whether this family requires the given component type
	 * @throws IllegalArgumentException when the component is not a part of the domain
	 */
	public boolean requires(@NotNull Class<? extends Component> componentType) {
		return requiredComponents.get(domain.index(componentType));
	}

	/**
	 * @return whether this family excludes the given component type
	 * @throws IllegalArgumentException when the component is not a part of the domain
	 */
	public boolean excludes(@NotNull Class<? extends Component> componentType) {
		return excludedComponents.get(domain.index(componentType));
	}

	/** Derive a new {@link Family} which also requires given components. */
	@SafeVarargs
	@NotNull
	public final Family with(@NotNull Class<? extends Component>... components) {
		final ComponentSet domain = this.domain;
		final Mask requiredComponents = this.requiredComponents;
		final Mask excludedComponents = this.excludedComponents;

		final Mask newRequiredComponents = Family.maskOf(requiredComponents, domain, components);
		if (newRequiredComponents == requiredComponents) {
			return this;
		}
		assert !newRequiredComponents.intersects(excludedComponents);
		return new Family(domain, newRequiredComponents, excludedComponents);
	}

	/** Derive a new {@link Family} which also excludes given components. */
	@SafeVarargs
	@NotNull
	public final Family without(@NotNull Class<? extends Component>... components) {
		final ComponentSet domain = this.domain;
		final Mask requiredComponents = this.requiredComponents;
		final Mask excludedComponents = this.excludedComponents;

		final Mask newExcludedComponents = Family.maskOf(excludedComponents, domain, components);
		if (newExcludedComponents == excludedComponents) {
			return this;
		}
		assert !newExcludedComponents.intersects(requiredComponents);
		return new Family(domain, requiredComponents, newExcludedComponents);
	}

	@NotNull
	static Mask maskOf(@NotNull Mask parent, @NotNull ComponentSet domain, @NotNull Class<? extends Component>[] components) {
		if (components.length == 0) {
			return parent;
		}
		final Mask extendedMask = new Mask();
		extendedMask.set(parent);
		boolean modified = false;
		for (Class<? extends Component> component : components) {
			modified |= extendedMask.setChanged(domain.index(component));
		}
		if (!modified) {
			return parent;
		}
		return extendedMask;
	}

	@Override
	public boolean equals(@Nullable Object o) {
		if (this == o) return true;
		if (!(o instanceof Family)) return false;

		Family family = (Family) o;

		return requiredComponents.equals(family.requiredComponents)
				&& excludedComponents.equals(family.excludedComponents)
				&& domain.equals(family.domain);
	}

	@Override
	public int hashCode() {
		return hashCode;
	}
}
