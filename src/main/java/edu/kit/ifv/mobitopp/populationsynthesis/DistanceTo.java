package edu.kit.ifv.mobitopp.populationsynthesis;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneClassificationType;
import edu.kit.ifv.mobitopp.data.ZoneRepository;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.StandardMode;
import edu.kit.ifv.mobitopp.time.Time;

public class DistanceTo {

	private static final Time time = Time.start.plusHours(7);
	private final ZoneRepository zoneRepository;
	private final ImpedanceIfc impedance;

	public DistanceTo(ZoneRepository zoneRepository, ImpedanceIfc impedance) {
		super();
		this.zoneRepository = zoneRepository;
		this.impedance = impedance;
	}

	public double studyAreaFrom(Zone zone, Mode mode) {
		if (ZoneClassificationType.studyArea.equals(zone.getClassification())) {
			return 0.0f;
		}
		return zoneRepository
				.getZones()
				.stream()
				.filter(z -> ZoneClassificationType.studyArea.equals(z.getClassification()))
				.map(Zone::getId)
				.mapToDouble(z -> impedance.getTravelTime(zone.getId(), z, fix(mode), time))
				.min()
				.orElseThrow(() -> new IllegalArgumentException(
						"Cannot calculate distance to study area, because the study area does not contain any zones."));
	}

	private Mode fix(Mode mode) {
		return StandardMode.UNKNOWN == mode || StandardMode.UNDEFINED == mode ? StandardMode.CAR : mode;
	}

}
