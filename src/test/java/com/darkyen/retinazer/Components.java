package com.darkyen.retinazer;

import com.darkyen.retinazer.systems.EntityProcessorSystem;

/**
 *
 */
public final class Components {

	public static final ComponentSet FULL_SET = new ComponentSet(FlagComponentA.class, FlagComponentB.class, FlagComponentC.class);

	public static final class FlagComponentA implements Component {
	}

	public static final class FlagComponentB implements Component {
	}

	public static final class FlagComponentC implements Component {
	}

	public static class FlagSystemA extends EntityProcessorSystem {
	    public FlagSystemA() {
	        super(FULL_SET.familyWith(FlagComponentA.class));
	    }

	    @Override
	    public void process(int entity) {
	    }
	}

	public static class FlagSystemB extends EntityProcessorSystem {
	    public FlagSystemB() {
	        super(FULL_SET.familyWith(FlagComponentB.class));
	    }

	    @Override
	    public void process(int entity) {
	    }
	}

	public static class FlagSystemC extends EntityProcessorSystem {
	    public FlagSystemC() {
	        super(FULL_SET.familyWith(FlagComponentC.class));
	    }

	    @Override
	    public void process(int entity) {
	    }
	}
}
