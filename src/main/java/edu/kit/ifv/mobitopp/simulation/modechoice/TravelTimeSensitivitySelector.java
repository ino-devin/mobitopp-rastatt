package edu.kit.ifv.mobitopp.simulation.modechoice;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.DoubleSupplier;

import edu.kit.ifv.mobitopp.populationsynthesis.PersonBuilder;
import edu.kit.ifv.mobitopp.populationsynthesis.serialiser.PersonChanger;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.StandardMode;
import edu.kit.ifv.mobitopp.simulation.modeChoice.ModeChoicePreferences;
import edu.kit.ifv.mobitopp.simulation.modechoice.sensitivity.gen.PersonChangerFunctionLogger;
import edu.kit.ifv.mobitopp.simulation.modechoice.sensitivity.gen.TimeSensitivityHelper;
import edu.kit.ifv.mobitopp.util.parameter.LogitParameters;

public class TravelTimeSensitivitySelector implements PersonChanger {

	private final TravelTimeSensitivityFuntions functions;
	private final DoubleSupplier random;
	private  final PersonChangerFunctionLogger logger;

	public TravelTimeSensitivitySelector(
			final DoubleSupplier random, final LogitParameters logitParameters, TimeSensitivityHelper helper, PersonChangerFunctionLogger logger) {
		super();
		this.random = random;
		this.logger = logger;
		functions = new TravelTimeSensitivityFuntions(logitParameters, helper, logger);
	}

	@Override
	public PersonBuilder attributesOf(PersonBuilder person) {
		ModeChoicePreferences travelTimeSensitivity = new ModeChoicePreferences(
				alterSensitivity(person));
		return person.setTravelTimeSensitivity(travelTimeSensitivity);
	}

	private Map<Mode, Double> alterSensitivity(PersonBuilder person) {	
		HashMap<Mode, Double> draws = new LinkedHashMap<>();
		draws.put(StandardMode.PEDESTRIAN, random.getAsDouble());
		draws.put(StandardMode.TAXI, random.getAsDouble());
		draws.put(StandardMode.CARSHARING_STATION, random.getAsDouble());
		draws.put(StandardMode.CARSHARING_FREE, random.getAsDouble());
		draws.put(StandardMode.RIDE_POOLING, random.getAsDouble());
		draws.put(StandardMode.CAR, random.getAsDouble());
		draws.put(StandardMode.BIKE, random.getAsDouble());
		draws.put(StandardMode.PUBLICTRANSPORT, random.getAsDouble());
		
		draws.put(StandardMode.BIKESHARING, draws.get(StandardMode.BIKE));
		draws.put(StandardMode.PASSENGER, draws.get(StandardMode.CAR));
		
		logger.logStart(person);

		HashMap<Mode, Double> map = new LinkedHashMap<>(person.getModeChoicePreferences().asMap());
		map.put(StandardMode.PASSENGER, functions.calculateAsc_car_p(person, draws));
		map.put(StandardMode.PEDESTRIAN, functions.calculateAsc_ped(person, draws));
		map.put(StandardMode.TAXI, functions.calculateAsc_taxi(person, draws));
		map.put(StandardMode.CARSHARING_STATION, functions.calculateAsc_cs_sb(person, draws));
		map.put(StandardMode.CARSHARING_FREE, functions.calculateAsc_cs_ff(person, draws));
		map.put(StandardMode.BIKESHARING, functions.calculateAsc_bs(person, draws));
		map.put(StandardMode.RIDE_POOLING, functions.calculateAsc_rp(person, draws));
		map.put(StandardMode.CAR, functions.calculateAsc_car_d(person, draws));
		map.put(StandardMode.BIKE, functions.calculateAsc_bike(person, draws));
		map.put(StandardMode.PUBLICTRANSPORT, functions.calculateAsc_put(person, draws));
		return map;
	}

}
