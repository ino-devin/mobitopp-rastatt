package edu.kit.ifv.mobitopp.populationsynthesis.mobilityprovider.carsharing;

import java.util.Map;
import java.util.function.Consumer;

import edu.kit.ifv.mobitopp.populationsynthesis.PersonBuilder;
import edu.kit.ifv.mobitopp.populationsynthesis.carownership.MobilityProviderCustomerModel;

public class LogitBasedCarSharingMembershipModel
		implements Consumer<PersonBuilder>, MobilityProviderCustomerModel {

	private final MobilityProviderCustomerModel generatedModel;

	public LogitBasedCarSharingMembershipModel(MobilityProviderCustomerModel generatedModel) {
		super();
		this.generatedModel = generatedModel;
	}

	@Override
	public void accept(PersonBuilder person) {
		boolean customership = estimateCustomership(person);
		person.setMobilityProviderMembership(Map.of("Stadtmobil", customership));
	}

	@Override
	public boolean estimateCustomership(PersonBuilder person) {
		return isAllowedToDrive(person) && generatedModel.estimateCustomership(person);
	}

	private boolean isAllowedToDrive(PersonBuilder person) {
		return 18 <= person.age() && person.hasDrivingLicense();
	}

}
