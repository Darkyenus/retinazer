package com.github.antag99.retinazer;

/** General {@link Engine} service.
 * @see WireResolver
 * @see EntitySystem */
public interface EngineService {

	/** Called after the service is {@link Wire}d up.
	 * If overriding, <b>ALWAYS</b> call {@code super} first. */
	default void initialize() {}

}
