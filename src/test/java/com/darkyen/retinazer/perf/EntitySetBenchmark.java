package com.darkyen.retinazer.perf;

import com.badlogic.gdx.utils.IntArray;
import com.darkyen.retinazer.EntitySet;
import jmh.mbr.junit5.Microbenchmark;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Warmup;

/**
 * Tests performance aspects of {@link EntitySet}.
 * <p>
 * Individual benchmarks are commented out, because they take a long time to run and are not usually needed.
 * (And no better way to disable them was found.)
 * Uncomment the @Benchmark annotations when you want to run them.
 */
@Microbenchmark
@Measurement(iterations = 2)
@Warmup(iterations = 2)
@Fork(2)
public class EntitySetBenchmark {

	private static int sumGetIndices(EntitySet entitySet) {
		IntArray indices = entitySet.getIndices();
		final int size = indices.size;
		final int[] items = indices.items;
		int sum = 0;
		for (int i = 0; i < size; i++) {
			sum += items[i];
		}
		return sum;
	}

	private static int sumForEach(EntitySet entitySet) {
		int[] sum = new int[1];
		entitySet.forEach((entity) -> sum[0] += entity);
		return sum[0];
	}

	private static final int ITERATIONS = 100_000;

	//@org.openjdk.jmh.annotations.Benchmark //Uncomment when relevant
	public int benchGetIndices() {
		EntitySet entitySet = new EntitySet();
		int sum = 0;
		for (int i = 0; i < ITERATIONS; i++) {
			entitySet.addEntity(i);
			sum += sumGetIndices(entitySet);
			sum += sumGetIndices(entitySet);
		}
		return sum;
	}

	//@org.openjdk.jmh.annotations.Benchmark // Uncomment when relevant
	public int benchLambda() {
		EntitySet entitySet = new EntitySet();
		int sum = 0;
		for (int i = 0; i < ITERATIONS; i++) {
			entitySet.addEntity(i);
			sum += sumForEach(entitySet);
			sum += sumForEach(entitySet);
		}
		return sum;
	}

}
