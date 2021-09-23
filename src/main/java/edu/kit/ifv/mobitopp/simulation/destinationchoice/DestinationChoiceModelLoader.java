package edu.kit.ifv.mobitopp.simulation.destinationchoice;

import java.io.File;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

import edu.kit.ifv.mobitopp.concurrent.WaitingExecutor;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.SimulationContext;
import edu.kit.ifv.mobitopp.simulation.destinationChoice.DestinationChoiceModel;
import edu.kit.ifv.mobitopp.simulation.destinationchoice.generated.DestinationChoiceHelper;
import edu.kit.ifv.mobitopp.simulation.destinationchoice.generated.DestinationChoiceUtilityFunction;
import edu.kit.ifv.mobitopp.simulation.destinationchoice.generated.DestinationChoiceUtilityModelLogger;
import edu.kit.ifv.mobitopp.util.collections.StreamUtils;
import edu.kit.ifv.mobitopp.util.parameter.LogitParameters;
import edu.kit.ifv.mobitopp.util.parameter.ParameterFormularParser;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DestinationChoiceModelLoader {

	private final LogitParameters baseParameters;
	private final WaitingExecutor executor;
	private final DestinationChoiceHelper destinationHelper;
	private final DestinationChoiceUtilityModelLogger logger;

	public DestinationChoiceModelLoader(LogitParameters baseParameters, DestinationChoiceHelper destinationChoiceHelper,
			DestinationChoiceUtilityModelLogger logger, WaitingExecutor executor) {
		this.baseParameters = baseParameters;
		this.executor = executor;
		this.logger = logger;
		this.destinationHelper = destinationChoiceHelper;
	}

	public DestinationChoiceModel loadFrom(SimulationContext context) {
		Map<String, String> destinationChoiceFiles = context.configuration().getDestinationChoice();
		Map<ZoneId, Zone> zones = context.zoneRepository().zones().entrySet().stream()
				.filter(e -> e.getValue().isDestination())
				.collect(StreamUtils.toLinkedMap(Entry::getKey, Entry::getValue));

		return loadRegioMove(destinationChoiceFiles, zones);
	}

	private DestinationChoiceModel loadRegioMove(Map<String, String> destinationChoiceFiles, Map<ZoneId, Zone> zones) {
		Map<ActivityType, String> typeToFile = Map.of( //
				ActivityType.BUSINESS, "business", //
				ActivityType.LEISURE_INDOOR, "leisure", //
				ActivityType.LEISURE_OUTDOOR, "leisure", //
				ActivityType.PRIVATE_BUSINESS, "leisure", //
				ActivityType.PRIVATE_VISIT, "leisure", //
				ActivityType.SERVICE, "service", //
				ActivityType.SHOPPING_DAILY, "shopping", //
				ActivityType.SHOPPING_OTHER, "shopping" //
		);
		Function<Entry<ActivityType, String>, DestinationChoiceModel> loadModel = entry -> loadModel(entry,
				destinationChoiceFiles, zones);
		Map<ActivityType, DestinationChoiceModel> models = typeToFile.entrySet().stream()
				.collect(StreamUtils.toLinkedMap(Entry::getKey, loadModel));
		DestinationChoiceModel defaultModel = loadModel(destinationChoiceFiles, zones, "leisure");
		return new FixedLocationDestinationChoice(models, defaultModel);
	}

	private DestinationChoiceModel loadModel(Entry<ActivityType, String> entry,
			Map<String, String> destinationChoiceFiles, Map<ZoneId, Zone> zones) {
		return loadModel(destinationChoiceFiles, zones, entry.getValue());
	}

	private DestinationChoiceModel loadModel(Map<String, String> destinationChoiceFiles, Map<ZoneId, Zone> zones,
			String fileName) {
		log.info("Destination choice for " + fileName);
		LogitParameters parameters = loadParameters(destinationChoiceFiles, fileName);

		DestinationChoiceUtilityFunction utilityFunction = new DestinationChoiceUtilityFunction(parameters,
				destinationHelper, logger);

		return new DestinationChoicePerActivity(zones, utilityFunction, executor, logger);
	}

	private LogitParameters loadParameters(Map<String, String> destinationChoiceFiles, String fileName) {
		File parameterFile = new File(destinationChoiceFiles.get(fileName));
		LogitParameters parameters = new ParameterFormularParser().parseToParameter(parameterFile);
		return parameters.combineWith(baseParameters);
	}

}
