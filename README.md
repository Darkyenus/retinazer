# Retinazer
[![License](https://img.shields.io/badge/license-MIT-blue)](http://choosealicense.com/licenses/mit/)
[![Wemi](https://img.shields.io/badge/Wemi-0.15-blue)](https://github.com/Darkyenus/wemi)
[![JitPack](https://jitpack.io/v/com.darkyen/retinazer.svg)](https://jitpack.io/#com.darkyen/retinazer)
[![JavaDoc](https://img.shields.io/badge/-JavaDoc-informational)](https://jitpack.io/com/darkyen/retinazer/latest/javadoc/)

This is an implementation of the [entity-component-system](https://en.wikipedia.org/wiki/Entity_component_system) design
pattern in Java and a fork of the [original implementation by Anton Gustafsson](https://github.com/antag99/retinazer).
Since forking, the code in this repository has moved in a slightly different direction than the original, so be sure to check it out as well.

## Distribution
You can obtain a build through [JitPack](https://jitpack.io/#com.darkyen/retinazer) and treat it like any other Java library.
The only dependency is the core jar of [libGDX](http://www.libgdx.com/) for the primitive collections.

## Documentation
All basic building blocks of an [ECS](https://en.wikipedia.org/wiki/Entity_component_system) are here.
- Entity is represented by a single `int` - and entity ID
    - Entity IDs are managed by [`Engine`](src/main/java/com/darkyen/retinazer/Engine.java)
    - Entity IDs are given out sequentially, but you can specify ID to use explicitly, for example for multiplayer synchronization
- Components are instances of classes implementing the [`Component`](src/main/java/com/darkyen/retinazer/Component.java) marker (empty) interface
    - Component type is the class implementing the `Component` interface, so component inheritance is not allowed
    - The most typical way of working with components is to create a new instance per entity, however you can also share single instance among multiple entities
        - Additionally, there is a component pooling support
    - Each entity can have at most one instance of each component type
- Systems are represented by subclasses of [`EntitySystem`](src/main/java/com/darkyen/retinazer/EntitySystem.java)
    - There are different pre-made subclasses to help with common tasks:
        - [`EntityProcessorSystem`](src/main/java/com/darkyen/retinazer/systems/EntityProcessorSystem.java) for iterating over all entities which have certain components
        - [`FamilyWatcherSystem`](src/main/java/com/darkyen/retinazer/systems/FamilyWatcherSystem.java) for detecting changes in entity sets
    - There is also a generalization of `EntitySystem`, the [`EngineService`](src/main/java/com/darkyen/retinazer/EngineService.java) which is useful for doing non-entity updates in certain parts of `Engine` update, or, as name suggests, to provide some service to other systems

Additionally, there are some concepts specific to this implementation:
- [`ComponentSet`](src/main/java/com/darkyen/retinazer/ComponentSet.java) is an immutable set of component types, which you have to create before you start using the `Engine`
- [`Family`](src/main/java/com/darkyen/retinazer/Family.java) describes a set of entities, based on the component types the entity has or does not have
- [`Mapper`](src/main/java/com/darkyen/retinazer/Mapper.java) provides access to the components and can be obtained from the `Engine`
- [`EntitySetView`](src/main/java/com/darkyen/retinazer/EntitySetView.java) is an immutable set of entities. You can obtain an automatically updated set of entities described by a `Family` through the `Engine`
- Wiring is a simple dependency injection system applied through `Engine.wire()` which fills all variables declared with the `@Wire` annotation with objects returned by appropriate `WireResolver`
    - You can use this system to inject whatever you like, but its main purpose is to inject instances of `Mapper`s, `EngineService`s and even `Engine` into the registered `EngineService`s

For more information about the various classes, see the [JavaDoc](https://jitpack.io/com/darkyen/retinazer/latest/javadoc/).

### Entity lifecycle
The entity management is setup to work in batches, which are triggered by a call to `Engine.flush()`.
This happens at the start of `Engine.update()` and after each `EngineService.update()` called within.
When you mark entity for removal, it won't be removed until the next `flush()`. Similarly for component removals.
Entity and component additions are instant, but `EntitySetView` membership update happens during the next `flush()`.
This is done to prevent errors stemming from using recently removed entities and components,
to make reasoning about change listeners easier and finally to improve performance by batching changes together.

Additionally, entity ID is guaranteed to not be reused during the very next update cycle. In other words,
if your update removes an entity, it is guaranteed that an entity with that ID will not exist during the next update.
Only during the update after that can a new entity be assigned that ID.

The ID reuse is important, because it allows the IDs to be small, which helps with performance and memory consumption.

### Example
```java
/** A simple component given to entities with position */
class Positioned implements Component {
	public int x, y;
}

/** Singleton component (tag component) given to entities that should fall. */
class Falling implements Component {
	public static final Falling INSTANCE = new Falling();
}

/** A system which moves all entities that should (and can) fall down. */
class GravitySystem extends EntityProcessorSystem {

    // This Mapper will be filled in automatically when the system is added to the engine
	@Wire
	private Mapper<Positioned> positioned;

	public GravitySystem() {
		super(Main.COMPONENT_DOMAIN.familyWith(Positioned.class, Falling.class));
	}

    // This is an EntityProcessorSystem, so this method will be called
    // once per update per entity which belongs to family specified in constructor
	@Override
	protected void process(int entity) {
		final Positioned positioned = this.positioned.get(entity);
		positioned.y -= 1;
	}
}

class Main {
    // A set of all used components
    public static final ComponentSet COMPONENT_DOMAIN = new ComponentSet(Positioned.class, Falling.class);

    public static void main() {
        // To create an Engine, specify the component domain and all systems/services to be used by the engine.
        // The order in which the systems are specified matches the update order.
        final Engine engine = new Engine(COMPONENT_DOMAIN, new GravitySystem()/*, ... */);
        // Instead of wiring, you can always get the Mappers manually
        final Mapper<Positioned> positioned = engine.getMapper(Positioned.class);
        final Mapper<Falling> falling = engine.getMapper(Falling.class);

        final int fallingEntity = engine.createEntity();
        positioned.add(fallingEntity, new Positioned());
        falling.add(fallingEntity, Falling.INSTANCE);

        final int staticEntity = engine.createEntity();
        positioned.add(staticEntity, new Positioned());

        for (int i = 0; i < 10; i++) {
            engine.update();
        }

        assertEquals(0, positioned.get(staticEntity).y);
        assertEquals(-10, positioned.get(fallingEntity).y);
    }
}
```  