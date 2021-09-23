package edu.kit.ifv.mobitopp.simulation.modechoice;

import java.util.Map;

import edu.kit.ifv.mobitopp.populationsynthesis.PersonBuilder;
import edu.kit.ifv.mobitopp.simulation.Mode;

public interface PersonChangerFunction {

	double calculateSensitivity(PersonBuilder person, Map<Mode, Double> draws);

}
