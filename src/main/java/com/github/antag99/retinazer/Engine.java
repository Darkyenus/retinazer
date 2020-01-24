package com.github.antag99.retinazer;

import com.badlogic.gdx.utils.ObjectMap;
import com.github.antag99.retinazer.util.Mask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Engine is the core class of retinazer; it manages all active entities,
 * performs system processing and initialization.
 */
public final class Engine {

    private final EngineService[] services;
    private final ObjectMap<Class<? extends EngineService>, EngineService> servicesByType = new ObjectMap<>();

    /** Entities that currently exist. */
    final Mask entities = new Mask();
    /** Subset of {@link #entities} - entities that will be removed on next {@link #flush()}. */
    private final Mask entitiesScheduledForRemoval = new Mask();

    /** Entities which exist currently or did in the last update cycle.
     * Used to prevent reusing entity IDs too soon. */
    private final Mask shadowEntities = new Mask();
    /** Subset of {@link #shadowEntities} - entities that were removed (or were scheduled to be removed) this update cycle. */
    private Mask entitiesRemovedThisUpdate = new Mask();
    /** {@link #entitiesRemovedThisUpdate} from the last update cycle.
     * These entity IDs will become available for allocation again after the end of this update cycle. */
    private Mask entitiesRemovedLastUpdate = new Mask();

    public final ComponentSet componentDomain;
    final Mapper<?>[] componentMappers;
    private final FamilyManager familyManager;
    private final WireManager wireManager;

    /** Tracks whether any components or entities have been modified; reset at every call to flush() */
    boolean dirty = false;
    /** Tracks whether this engine is within a call to update() */
    boolean update = false;

    /**
     * Creates a new {@link Engine} based on the specified configuration.
     *
     * @param domain set of components that this engine operates over
     * @param services of this engine. Services implementing {@link WireResolver} will
     * be used as wire resolvers. Order is significant.
     */
    public Engine(ComponentSet domain, EngineService...services) {
        final ArrayList<WireResolver> wireResolvers = new ArrayList<>();
        wireResolvers.add(new DefaultWireResolver(this));

        for (EngineService service : services) {
            if (service instanceof WireResolver) {
                wireResolvers.add((WireResolver) service);
            }
            final EngineService previous = servicesByType.put(service.getClass(), service);
            if (previous != null) {
                throw new IllegalArgumentException("Types of services must be unique and "+previous+" is duplicated by "+service);
            }
        }

        this.componentDomain = domain;
        this.componentMappers = domain.buildComponentMappers(this);
        this.services = services;
        this.familyManager = new FamilyManager(this);
        this.wireManager = new WireManager(wireResolvers);

        for (EngineService service : services)
            wire(service);

        for (EngineService service : services)
            service.initialize();

        this.wireManager.flushCache();
        flush();
    }

    public void wire(Object object) {
        wireManager.wire(object);
    }

    /** Updates all systems, interleaved by inserting/removing entities to/from entity sets. */
    public void update() {
        if (update) {
            throw new IllegalStateException("Cannot nest calls to update()");
        }

        update = true;

        flush();

        for (EngineService service : services) {
            service.update();
            flush();
        }

        // Update shadow entities
        final Mask entitiesRemovedLastUpdate = this.entitiesRemovedLastUpdate;
        final Mask entitiesRemovedThisUpdate = this.entitiesRemovedThisUpdate;
        // Allow the entity IDs to be used again
        shadowEntities.andNot(entitiesRemovedLastUpdate);
        entitiesRemovedLastUpdate.clear();
        // Swap the masks
        this.entitiesRemovedLastUpdate = entitiesRemovedThisUpdate;
        this.entitiesRemovedThisUpdate = entitiesRemovedLastUpdate;

        update = false;
    }

    public void flush() {
        if (dirty) {
            dirty = false;

            entities.andNot(entitiesScheduledForRemoval);
            for (Mapper<?> mapper : componentMappers) {
                mapper.removeScheduled(entitiesScheduledForRemoval);
            }
            entitiesScheduledForRemoval.clear();

            familyManager.updateFamilyMembership();
        }
    }

    /**
     * Creates a new entity. This entity will be assigned a index, which is not
     * shared with any existing entity. Note that indices are reused once the
     * entity is no longer active. The entity is immediately inserted into the
     * engine, but it won't show up in entity sets until the next system processing.
     *
     * @return index of the created entity.
     */
    public int createEntity() {
        dirty = true;
        int entity = shadowEntities.nextClearBit(0);
        entities.set(entity);
        shadowEntities.set(entity);
        return entity;
    }

    /**
     * Create a new entity with defined ID.
     * Note that when an entity is removed, its ID is not available that update cycle, nor the one after,
     * but only the one after that.
     *
     * @return true if entity was created, false if such entity already exists
     */
    public boolean createEntity(int entity) {
        if (shadowEntities.setChanged(entity)) {
            entities.set(entity);
            return dirty = true;
        }
        return false;
    }

    /**
     * Destroys the entity with the given index. This will not remove the entity
     * immediately; only after the current system processing,
     *
     * @param entity
     *            the entity to destroy.
     */
    public void destroyEntity(int entity) {
        if (entitiesScheduledForRemoval.setChanged(entity)) {
            entitiesRemovedThisUpdate.set(entity);
            dirty = true;
        }
    }

    /**
     * Gets all entities added to this engine.
     *
     * @return {@link EntitySetView} containing all entities added to this engine
     */
    public EntitySetView getEntities() {
        return getEntities(Family.ALL);
    }

    /**
     * Retrieves the set of entities belonging to the given {@link Family}.
     * The set is dynamically updated, so the returned object may be kept indefinitely.
     *
     * @param family specification of the entity family
     */
    public EntitySetView getEntities(Family family) {
        return familyManager.getFamily(family);
    }

    /**
     * Gets the service of the given type. Note that only one service of a type
     * can exist in an engine configuration.
     * @throws IllegalArgumentException if the service does not exist
     */
    public <T extends EngineService> T getService(Class<T> serviceType) {
        return getService(serviceType, false);
    }

    /**
     * Gets the service of the given type. Note that only one service of a type
     * can exist in an engine configuration.
     *
     * @return the system, or {@code null} if {@code optional} is {@code true} and the system does not exist.
     * @throws IllegalArgumentException if {@code optional} is {@code false} and the system does not exist.
     */
    public <T extends EngineService> T getService(Class<T> serviceType, boolean optional) {
        @SuppressWarnings("unchecked")
        final T service = (T) servicesByType.get(serviceType);

        if (service == null && !optional) {
            throw new IllegalArgumentException("Service not registered: " + serviceType.getName());
        } else {
            return service;
        }
    }

    /** Retrieve all services that are subclasses of given class */
    @SuppressWarnings("unchecked")
    public <T> List<T> getServices(Class<T> type) {
        if (type == EngineService.class) {
            return (List<T>) Arrays.asList(services);
        }
        ArrayList<T> result = null;
        for (EngineService service : services) {
            if (type.isInstance(service)) {
                if (result == null) {
                    result = new ArrayList<>();
                }
                result.add((T) service);
            }
        }

        if (result == null) {
            return Collections.emptyList();
        }
        return result;
    }

    /**
     * Gets a {@link Mapper} for accessing components of the specified type.
     *
     * @param componentType
     *            component type.
     * @param <T>
     *            generic type of the component.
     * @return mapper for the specified type.
     */
    @SuppressWarnings("unchecked")
    public <T extends Component> Mapper<T> getMapper(Class<T> componentType) {
        return (Mapper<T>) componentMappers[componentDomain.index(componentType)];
    }

    /** @return array of all mappers - do not modify! */
    public Mapper<?>[] getMappers(){
        return componentMappers;
    }
}
