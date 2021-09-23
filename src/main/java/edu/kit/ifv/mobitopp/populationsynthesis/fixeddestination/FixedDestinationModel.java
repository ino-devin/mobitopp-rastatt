package edu.kit.ifv.mobitopp.populationsynthesis.fixeddestination;

import edu.kit.ifv.mobitopp.populationsynthesis.PersonBuilder;
import edu.kit.ifv.mobitopp.simulation.FixedDestination;

public interface FixedDestinationModel {

	FixedDestination selectFor(PersonBuilder person);

}
