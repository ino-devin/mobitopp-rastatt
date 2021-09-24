package edu.kit.ifv.mobitopp.populationsynthesis.commutationticket;

import java.util.Map;

import edu.kit.ifv.mobitopp.populationsynthesis.PersonBuilder;
import edu.kit.ifv.mobitopp.result.Results;

public interface CommutationTicketModelOwnershipModelLogger {
	
	public void logStart(Results results, PersonBuilder person);
	
	public void logProbabilities(Results results, Map<String, Double> probabilities, PersonBuilder person);
	
}

