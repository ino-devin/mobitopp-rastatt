package edu.kit.ifv.mobitopp.simulation.modechoice;

import java.util.Collection;
import java.util.Map;

import edu.kit.ifv.mobitopp.populationsynthesis.PersonBuilder;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.StandardMode;
import edu.kit.ifv.mobitopp.simulation.modechoice.preference.gen.DefaultModePreferenceHelper;

public class ModePreferenceHelperImpl extends DefaultModePreferenceHelper {

	@Override
	public double getHAS_E_BIKE(String category, PersonBuilder person, Map<Mode, Double> draws) {
		return 0; //TODO
	}

	@Override
	public double getHAS_MEMBERSHIP_CS(String category, PersonBuilder person, Map<Mode, Double> draws) {
		return person.getMobilityProviderMembership().getOrDefault("Stadtmobil", false) ? 1.0d : 0.0d;
	}

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
	public double getDRAW_PREF_BIKE(String category, PersonBuilder person, Map<Mode, Double> draws) {
		return draws.getOrDefault(StandardMode.BIKE, 0.0);
	}

	@Override
	public double getDRAW_PREF_CAR(String category, PersonBuilder person, Map<Mode, Double> draws) {
		return draws.getOrDefault(StandardMode.CAR, 0.0);
	}

	@Override
	public double getDRAW_PREF_CS_SB(String category, PersonBuilder person, Map<Mode, Double> draws) {
		return draws.getOrDefault(StandardMode.CARSHARING_STATION, 0.0);
	}

	@Override
	public double getDRAW_PREF_PASSENGER(String category, PersonBuilder person, Map<Mode, Double> draws) {
		return draws.getOrDefault(StandardMode.PASSENGER, 0.0);
	}

	@Override
	public double getDRAW_PREF_PEDESTRIAN(String category, PersonBuilder person, Map<Mode, Double> draws) {
		return draws.getOrDefault(StandardMode.PEDESTRIAN, 0.0);
	}

	@Override
	public double getDRAW_PREF_PUBLICTRANSPORT(String category, PersonBuilder person, Map<Mode, Double> draws) {
		return draws.getOrDefault(StandardMode.PUBLICTRANSPORT, 0.0);
	}

}
