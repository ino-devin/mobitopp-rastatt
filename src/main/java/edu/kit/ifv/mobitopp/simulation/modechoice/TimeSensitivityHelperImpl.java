package edu.kit.ifv.mobitopp.simulation.modechoice;

import java.util.Collection;
import java.util.Map;

import edu.kit.ifv.mobitopp.populationsynthesis.PersonBuilder;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.StandardMode;
import edu.kit.ifv.mobitopp.simulation.modechoice.sensitivity.gen.DefaultTimeSensitivityHelper;

public class TimeSensitivityHelperImpl extends DefaultTimeSensitivityHelper {

	@Override
	public double getRandom(PersonBuilder person, Map<Mode, Double> draws) {
		throw new UnsupportedOperationException("getRandom should never be called");
	}

	@Override
	public boolean isAvailable(String category, PersonBuilder person, Map<Mode, Double> draws) {
		throw new UnsupportedOperationException("isAvailable should never be called");
	}

	@Override
	public double resolve(String category) {
		throw new UnsupportedOperationException("resolve should never be called");
	}

	@Override
	public Collection<String> getChoiceSet(PersonBuilder person, Map<Mode, Double> draws) {
		throw new UnsupportedOperationException("getChoiceSet should never be called");
	}

	
	@Override
	public double getDRAW_TIME_BIKE(String category, PersonBuilder person, Map<Mode, Double> draws) {
		return draws.getOrDefault(StandardMode.BIKE, 0.0);
	}

	@Override
	public double getDRAW_TIME_CAR(String category, PersonBuilder person, Map<Mode, Double> draws) {
		return draws.getOrDefault(StandardMode.CAR, 0.0);
	}

	@Override
	public double getDRAW_TIME_PASSENGER(String category, PersonBuilder person, Map<Mode, Double> draws) {
		return draws.getOrDefault(StandardMode.PASSENGER, 0.0);
	}

	@Override
	public double getDRAW_TIME_PUBLICTRANSPORT(String category, PersonBuilder person, Map<Mode, Double> draws) {
		return draws.getOrDefault(StandardMode.PUBLICTRANSPORT, 0.0);
	}

}
