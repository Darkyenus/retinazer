package com.darkyen.retinazer;

/**
 * General {@link Engine} service.
 *
 * @see WireResolver
 * @see EntitySystem
 */
public interface EngineService {

	/**
	 * Called after the service is {@link Wire}d up.
	 * If overriding, <b>ALWAYS</b> call {@code super} first.
	 */
	default void initialize() {}

	/** Called on each {@link Engine#update}. */
	default void update() {}
}
