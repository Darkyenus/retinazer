package com.github.antag99.retinazer;

public class FlagSystemA extends EntityProcessorSystem {
    public FlagSystemA() {
        super(Family.with(FlagComponentA.class));
    }

    @Override
    public void process(int entity, float delta) {
    }
}
