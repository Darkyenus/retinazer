package com.darkyen.retinazer;

/**
 * Components are used as plain bags of data. As a matter of style, always
 * declare them {@code final}.
 */
public interface Component {
	/**
	 * These components are pooled by their Mapper and are reused.
	 * They MUST have no-arg constructor.
	 * <p>
	 * Pooled components may additionally implement {@link com.badlogic.gdx.utils.Pool.Poolable} for resetting.
	 */
	interface Pooled extends Component {
	}
}
