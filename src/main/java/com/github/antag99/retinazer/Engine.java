package com.github.antag99.retinazer;

import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.ObjectMap;
import com.github.antag99.retinazer.util.Mask;

import java.util.ArrayList;

/**
 * Engine is the core class of retinazer; it manages all active entities,
 * performs system processing and initialization.
 */
public final class Engine {

    private final EntitySystem[] systems;
    private final ObjectMap<Class<? extends EngineService>, EngineService> servicesByType = new ObjectMap<>();

    final Mask entities = new Mask();
    final Mask removeQueue = new Mask();
    final Mask remove = new Mask();

    final ComponentSet componentSet;
    final Mapper<?>[] componentMappers;
    final FamilyManager familyManager;
    final WireManager wireManager;

    /** Tracks whether any components or entities have been modified; reset at every call to flush() */
    boolean dirty = false;
    /** Tracks whether this engine is within a call to update() */
    boolean update = false;

    /**
     * Creates a new {@link Engine} based on the specified configuration.
     *
     * @param componentSet set of components that this engine operates over
     * @param services of this engine. Services implementing {@link EntitySystem} and {@link WireResolver} will
     * be used as entity systems and wire resolvers, respectively. Order is significant for both.
     */
    public Engine(ComponentSet componentSet, EngineService...services) {
        final ArrayList<EntitySystem> entitySystems = new ArrayList<>();
        final ArrayList<WireResolver> wireResolvers = new ArrayList<>();

        wireResolvers.add(new DefaultWireResolver());

        for (EngineService service : services) {
            if (service instanceof EntitySystem) {
                entitySystems.add((EntitySystem) service);
            }
            if (service instanceof WireResolver) {
                wireResolvers.add((WireResolver) service);
            }
            servicesByType.put(service.getClass(), service);
        }

        this.componentSet = componentSet;
        componentMappers = componentSet.buildComponentMappers(this);
        familyManager = new FamilyManager(this);
        wireManager = new WireManager(this, wireResolvers.toArray(WireResolver.EMPTY_ARRAY));

        final EntitySystem[] systems = this.systems = entitySystems.toArray(EntitySystem.EMPTY_ARRAY);

        for (EntitySystem system : systems)
            wire(system);

        for (EntitySystem system : systems)
            system.setup();

        for (EntitySystem system : systems)
            system.initialize();

        flush();
    }

    public void wire(Object object) {
        wireManager.wire(object);
    }

    public void addEntityListener(EntityListener entityListener) {
        getFamily(new FamilyConfig()).addListener(entityListener);
    }

    public void removeEntityListener(EntityListener entityListener) {
        getFamily(new FamilyConfig()).removeListener(entityListener);
    }

    /**
     * Updates all systems, interleaved by inserting/removing entities to/from
     * entity sets.
     * @param delta time in seconds since last update
     */
    public void update(float delta) {
        if (update) {
            throw new IllegalStateException("Cannot nest calls to update()");
        }

        update = true;

        flush();

        for (EntitySystem system : systems) {
            system.update(delta);

            flush();
        }

        update = false;
    }

    /**
     * Resets this engine; this removes all existing entities.
     */
    public void reset() {
        if (update) {
            throw new IllegalStateException("Cannot call reset() within update()");
        }

        update = true;

        flush();

        IntArray entities = getEntities().getIndices();
        int[] items = entities.items;
        for (int i = 0, n = entities.size; i < n; i++) {
            destroyEntity(items[i]);
        }

        flush();

        update = false;
    }

    public void flush() {
        while (dirty) {
            dirty = false;

            remove.set(removeQueue);
            removeQueue.clear();

            for (Mapper<?> mapper : componentMappers) {
                mapper.removeMask.set(mapper.removeQueueMask);
                mapper.removeMask.or(remove);
                remove.getIndices(mapper.remove);
                mapper.removeQueueMask.clear();
                mapper.removeCount = mapper.remove.size;
            }

            familyManager.updateFamilyMembership();
            for (Mapper<?> mapper : componentMappers) {
                mapper.flushComponentRemoval();
            }

            entities.andNot(remove);
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
        int entity = entities.nextClearBit(0);
        entities.set(entity);
        return entity;
    }

    /**
     * Create a new entity with defined ID.
     *
     * @return true if entity was created, false if such entity already exists
     */
    public boolean createEntity(int entity) {
        final boolean exists = entities.get(entity);
        if(exists) return false;
        dirty = true;
        entities.set(entity);
        return true;
    }

    /**
     * Destroys the entity with the given index. This will not remove the entity
     * immediately; only after the current system processing,
     *
     * @param entity
     *            the entity to destroy.
     */
    public void destroyEntity(int entity) {
        dirty = true;
        removeQueue.set(entity);
    }

    /**
     * Gets all entities added to this engine.
     *
     * @return {@link EntitySetView} containing all entities added to this engine
     */
    public EntitySetView getEntities() {
        return familyManager.getEntities();
    }

    /**
     * Gets or creates a family for the given configuration. Typically, it's
     * not necessary to retrieve a family directly, but rather only use
     * {@link FamilyConfig}.
     *
     * @param config
     *            configuration for the family
     * @return family for the given configuration
     */
    public Family getFamily(FamilyConfig config) {
        return familyManager.getFamily(config);
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

    /**
     * Gets the systems registered during configuration of the engine.
     * Do not modify returned value.
     *
     * @return all systems registered during configuration of the engine.
     */
    public EntitySystem[] getSystems() {
        return systems;
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
        return (Mapper<T>) componentMappers[componentSet.index(componentType)];
    }

    /** @return array of all mappers - do not modify! */
    public Mapper<?>[] getMappers(){
        return componentMappers;
    }
}
