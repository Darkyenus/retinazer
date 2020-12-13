package com.darkyen.retinazer;

import org.jetbrains.annotations.NotNull;

/** Base class for system implementations. */
public abstract class EntitySystem implements EngineService {

	/** Engine instance this entity system is added to, for convenience. */
	@Wire
	protected Engine engine;

	private final Family        family;
	private       EntitySetView familyEntities;

	protected EntitySystem(@NotNull Family family) {
		this.family = family;
	}

	/** @return the family passed in the constructor */
	@NotNull
	public final Family getFamily() {
		return family;
	}

	/** @return set of entities which match the set
	 * @see Engine#getEntities(Family) precise semantics */
	@NotNull
	public final EntitySetView getEntities() {
		return familyEntities;
	}

	@Override
	public void initialize() {
		familyEntities = engine.getEntities(family);
	}
}
