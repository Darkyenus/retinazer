package com.github.antag99.benchmarks.ashley;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Setup;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;

public class RetrievalBenchmark extends AshleyBenchmark {
    private Engine engine;

    @Setup
    public void setup() {
        engine = new Engine();
        // @off
        engine.addSystem(new RetrievalSystem(ComponentA.class) {});
        engine.addSystem(new RetrievalSystem(ComponentB.class) {});
        engine.addSystem(new RetrievalSystem(ComponentC.class) {});
        engine.addSystem(new RetrievalSystem(ComponentD.class) {});
        engine.addSystem(new RetrievalSystem(ComponentE.class) {});
        engine.addSystem(new RetrievalSystem(ComponentF.class) {});
        engine.addSystem(new RetrievalSystem(ComponentG.class) {});
        engine.addSystem(new RetrievalSystem(ComponentH.class) {});
        // @on

        Class<? extends Component>[] componentTypes = getComponentTypes();
        for (int i = 0, n = getEntityCount(); i < n; ++i) {
            Entity entity = new Entity();
            // equivalent to mask = i % (2 ^ componentTypes.length)
            int mask = i & ((1 << componentTypes.length) - 1);
            for (int ii = 0, nn = componentTypes.length; ii < nn; ++ii) {
                if (((mask >> ii) & 1) == 1) {
                    entity.add(newInstance(componentTypes[ii]));
                }
            }
            engine.addEntity(entity);
        }

        engine.update(0f);
    }

    @Benchmark
    public void benchmarkRetrieval() {
        engine.update(0f);
    }
}