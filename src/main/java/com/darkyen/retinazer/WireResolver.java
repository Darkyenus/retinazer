package com.darkyen.retinazer;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

/**
 * WireResolver is used for wiring/un-wiring fields marked with {@link Wire}.
 *
 * @see Wire
 */
public interface WireResolver extends EngineService {

	/**
	 * Wires the field of the given object.
	 * Note that this will be called before {@link EngineService#initialize()}, because
	 * wiring is done before initialization.
	 *
	 * @param object The object to wire.
	 * @param field The field of the object.
	 * @return Whether this resolver handled the given field.
	 */
	boolean wire(@NotNull Object object, @NotNull Field field) throws IllegalAccessException;
}
