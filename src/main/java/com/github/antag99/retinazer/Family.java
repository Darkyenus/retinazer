package com.github.antag99.retinazer;

import com.github.antag99.retinazer.util.Mask;

/** Specifies a set of entities defined by what {@link Component}s they have or don't have. Immutable. */
public final class Family {

    /** Family of all entities.
     * NOTE: It is impossible to derive anything from it, because it has an empty domain. */
    static final Family ALL = new Family(ComponentSet.EMPTY);

    final ComponentSet domain;
    /** Each family member must have all of these components. */
    final Mask requiredComponents = new Mask();
    /** Each family member must not have any of these components. */
    final Mask excludedComponents = new Mask();

    Family(ComponentSet domain) {
        this.domain = domain;
    }

    @SafeVarargs
    Family(ComponentSet domain, boolean require, Class<? extends Component>... components) {
        this(domain);
        final Mask mask = require ? requiredComponents : excludedComponents;
        for (Class<? extends Component> component : components) {
            mask.set(domain.index(component));
        }
    }


    /** Derive a new {@link Family} which also requires given components. */
    @SafeVarargs
    public final Family with(Class<? extends Component>... components) {
        final ComponentSet domain = this.domain;
        final Family result = new Family(domain);
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

    /** Derive a new {@link Family} which also excludes given components. */
    @SafeVarargs
    public final Family without(Class<? extends Component>... components) {
        if (components.length == 0) {
            return this;
        }
        final ComponentSet domain = this.domain;
        final Family result = new Family(domain);
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
