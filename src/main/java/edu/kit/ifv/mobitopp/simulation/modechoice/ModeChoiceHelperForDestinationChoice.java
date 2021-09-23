package edu.kit.ifv.mobitopp.simulation.modechoice;

import edu.kit.ifv.mobitopp.data.Zone;

public interface ModeChoiceHelperForDestinationChoice {

	public double getACCESS_TIME_PUBLICTRANSPORT(Zone origin, Zone destination);
	
	public double getEGRESS_TIME_PUBLICTRANSPORT(Zone origin, Zone destination);
	
	public double getACCESS_TIME_RIDEPOOLING(Zone origin, Zone destination);
}
