package com.github.antag99.retinazer;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.ObjectMap;
import com.github.antag99.retinazer.EngineConfig.EntitySystemRegistration;
import com.github.antag99.retinazer.util.Mask;

/**
 * Engine is the core class of retinazer; it manages all active entities,
 * performs system processing and initialization.
 */
public final class Engine {

    private final EntitySystem[] systems;
    private final ObjectMap<Class<? extends EntitySystem>, EntitySystem> systemsByType;

    final Mask entities = new Mask();
    final Mask removeQueue = new Mask();
    final Mask remove = new Mask();

    final ComponentManager componentManager;
    final FamilyManager familyManager;
    final WireManager wireManager;

    /** Tracks whether any components or entities have been modified; reset at every call to flush() */
    boolean dirty = false;
    /** Tracks whether this engine is within a call to update() */
    boolean update = false;

    /**
     * Creates a new {@link Engine} based on the specified configuration. Note
     * that the same configuration should <b>not</b> be reused, as system
     * implementations do not handle being registered to multiple engines.
     *
     * @param config
     *            configuration for this Engine.
     */
    public Engine(EngineConfig config) {
        this(config, true);
    }

    /**
     * @see #Engine(EngineConfig)
     * @param initialize if {@link #initialize()} should be called automatically, if false, call it manually before doing anything else with the engine
     */
    public Engine(EngineConfig config, boolean initialize) {
        componentManager = new ComponentManager(this);
        familyManager = new FamilyManager(this);
        wireManager = new WireManager(this, config);

        Array<EntitySystemRegistration> systemRegistrations = new Array<>();
        for (EntitySystemRegistration system : config.systems) {
            systemRegistrations.add(system);
        }

        systemRegistrations.sort();

        EntitySystem[] systems = new EntitySystem[systemRegistrations.size];
        ObjectMap<Class<? extends EntitySystem>, EntitySystem> systemsByType = new ObjectMap<>();

        for (int i = 0, n = systemRegistrations.size; i < n; i++) {
            systems[i] = systemRegistrations.get(i).system;
            systemsByType.put(systems[i].getClass(), systems[i]);
        }

        this.systems = systems;
        this.systemsByType = systemsByType;

        for (EntitySystem system : systems)
            wire(system);

        if (initialize) initialize();
    }

    public void initialize() {
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

            for (Mapper<?> mapper : componentManager.array) {
                mapper.removeMask.set(mapper.removeQueueMask);
                mapper.removeMask.or(remove);
                remove.getIndices(mapper.remove);
                mapper.removeQueueMask.clear();
                mapper.removeCount = mapper.remove.size;
            }

            familyManager.updateFamilyMembership();
            componentManager.applyComponentChanges();

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
     * Gets the system of the given type. Note that only one system of a type
     * can exist in an engine configuration.
     *
     * @param systemType
     *            type of the system
     * @param <T>
     *            generic type of the system.
     * @return the system
     * @throws IllegalArgumentException
     *             if the system does not exist
     */
    public <T extends EntitySystem> T getSystem(Class<T> systemType) {
        return getSystem(systemType, false);
    }

    /**
     * Gets the system of the given type. Note that only one system of a type
     * can exist in an engine configuration.
     *
     * @param systemType
     *            type of the system.
     * @param optional
     *            whether to return {@code null} if the system does not exist.
     * @param <T>
     *            generic type of the system.
     * @return the system, or {@code null} if {@code optional} is {@code true}
     *         and the system does not exist.
     * @throws IllegalArgumentException
     *             if {@code optional} is {@code false} and the system does not exist.
     */
    public <T extends EntitySystem> T getSystem(Class<T> systemType, boolean optional) {
        @SuppressWarnings("unchecked")
        T system = (T) systemsByType.get(systemType);

        if (system == null && !optional) {
            throw new IllegalArgumentException("System not registered: " + systemType.getName());
        } else {
            return system;
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
    public <T extends Component> Mapper<T> getMapper(Class<T> componentType) {
        return componentManager.getMapper(componentType);
    }

    /**
     * Get all mappers created so far.
     * Do not modify or keep the array.
     *
     * @return array of all mappers
     */
    public Mapper<?>[] getMappers(){
        return componentManager.array;
    }
}
