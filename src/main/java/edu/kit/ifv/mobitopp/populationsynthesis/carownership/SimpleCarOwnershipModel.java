package edu.kit.ifv.mobitopp.populationsynthesis.carownership;

import java.util.Collection;
import java.util.function.IntFunction;

import edu.kit.ifv.mobitopp.populationsynthesis.HouseholdForSetup;
import edu.kit.ifv.mobitopp.populationsynthesis.PrivateCarForSetup;

public class SimpleCarOwnershipModel implements CarOwnershipModel {

	private final IntFunction<NumberOfCarsSelector> numberSelectors;
	private final GenericElectricCarOwnershipModel electricCarModel;

	public SimpleCarOwnershipModel(
			IntFunction<NumberOfCarsSelector> numberSelectors,
			GenericElectricCarOwnershipModel electricCarModel) {
		super();
		this.numberSelectors = numberSelectors;
		this.electricCarModel = electricCarModel;
	}

	@Override
	public Collection<PrivateCarForSetup> createCars(HouseholdForSetup household) {
		NumberOfCarsSelector numberSelector = numberSelectors
				.apply(household.homeZone().getRegionType().code());
		int numberOfCars = numberSelector.calculateNumberOfCarsFor(household);
		return electricCarModel.createCars(household, numberOfCars);
	}

}
