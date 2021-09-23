package edu.kit.ifv.mobitopp.simulation.destinationchoice;

import static edu.kit.ifv.mobitopp.util.collections.StreamUtils.toSortedMap;
import static java.util.Comparator.comparing;
import static java.util.function.Function.identity;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import edu.kit.ifv.mobitopp.concurrent.WaitingExecutor;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.destinationChoice.DestinationChoiceModel;
import edu.kit.ifv.mobitopp.simulation.destinationchoice.generated.DestinationChoiceUtilityFunction;
import edu.kit.ifv.mobitopp.simulation.destinationchoice.generated.DestinationChoiceUtilityModelLogger;
import edu.kit.ifv.mobitopp.util.logit.DefaultLogitModel;

public class DestinationChoicePerActivity implements DestinationChoiceModel {

	private final Map<ZoneId, Zone> zones;
	private final WaitingExecutor executor;
	private final DestinationChoiceUtilityFunction utilityFunction;
	private final DefaultLogitModel<Zone> logitModel;
	private final DestinationChoiceUtilityModelLogger logger;

	public DestinationChoicePerActivity(Map<ZoneId, Zone> zones, DestinationChoiceUtilityFunction utilityFunction,
			WaitingExecutor executor, DestinationChoiceUtilityModelLogger logger) {
		super();
		this.zones = zones;
		this.executor = executor;
		this.utilityFunction = utilityFunction;
		this.logger = logger;
		logitModel = new DefaultLogitModel<>();

	}

	@Override
	public Zone selectDestination(Person person, Optional<Mode> tourMode, ActivityIfc previousActivity,
			ActivityIfc nextActivity, double randomNumber) {
		logger.logStart(person, previousActivity.zone(), null, previousActivity, nextActivity,
				previousActivity.calculatePlannedEndDate(), randomNumber);
		Function<Zone, Double> calculateUtility = zone -> utilityFunction.calculateU_destination(zone, person,
				previousActivity.zone(), zone, previousActivity, nextActivity,
				previousActivity.calculatePlannedEndDate(), randomNumber);

		Map<Zone, Double> utilities = executor.execute(
				() -> zones.values().parallelStream().filter(z -> 0.0d < z.getAttractivity(nextActivity.activityType()))
						.collect(toSortedMap(identity(), calculateUtility, comparing(Zone::getId))));

		return logitModel.select(utilities, randomNumber);
	}

	@Override
	public boolean isTourBased() {
		return false;
	}

}
