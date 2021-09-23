package edu.kit.ifv.mobitopp.populationsynthesis;

import java.util.function.Predicate;

import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.data.ZoneClassificationType;
import edu.kit.ifv.mobitopp.data.ZoneRepository;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.StandardMode;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;

public class AllowedHouseholdFilter {

	private final ZoneRepository zoneRepository;
	private final ImpedanceIfc impedance;
	private final SynthesisContext context;

	public AllowedHouseholdFilter(ZoneRepository zoneRepository, ImpedanceIfc impedance, SynthesisContext context) {
		super();
		this.zoneRepository = zoneRepository;
		this.impedance = impedance;
		this.context = context;
	}

	public Predicate<HouseholdOfPanelData> touchesStudyArea(DemandZone zone) {
		Predicate<HouseholdOfPanelData> filterByZoneClassification = h -> ZoneClassificationType.studyArea
				.equals(zone.zone().getClassification());
		if (generateExtendedArea()) {
			Predicate<HouseholdOfPanelData> filterByDistance = h -> distanceToStudyArea(zone,
					StandardMode.getTypeFromInt(h.getActivityRadiusMode())) <= h.getActivityRadius();
			Predicate<HouseholdOfPanelData> filterExtendedStudyArea = h -> ZoneClassificationType.extendedStudyArea
					.equals(zone.zone().getClassification());
			return filterByZoneClassification.or(filterExtendedStudyArea.and(filterByDistance));
		}
		return filterByZoneClassification;
	}

	private boolean generateExtendedArea() {
		return context.experimentalParameters().valueAsBoolean("generateExtendedArea");
	}

	private double distanceToStudyArea(DemandZone homeZone, Mode mode) {
		return new DistanceTo(zoneRepository, impedance).studyAreaFrom(homeZone.zone(), mode);
	}

}
