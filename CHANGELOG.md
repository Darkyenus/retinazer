## Changelog

# Version 0.2.6
- Add `Engine.getServices` to retrieve service subclasses
- Made `FamilyWatcherSystem` (renamed from `FamilyPresenceWatcherSystem`) more open for extensions
- Lift `update` into `EngineService` - all services can now receive updates

# Version 0.2.5 (released 2020-01-21)
- `EngineService`s are now automatically wired and initialized
- Internal optimizations and simplifications

# Version 0.2.4 (released 2020-01-21)
- Update libGDX to 1.9.10
- Fix build

# Version 0.2.3 (released 2020-01-21)
- It is now required to supply all components at the `Engine` creation through `ComponentSet`
- `EngineConfig` has been deleted
- `Engine.getSystem` has been replaced with generic `getService`
- Renamed `FamilyConf` to `Family` and made it immutable. Old `Family` no longer exists because it served no purpose.
- Added `FamilyPresenceWatcherSystem`, replacing `EntityListener`
- `ensureCapacity(int)` for all `Bag` implementations
- Fix: `Bag.set(int)` no longer treats the `0` value specially

# Version 0.2.2 (released 2017-01-30)
- Add `Component` pooling support, just implement `Pooled` interface on your components
- Remove overly defensive behavior in some places, including altering of unnecessarily complex and defensive API
- Migrate build to `sbt` (no intention of using Scala)
- Remove support for GWT
- Rename `Priority` enum to `Order`, with more intuitive naming
- Simplify Wire system by removing `SkipWire` and requiring explicit `Wire` annotation on fields
    - Remove `unwire` functionality
- Add convenience classes
    - `SimpleWireResolver`
    - `EntityListenerAdapter`
- Greatly simplify `EntitySet` API, replacing it with modifiable `EntitySet` and non-modifiable (but not immutable) `EntitySetView`
- Inline `EntityProcessorSystem.processEntities`
- Lots of minor source cleanups

# Forked by Jan Polák

# Version 0.2.1 (released 2015-10-30)
- Fix: `OutOfMemoryError` due to never-reset inserted component list

# Version 0.2.0 (released 2015-10-30)
- Revised `Wire` API and semantics
  - `Wire` is inherited from superclass
  - An exception is always thrown when a field is not handled by a resolver
  - `Wire.Ignore` -> `SkipWire`
- `EntitySystem.setEngine(Engine)` is now `EntitySystem.setup()`
- `Engine.getEntitiesFor(FamilyConfig)` is now `Engine.getFamily(FamilyConfig).getEntities()`
- Reset method for `Engine` (`Engine.reset()`)
- Enhanced `EntitySetListener` API
  - Renamed to `EntityListener`
  - `inserted(IntArray)` -> `inserted(EntitySet)`
  - `removed(IntArray)` -> `removed(EntitySet)`
- `Engine.addEntityListener(EntityListener)`
- `Engine.removeEntityListener(EntityListener)`
- `Family.addListener(EntityListener)`
- `Family.removeListener(EntityListener)`
- Revised `Mask` API
  - `Mask.getWord(int)`
  - `Mask.getWordCount()`
  - `Mask.set(long[])`
  - `Mask.setWord(int, long)`
  - `Mask.getWords()`
  - `Mask.isEmpty()`
  - Removed 'Mask.push(int)'
  - Removed 'Mask.pop(int)'
- Revised `EntitySet` API
  - Modification delegated to `EntitySetEdit`
    - `EntitySet.addEntities(IntArray)` removed
    - `EntitySet.removeEntities(IntArray)` removed
    - `EntitySet.addEntities(Mask)` checks existing entities
    - `EntitySet.removeEntities(Mask)` checks existing entities
  - `EntitySet.edit()` returns `EntitySetEdit` or throws an exception if unmodifiable
  - `EntitySet.unmodifiable()` renamed to `EntitySet.view()`
  - `EntitySet.size()`
  - `EntitySet.isEmpty()`
  - Listeners are no longer supported, `EntityListener` is used for family events.
- `DependencyConfig` and `DependencyResolver` removed
- `Handle` removed
- `toString()` implementation for `EntitySet`
- `RetinazerException` as a general-purpose runtime exception
- Fix: EntitySet indices were invalidated even when there was no change
- Fix: `IndexOutOfBoundsException` when negative index is used for `Bag.get`
- Fix: No more global state, different engines running in different threads should work.

# Version 0.1.2 (released 2015-08-22)
- Made `Mask#getIndices(IntArray)` append elements to the array.
- Fixed issue where destroying an entity didn't remove it's components

# Version 0.1.1 (released 2015-08-21)
- Updated to libgdx 1.6.5

# Version 0.1.0 (released 2015-08-20)
- Initial release
