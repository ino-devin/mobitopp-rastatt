package edu.kit.ifv.mobitopp.populationsynthesis.commutationticket;

import static java.lang.Math.exp;

import java.util.Map;

import edu.kit.ifv.mobitopp.populationsynthesis.PersonBuilder;
import edu.kit.ifv.mobitopp.populationsynthesis.commutation.CommutationTicketModelOwnershipModel;
import edu.kit.ifv.mobitopp.result.Results;

public class GeneratedCommutationTicketOwnership implements CommutationTicketModelOwnershipModel {
	
	private final GeneratedCommutationTicketOwnershipUtilityFunction utilities;
	private final GeneratedCommutationTicketOwnershipHelper helper;
	private final CommutationTicketModelOwnershipModelLogger log;
	private final Results results;
	
	/**
	  * Instantiates a GeneratedCommutationTicketOwnership with the given GeneratedCommutationTicketOwnershipUtilityFunction, CommutationTicketModelOwnershipModelLogger and Results
	  */
	public GeneratedCommutationTicketOwnership(
			GeneratedCommutationTicketOwnershipUtilityFunction utilities,
			GeneratedCommutationTicketOwnershipHelper helper,
			CommutationTicketModelOwnershipModelLogger log, Results results) {
		super();
		this.utilities = utilities;
		this.helper = helper;
		this.log = log;
		this.results = results;
	}
	
	/**
	  * Convenience constuctor: Uses a NullCommutationTicketModelOwnershipModelLogger as logger and null as Results object.
	  */
	public GeneratedCommutationTicketOwnership(
			GeneratedCommutationTicketOwnershipUtilityFunction utilities,
			GeneratedCommutationTicketOwnershipHelper helper) {
		this(utilities, helper, new NullCommutationTicketModelOwnershipModelLogger(), null);
	}
	
	
	public boolean hasCommutationTicket(PersonBuilder person) {
		this.log.logStart(results, person);
		
		// Calculate single utilitiy/probability for non zero utility function
		double utility = utilities.calculateU_transit_pass(person);
		double probability = exp(utility) / (1.0 + exp(utility));
		
		Map<String, Double> probabilities = Map.of("Transit_pass", probability);
		this.log.logProbabilities(results, probabilities, person);
		
		double rand = helper.getRandom(person);
		String choice = (rand < probability) ? "Transit_pass" : "No_Transit_pass";
		return helper.resolve(choice);
	}
	
}
