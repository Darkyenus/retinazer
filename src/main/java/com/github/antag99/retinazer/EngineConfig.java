package com.github.antag99.retinazer;

import java.util.Objects;

import com.badlogic.gdx.utils.Array;
import com.github.antag99.retinazer.resolvers.DefaultWireResolver;
import com.github.antag99.retinazer.resolvers.MapperWireResolver;

/** Stores configuration for an {@link Engine} instance. */
public final class EngineConfig {

    final ComponentSet componentSet;
    final Array<EntitySystemRegistration> systems = new Array<>();
    final Array<WireResolver> wireResolvers = new Array<>(WireResolver.class);

    /** Creates a new engine configuration with the default values. */
    @SafeVarargs
    public EngineConfig(Class<? extends Component>...components) {
        componentSet = new ComponentSet(components);
        wireResolvers.add(new DefaultWireResolver());
        wireResolvers.add(new MapperWireResolver());
    }

    /**
     * Registers a system.
     *
     * @param system
     *            system to register.
     * @return {@code this} for chaining.
     */
    public EngineConfig addSystem(EntitySystem system) {
        return addSystem(system, Order.DEFAULT);
    }

    /**
     * Registers a system.
     *
     * @param system
     *            system to register.
     * @param order
     *            order of the system.
     * @return {@code this} for chaining.
     */
    public EngineConfig addSystem(EntitySystem system, Order order) {
        Objects.requireNonNull(system, "system cannot be null");
        Objects.requireNonNull(order, "order cannot be null");
        Class<? extends EntitySystem> systemType = system.getClass();

        for (int i = 0, n = systems.size; i < n; i++) {
            if (systems.get(i).system.getClass() == systemType) {
                throw new IllegalArgumentException(
                        "System of type " + systemType.getName() + " has already been registered");
            }
        }

        systems.add(new EntitySystemRegistration(system, order));
        return this;
    }

    /**
     * Registers a wire resolver.
     *
     * @param resolver
     *            resolver to register.
     * @return {@code this} for chaining.
     */
    public EngineConfig addWireResolver(WireResolver resolver) {
        Objects.requireNonNull(resolver, "resolver cannot be null");
        wireResolvers.add(resolver);
        return this;
    }

    static final class EntitySystemRegistration implements Comparable<EntitySystemRegistration> {
        final EntitySystem system;
        final Order order;

        EntitySystemRegistration(EntitySystem system, Order order) {
            this.system = system;
            this.order = order;
        }

        @Override
        public int compareTo(EntitySystemRegistration o) {
            return Integer.compare(order.ordinal(), o.order.ordinal());
        }
    }
}
