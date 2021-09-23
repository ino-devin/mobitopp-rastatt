package edu.kit.ifv.mobitopp.populationsynthesis;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneClassificationType;
import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.data.ZoneRepository;
import edu.kit.ifv.mobitopp.populationsynthesis.opportunities.OpportunityLocationSelector;
import edu.kit.ifv.mobitopp.simulation.ActivityType;

@ExtendWith(MockitoExtension.class)
public class FixedNumberOfLocationsForOuterZonesTest {

	private static final int opportunities = 5;
	private static final ActivityType activityType = ActivityType.WORK;
	@Mock
	OpportunityLocationSelector other;
	@Mock(lenient = true)
	Zone outerZone;
	@Mock(lenient = true)
	Zone innerZone;
	@Mock(lenient = true)
	ZoneRepository zoneRepository;
	private FixedNumberOfLocationsForOuterZones selector;

	@BeforeEach
	public void beforeEach() {
		when(outerZone.getId()).thenReturn(new ZoneId("outerZone", 0));
		when(innerZone.getId()).thenReturn(new ZoneId("innerZone", 1));
		when(outerZone.getClassification()).thenReturn(ZoneClassificationType.outlyingArea);
		when(innerZone.getClassification()).thenReturn(ZoneClassificationType.studyArea);
		when(zoneRepository.getZones()).thenReturn(List.of(outerZone, innerZone));
		selector = new FixedNumberOfLocationsForOuterZones(zoneRepository, other);
	}

	@Test
	void delegateToOther() throws Exception {
		selector.createLocations(innerZone.getId(), activityType, opportunities);

		verify(other).createLocations(innerZone.getId(), activityType, opportunities);
	}
	
	@Test
	void useFixedNumberForOuterZones() throws Exception {
		selector.createLocations(outerZone.getId(), activityType, opportunities);
		
		verify(other).createLocations(outerZone.getId(), activityType, 1);
	}

}
