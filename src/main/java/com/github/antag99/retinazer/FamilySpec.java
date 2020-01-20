package com.github.antag99.retinazer;

import com.github.antag99.retinazer.util.Mask;

/** Specifies a {@link Family}. Immutable. */
public final class FamilySpec {

    /** Family of all entities.
     * NOTE: It is impossible to derive anything from it, because it has an empty domain. */
    static final FamilySpec ALL = new FamilySpec(ComponentSet.EMPTY);

    final ComponentSet domain;
    /** Each family member must have all of these components. */
    final Mask requiredComponents = new Mask();
    /** Each family member must not have any of these components. */
    final Mask excludedComponents = new Mask();

    FamilySpec(ComponentSet domain) {
        this.domain = domain;
    }

    @SafeVarargs
    FamilySpec(ComponentSet domain, boolean require, Class<? extends Component>... components) {
        this(domain);
        final Mask mask = require ? requiredComponents : excludedComponents;
        for (Class<? extends Component> component : components) {
            mask.set(domain.index(component));
        }
    }


    /** Derive a new {@link FamilySpec} which also requires given components. */
    @SafeVarargs
    public final FamilySpec with(Class<? extends Component>... components) {
        final ComponentSet domain = this.domain;
        final FamilySpec result = new FamilySpec(domain);
        for (Class<? extends Component> component : components) {
            result.requiredComponents.set(domain.index(component));
        }
        if (result.requiredComponents.equals(this.requiredComponents)) {
            return this;
        }
        result.excludedComponents.set(this.excludedComponents);
        assert !result.requiredComponents.intersects(result.excludedComponents);
        return result;
    }

    /** Derive a new {@link FamilySpec} which also excludes given components. */
    @SafeVarargs
    public final FamilySpec without(Class<? extends Component>... components) {
        if (components.length == 0) {
            return this;
        }
        final ComponentSet domain = this.domain;
        final FamilySpec result = new FamilySpec(domain);
        for (Class<? extends Component> component : components) {
            result.excludedComponents.set(domain.index(component));
        }
        if (result.excludedComponents.equals(this.excludedComponents)) {
            return this;
        }
        result.requiredComponents.set(this.requiredComponents);
        assert !result.requiredComponents.intersects(result.excludedComponents);
        return result;
    }
}
