package edu.kit.ifv.mobitopp.simulation.modechoice;

import java.util.Map;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.modechoice.gen.DefaultModeChoiceModelLogger;
import edu.kit.ifv.mobitopp.simulation.modechoice.gen.ModeChoiceModelLogger;

public class ModeChoiceLogger implements ModeChoiceModelLogger {

	private final int n;
	private int count;
	private final DefaultModeChoiceModelLogger logger;

	public ModeChoiceLogger(int n) {
		this.n = n;
		this.count = -1;
		this.logger = new DefaultModeChoiceModelLogger();
	}

	@Override
	public void logStart(Zone origin, Zone destination) {
		if (n != 0) {
			count = (count + 1) % n;
		}
		if (count == 0) {
			logger.logStart(origin, destination);
		}
	}

	@Override
	public void logProbabilities(Map<String, Double> probabilities, Zone origin, Zone destination) {
		if (count == 0) {
			logger.logProbabilities(probabilities, origin, destination);
		}
	}

	@Override
	public void log(Zone origin, Zone destination, String category, String key, double value) {
		if (count == 0) {
			logger.log(origin, destination, category, key, value);
		}
	}

	@Override
	public void log(Zone origin, Zone destination, String key, double value) {
		if (count == 0) {
			logger.log(origin, destination, key, value);
		}
	}

}
