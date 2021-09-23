package edu.kit.ifv.mobitopp.populationsynthesis.commutationticket;

import java.util.function.Consumer;

import edu.kit.ifv.mobitopp.populationsynthesis.PersonBuilder;
import edu.kit.ifv.mobitopp.populationsynthesis.commutationticket.gen.GeneratedCommutationTicketOwnership;

public class LogitBasedCommutationTicketOwnershipModel implements Consumer<PersonBuilder> {

	private final GeneratedCommutationTicketOwnership generatedModel;

	public LogitBasedCommutationTicketOwnershipModel(GeneratedCommutationTicketOwnership generatedModel) {
		this.generatedModel = generatedModel;
	}

	@Override
	public void accept(PersonBuilder person) {
		boolean hasCommutationTicket = generatedModel.hasCommutationTicket(person);
		person.setHasCommuterTicket(hasCommutationTicket);
	}

}
