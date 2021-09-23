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
import edu.kit.ifv.mobitopp.simulation.modechoice.preference.gen.ModePreferenceHelper;
import edu.kit.ifv.mobitopp.simulation.modechoice.preference.gen.PersonChangerFunctionLogger;
import edu.kit.ifv.mobitopp.util.parameter.LogitParameters;

public class ModeChoicePreferencesChanger implements PersonChanger {

	private final ModePreferencesFuntions functions;
	private final PersonChangerFunctionLogger logger;
	private final DoubleSupplier random;

	public ModeChoicePreferencesChanger(
			final DoubleSupplier random, final LogitParameters logitParameters,
			final ModePreferenceHelper helper, final PersonChangerFunctionLogger logger) {
		super();
		this.functions = new ModePreferencesFuntions(logitParameters, helper, logger);
		this.logger = logger;
		this.random = random;
	}

	@Override
	public PersonBuilder attributesOf(PersonBuilder person) {
		ModeChoicePreferences newPreferences = new ModeChoicePreferences(alter(person));
		return person.setModeChoicePreferences(newPreferences);
	}

	private Map<Mode, Double> alter(PersonBuilder person) {
		HashMap<Mode, Double> draws = new LinkedHashMap<>();
		draws.put(StandardMode.PASSENGER, random.getAsDouble());
		draws.put(StandardMode.PEDESTRIAN, random.getAsDouble());
		draws.put(StandardMode.TAXI, random.getAsDouble());
		draws.put(StandardMode.CARSHARING_STATION, random.getAsDouble());
		draws.put(StandardMode.CARSHARING_FREE, random.getAsDouble());
		draws.put(StandardMode.BIKESHARING, random.getAsDouble());
		draws.put(StandardMode.RIDE_POOLING, random.getAsDouble());
		draws.put(StandardMode.CAR, random.getAsDouble());
		draws.put(StandardMode.BIKE, random.getAsDouble());
		draws.put(StandardMode.PUBLICTRANSPORT, random.getAsDouble());	
		
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
