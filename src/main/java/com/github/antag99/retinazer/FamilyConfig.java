package com.github.antag99.retinazer;

import com.badlogic.gdx.utils.ObjectSet;

public final class FamilyConfig {
    ObjectSet<Class<? extends Component>> components = new ObjectSet<>();
    ObjectSet<Class<? extends Component>> excludedComponents = new ObjectSet<>();

    public FamilyConfig() {
    }

    @SafeVarargs
    public final FamilyConfig with(Class<? extends Component>... componentTypes) {
        ObjectSet<Class<? extends Component>> newComponents = new ObjectSet<>();
        newComponents.addAll(components);
        for (Class<? extends Component> componentType : componentTypes) {
            if (newComponents.contains(componentType))
                throw new IllegalArgumentException(componentType.getName());
            if (excludedComponents.contains(componentType))
                throw new IllegalArgumentException(componentType.getName());
            newComponents.add(componentType);
        }
        this.components = newComponents;
        return this;
    }

    @SafeVarargs
    public final FamilyConfig exclude(Class<? extends Component>... componentTypes) {
        ObjectSet<Class<? extends Component>> newExcludedComponents = new ObjectSet<>();
        newExcludedComponents.addAll(excludedComponents);
        for (Class<? extends Component> componentType : componentTypes) {
            if (newExcludedComponents.contains(componentType))
                throw new IllegalArgumentException(componentType.getName());
            if (components.contains(componentType))
                throw new IllegalArgumentException(componentType.getName());
            newExcludedComponents.add(componentType);
        }
        this.excludedComponents = newExcludedComponents;
        return this;
    }
}
