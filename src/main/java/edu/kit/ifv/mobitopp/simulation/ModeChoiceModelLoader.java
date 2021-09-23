package edu.kit.ifv.mobitopp.simulation;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import edu.kit.ifv.mobitopp.data.CostMatrix;
import edu.kit.ifv.mobitopp.data.TravelTimeMatrix;
import edu.kit.ifv.mobitopp.data.local.configuration.DynamicParameters;
import edu.kit.ifv.mobitopp.simulation.modeChoice.ModeAvailabilityModel;
import edu.kit.ifv.mobitopp.simulation.modeChoice.ModeChoiceModelUsingAvailableModes;
import edu.kit.ifv.mobitopp.simulation.modechoice.ModeChoiceHelper;
import edu.kit.ifv.mobitopp.simulation.modechoice.ModeResolver;
import edu.kit.ifv.mobitopp.simulation.modechoice.gen.GeneratedMixedModeChoice;
import edu.kit.ifv.mobitopp.simulation.modechoice.gen.MixedModeChoiceHelper;
import edu.kit.ifv.mobitopp.simulation.modechoice.gen.ModeChoiceModelLogger;
import edu.kit.ifv.mobitopp.simulation.person.DefaultTripFactory;
import edu.kit.ifv.mobitopp.simulation.person.ModeToTrip;
import edu.kit.ifv.mobitopp.simulation.person.TripFactory;
import edu.kit.ifv.mobitopp.simulation.tour.TourBasedModeChoiceModel;
import edu.kit.ifv.mobitopp.simulation.tour.TourBasedModeChoiceModelDummy;
import edu.kit.ifv.mobitopp.util.parameter.LogitParameters;
import edu.kit.ifv.mobitopp.util.parameter.ParameterFormularParser;
import edu.kit.ifv.mobitopp.visum.VisumMatrixParser;

public class ModeChoiceModelLoader {

	private final ModeAvailabilityModel modeAvailabilityModel;
	private final SimulationContext context;
	private final ImpedanceIfc impedance;
	private final ModeChoiceModelLogger loggerMain;



	public ModeChoiceModelLoader(final ModeAvailabilityModel modeAvailabilityModel,
		final SimulationContext context,
		final ImpedanceIfc impedance, 
		final ModeChoiceModelLogger loggerMain) {
		this.modeAvailabilityModel = modeAvailabilityModel;
		this.context = context;
		this.impedance = impedance;
		this.loggerMain = loggerMain;
	}

	private SimulationContext context() {
		return context;
	}

	private DynamicParameters modeChoiceParameters() {
		return context().modeChoiceParameters();
	}

	public TourBasedModeChoiceModel loadFrom() {
		GeneratedMixedModeChoice mainModeChoiceModel = loadMainModel(modeAvailabilityModel,
			impedance);
		TourBasedModeChoiceModel tourBasedWrapper = new TourBasedModeChoiceModelDummy(
			mainModeChoiceModel);
		return new ModeChoiceModelUsingAvailableModes(modeAvailabilityModel, tourBasedWrapper,
			context().results());
	}

	private GeneratedMixedModeChoice loadMainModel(
		final ModeAvailabilityModel modeAvailabilityModel, final ImpedanceIfc impedance) {
		MixedModeChoiceHelper helper = loadModeChoiceHelper(modeAvailabilityModel, impedance);
		LogitParameters utilityParameters = loadParameters("main");

		return new GeneratedMixedModeChoice(utilityParameters, helper, loggerMain);
	}

	public ModeChoiceHelper loadModeChoiceHelper(
		final ModeAvailabilityModel modeAvailabilityModel, final ImpedanceIfc impedance) {

		
		ModeResolver modeResolver = createModeResolver();
		DynamicParameters modeChoiceParameters = modeChoiceParameters();
		return new ModeChoiceHelper(modeAvailabilityModel, impedance, modeResolver);
	}


	private LogitParameters loadParameters(String parameter) {
		System.out.println("Loading logit parameters for: " + parameter);
		File parameterFile = modeChoiceParameters().valueAsFile(parameter);
		LogitParameters parameters = new ParameterFormularParser().parseToParameter(parameterFile);
		return addLambdaRoot(parameters);
	}

	private LogitParameters addLambdaRoot(LogitParameters parameters) {
		Map<String, Double> map = new LinkedHashMap<>(parameters.toMap());
		map.put("lambda_root", 1.0d);
		return new LogitParameters(map);
	}

	private ModeResolver createModeResolver() {
		HashMap<String, Mode> mapping = new HashMap<>();
		mapping.put("Bike", StandardMode.BIKE);
		mapping.put("BikeSharing", StandardMode.BIKESHARING);
		mapping.put("Car", StandardMode.CAR);
		mapping.put("Carsharing_FreeFloating", StandardMode.CARSHARING_FREE);
		mapping.put("Carsharing_Station", StandardMode.CARSHARING_STATION);
		mapping.put("Passenger", StandardMode.PASSENGER);
		mapping.put("Pedestrian", StandardMode.PEDESTRIAN);
		mapping.put("PublicTransport", StandardMode.PUBLICTRANSPORT);
		mapping.put("RidePooling", StandardMode.RIDE_POOLING);
		mapping.put("Taxi", StandardMode.TAXI);

		HashMap<Mode, String> reverseMapping = new HashMap<>();
		mapping.keySet().forEach(s -> reverseMapping.put(mapping.get(s), s));

		return new ModeResolver() {

			@Override
			public Mode resolve(String mode) {
				if (mapping.containsKey(mode)) {
					return mapping.get(mode);
				}
				throw new IllegalArgumentException(mode + " cannot be resolved to a mode.");
			}

			@Override
			public String getString(Mode mode) {
				if (reverseMapping.containsKey(mode)) {
					return reverseMapping.get(mode);
				}
				throw new IllegalArgumentException(mode + " has no associated string.");
			}

		};
	}

	private static TravelTimeMatrix loadTravelTimeMatrix(DynamicParameters modeChoiceParameters,
		String matrixFile) {
		File accessFile = modeChoiceParameters.valueAsFile(matrixFile);
		try {
			return VisumMatrixParser.load(accessFile).parseTravelTimeMatrix();
		} catch (IOException cause) {
			throw new UncheckedIOException(cause);
		}
	}

	private CostMatrix loadCostMatrix(DynamicParameters modeChoiceParameters, String matrixFile) {
		File accessFile = modeChoiceParameters.valueAsFile(matrixFile);
		try {
			return VisumMatrixParser.load(accessFile).parseCostMatrix();
		} catch (IOException cause) {
			throw new UncheckedIOException(cause);
		}
	}
	
	public TripFactory createTripFactory() {
		ModeToTrip modeToTrip = ModeToTrip.createDefault();
		DefaultTripFactory base = new DefaultTripFactory(modeToTrip);
		return base;
	}

}
