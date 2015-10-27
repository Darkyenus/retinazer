package com.github.antag99.benchmarks.retinazer;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Setup;

import com.github.antag99.retinazer.Engine;
import com.github.antag99.retinazer.EngineConfig;
import com.github.antag99.retinazer.Handle;

public class SingletonIterationBenchmark extends RetinazerBenchmark {
    private Engine engine;
    private Handle handle;

    @Setup
    public void setup() {
        engine = new Engine(new EngineConfig()
                .addSystem(new SingletonIterationSystem()));
        handle = engine.createHandle();
        for (int i = 0, n = getEntityCount(); i < n; i++) {
            Handle entity = handle.idx(engine.createEntity());
            if ((i % 8) == 0)
                entity.add(SingletonComponent.INSTANCE);
        }
    }

    @Benchmark
    public void benchmarkSingletonIteration() {
        engine.update();
    }
}
