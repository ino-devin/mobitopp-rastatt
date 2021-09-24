package edu.kit.ifv.mobitopp.populationsynthesis.commutationticket;

import java.util.Map;

import edu.kit.ifv.mobitopp.populationsynthesis.PersonBuilder;
import edu.kit.ifv.mobitopp.result.Results;

public class NullCommutationTicketModelOwnershipModelLogger implements CommutationTicketModelOwnershipModelLogger {
	
	@Override
	public void logStart(Results results, PersonBuilder person) {
		//Do Nothing, since this is a Null-Object Logger
	}
	
	@Override
	public void logProbabilities(Results results, Map<String, Double> probabilities, PersonBuilder person) {
		//Do Nothing, since this is a Null-Object Logger
	}
}

