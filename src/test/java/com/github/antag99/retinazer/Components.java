package com.github.antag99.retinazer;

/**
 *
 */
public final class Components {

	public static final ComponentSet EMPTY_SET = new ComponentSet();
	public static final ComponentSet FULL_SET = new ComponentSet(FlagComponentA.class, FlagComponentB.class, FlagComponentC.class);

	public static final class FlagComponentA implements Component {
	}

	public static final class FlagComponentB implements Component {
	}

	public static final class FlagComponentC implements Component {
	}

	public static class FlagSystemA extends EntityProcessorSystem {
	    public FlagSystemA() {
	        super(Family.with(FlagComponentA.class));
	    }

	    @Override
	    public void process(int entity, float delta) {
	    }
	}

	public static class FlagSystemB extends EntityProcessorSystem {
	    public FlagSystemB() {
	        super(Family.with(FlagComponentB.class));
	    }

	    @Override
	    public void process(int entity, float delta) {
	    }
	}

	public static class FlagSystemC extends EntityProcessorSystem {
	    public FlagSystemC() {
	        super(Family.with(FlagComponentC.class));
	    }

	    @Override
	    public void process(int entity, float delta) {
	    }
	}
}
