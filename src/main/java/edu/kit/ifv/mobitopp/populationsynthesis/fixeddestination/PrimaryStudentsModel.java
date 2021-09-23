package edu.kit.ifv.mobitopp.populationsynthesis.fixeddestination;

import java.util.function.DoubleSupplier;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.data.ZoneRepository;
import edu.kit.ifv.mobitopp.populationsynthesis.PersonBuilder;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.FixedDestination;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.Location;

public class PrimaryStudentsModel implements FixedDestinationModel {

	private final ZoneRepository zoneRepository;
	private final ImpedanceIfc impedance;
	private final DoubleSupplier random;

	public PrimaryStudentsModel(
			ZoneRepository zoneRepository, ImpedanceIfc impedance, DoubleSupplier random) {
		this.zoneRepository = zoneRepository;
		this.impedance = impedance;
		this.random = random;
	}

	@Override
	public FixedDestination selectFor(PersonBuilder person) {
		ZoneId homeId = person.homeZone().getId();
		float min = Float.MAX_VALUE;
		Zone fixedDestination = null;
		for (Zone destination : zoneRepository.zones().values()) {
			if (0 >= destination.getAttractivity(ActivityType.EDUCATION_PRIMARY)) {
				continue;
			}
			float distance = impedance.getDistance(homeId, destination.getId());
			if (distance < min) {
				min = distance;
				fixedDestination = destination;
			}
		}
		assert null != fixedDestination : "Could not find any destination for primary education.";
		Location location = fixedDestination
				.opportunities()
				.selectRandomLocation(ActivityType.EDUCATION_PRIMARY, nextRandom());
		return new FixedDestination(ActivityType.EDUCATION_PRIMARY, fixedDestination, location);
	}

	private double nextRandom() {
		return random.getAsDouble();
	}

}
