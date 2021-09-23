package edu.kit.ifv.mobitopp.populationsynthesis;

import java.util.Map;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneClassificationType;
import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.data.ZoneRepository;
import edu.kit.ifv.mobitopp.populationsynthesis.opportunities.OpportunityLocationSelector;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.Location;

public class FixedNumberOfLocationsForOuterZones implements OpportunityLocationSelector {

	private static final int fixedNumberOfOpportunities = 1;
	private final OpportunityLocationSelector other;
	private final ZoneRepository zoneRepository;
	
	public FixedNumberOfLocationsForOuterZones(final ZoneRepository zoneRepository, final OpportunityLocationSelector other) {
		this.other = other;
		this.zoneRepository = zoneRepository;
	}

	@Override
	public Map<Location, Integer> createLocations(
			final ZoneId zoneId, final ActivityType activityType, final Integer total_opportunities) {
		
		Zone zone = zoneRepository.getZones()
								  .stream()
								  .filter(z -> z.getId().equals(zoneId))
								  .findFirst().get();
		
		if (ZoneClassificationType.studyArea.equals(zone.getClassification())) {
			return other.createLocations(zoneId, activityType, total_opportunities);
		}
		return other.createLocations(zoneId, activityType, fixedNumberOfOpportunities);
	}

}
