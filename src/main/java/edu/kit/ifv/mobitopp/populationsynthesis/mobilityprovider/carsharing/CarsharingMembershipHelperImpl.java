package edu.kit.ifv.mobitopp.populationsynthesis.mobilityprovider.carsharing;

import java.util.Collection;
import java.util.List;
import java.util.function.DoubleSupplier;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.populationsynthesis.PersonBuilder;
import edu.kit.ifv.mobitopp.populationsynthesis.mobilityprovider.carsharing.generated.CarSharingMembershipHelper;
import edu.kit.ifv.mobitopp.populationsynthesis.mobilityprovider.carsharing.generated.DefaultCarSharingMembershipHelper;
import edu.kit.ifv.mobitopp.simulation.ActivityType;


public class CarsharingMembershipHelperImpl extends DefaultCarSharingMembershipHelper implements CarSharingMembershipHelper {

	private final DoubleSupplier random;

	public CarsharingMembershipHelperImpl(DoubleSupplier random) {
		super();
		this.random = random;
	}

	@Override
	public double getRandom(PersonBuilder person) {
		return random.getAsDouble();
	}

	public double getCS_DENSITY(String category, PersonBuilder person) {
		return person.homeZone().carSharing().carsharingcarDensity("Stadtmobil");
	}

	@Override
	public double getPARKEN(String category, PersonBuilder person) {
		Zone destination = person.homeZone();
		double numberOfInhabitants = destination
				.getDemandData()
				.getPopulationData()
				.getNumberOfInhabitants();
		double workPlaces = destination.getAttractivity(ActivityType.WORK);
		double numberOfParkingPlaces = destination.getNumberOfParkingPlaces();
		if (Double.isFinite(numberOfParkingPlaces) && 0.0d != numberOfParkingPlaces) {
			return (numberOfInhabitants + workPlaces) / numberOfParkingPlaces;
		}
		System.out.println("numberOfParkingPlaces is infinite or 0: " + destination.getId());
		return 0.0d;
	}

	@Override
	public boolean resolve(String category) {
		return "Cs_member".equals(category);
	}

	@Override
	public Collection<String> getChoiceSet(PersonBuilder person) {
		return List.of("Cs_member", "No_cs_member");
	}

}
