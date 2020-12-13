package com.darkyen.retinazer;

import com.darkyen.retinazer.systems.EntityProcessorSystem;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Runnable example from the README
 */
public class ReadmeExample {

	public static final ComponentSet COMPONENT_DOMAIN = new ComponentSet(Positioned.class, Falling.class);

	@Test
	public void readmeExample() {
		final Engine engine = new Engine(COMPONENT_DOMAIN, new GravitySystem());
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

class Positioned implements Component {
	public int y;
}

class Falling implements Component {
	public static final Falling INSTANCE = new Falling();
}

class GravitySystem extends EntityProcessorSystem {

	@Wire
	private Mapper<Positioned> positioned;

	public GravitySystem() {
		super(ReadmeExample.COMPONENT_DOMAIN.familyWith(Positioned.class, Falling.class));
	}

	@Override
	protected void process(int entity) {
		final Positioned positioned = this.positioned.get(entity);
		positioned.y -= 1;
	}
}