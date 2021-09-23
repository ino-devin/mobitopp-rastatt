package edu.kit.ifv.mobitopp.concurrent;

import java.util.concurrent.Callable;

public interface WaitingExecutor {
	<T> T execute(Callable<T> task);
}
