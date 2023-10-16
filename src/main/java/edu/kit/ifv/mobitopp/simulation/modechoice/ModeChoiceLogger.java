package edu.kit.ifv.mobitopp.simulation.modechoice;

import java.util.*;
import java.util.stream.Collectors;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.result.Category;
import edu.kit.ifv.mobitopp.result.Results;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.StandardMode;
import edu.kit.ifv.mobitopp.simulation.modechoice.gen.DefaultModeChoiceModelLogger;
import edu.kit.ifv.mobitopp.simulation.modechoice.gen.ModeChoiceModelLogger;

public class ModeChoiceLogger implements ModeChoiceModelLogger {

	private final static Category modeAvailability = new Category("mode_availablity", columns());
	private final Results results;
	private final ModeResolver resolver;

	private static List<String> columns() {
		List<String> columns = new ArrayList<>(List.of("", ""));
		columns.addAll(
				Arrays.stream(StandardMode.values())
						.map(StandardMode::name)
						.collect(Collectors.toList())
		);
		return columns;
	}


	private final int n;
	private int count;
	private final DefaultModeChoiceModelLogger logger;

	public ModeChoiceLogger(int n, Results results, ModeResolver resolver) {
		this.n = n;
		this.results = results;
		this.resolver = resolver;
		this.count = -1;
		this.logger = new DefaultModeChoiceModelLogger();
	}

	@Override
	public void logStart(Person person, Zone origin, Zone destination) {
		if (n != 0) {
			count = (count + 1) % n;
		}
		if (count == 0) {
			logger.logStart(person, origin, destination);
		}
	}

	@Override
	public void logProbabilities(Map<String, Double> probabilities, Person person, Zone origin, Zone destination) {
		if (count == 0) {
			logAvailability(person, probabilities.keySet());
			logger.logProbabilities(probabilities, person, origin, destination);
		}
	}

	@Override
	public void log(Person person, Zone origin, Zone destination, String category, String key, double value) {
		if (count == 0) {
			logger.log(person, origin, destination, category, key, value);
		}
	}

	@Override
	public void log(Person person, Zone origin, Zone destination, String key, double value) {
		if (count == 0) {
			logger.log(person, origin, destination, key, value);
		}
	}

	private void logAvailability(Person person, Set<String> modes) {
		Set<Mode> availableModes = modes.stream().map(resolver::resolve).collect(Collectors.toSet());
		StringBuilder row = new StringBuilder();
		row.append(person.getOid()).append(";");
		row.append(person.currentActivity().calculatePlannedEndDate()).append(";");

		for (StandardMode mode : StandardMode.values()) {
			boolean isAvailable = availableModes.contains(mode);
			row.append((isAvailable) ? 1 : 0) ;
			row.append(";");
		}

		results.write(modeAvailability, row.toString());
	}

}
