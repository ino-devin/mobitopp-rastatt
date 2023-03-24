package edu.kit.ifv.mobitopp.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import edu.kit.ifv.mobitopp.data.DemandRegion;
import edu.kit.ifv.mobitopp.populationsynthesis.HouseholdForSetup;
import edu.kit.ifv.mobitopp.populationsynthesis.region.HouseholdBasedStep;

public class ParallelHouseholdStep extends HouseholdBasedStep {
	
	private static ExecutorService executor;
	private static List<Callable<Void>> tasks = new ArrayList<>();
	
	public ParallelHouseholdStep(Consumer<HouseholdForSetup> consumer) {
		super(wrapConsumer(consumer));
	}
	
	public ParallelHouseholdStep(Consumer<HouseholdForSetup> consumer, int threads) {
		this(consumer);
		initExecutor(threads);
	}

	private static void initExecutor(int threads) {
		if (executor != null) {
			executor = Executors.newFixedThreadPool(threads);
		}		
	}
	
	private static ExecutorService getExecutor() {
		if (executor == null) {
			initExecutor(4);
		}
		
		return executor;
	}

	private static <T> Consumer<T> wrapConsumer(Consumer<T> consumer) {
		return 	p -> {
			tasks.add(() -> {consumer.accept(p); return null;});
		};
	}
	
	@Override
	public void process(DemandRegion region) {
		tasks.clear();
		
		super.process(region);
		
		try {
			getExecutor().invokeAll(tasks);
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		tasks.clear();
		
	}
	
	public static void shutDown() {
		if (executor != null && !executor.isShutdown()) {
			executor.shutdown();
		}
	}
}
