package edu.kit.ifv.mobitopp.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinPool;

public class ParallelExecutor implements WaitingExecutor {

	private final ForkJoinPool pool;

	public ParallelExecutor(int parallelism) {
		pool = new ForkJoinPool(parallelism);
	}

	@Override
	public <T> T execute(Callable<T> task) {
		try {
			return pool.submit(task).get();
		} catch (Exception cause) {
			throw new RuntimeException(cause);
		}
	}

}
