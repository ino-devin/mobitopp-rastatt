package edu.kit.ifv.mobitopp.simulation.modechoice;

import edu.kit.ifv.mobitopp.simulation.Mode;

public interface ModeResolver {

	Mode resolve(String mode);
	
	String getString(Mode mode);
}
