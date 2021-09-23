package edu.kit.ifv.mobitopp.populationsynthesis;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.kit.ifv.mobitopp.data.ExampleZones;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneClassificationType;
import edu.kit.ifv.mobitopp.data.ZoneRepository;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.StandardMode;

@ExtendWith(MockitoExtension.class)
public class DistanceToTest {

	private static final double withinMargin = 1e-6d;
	private static final Mode mode = StandardMode.CAR;
	@Mock
	ZoneRepository zoneRepository;
	@Mock
	ImpedanceIfc impedance;
	private Zone inArea;

	@BeforeEach
	public void beforeEach() {
		inArea = ExampleZones.create().otherZone();
	}

	@Test
	void distanceInStudyArea() throws Exception {
		DistanceTo distanceTo = new DistanceTo(zoneRepository, impedance);

		double distance = distanceTo.studyAreaFrom(inArea, mode);

		assertThat(distance, is(closeTo(0.0d, withinMargin)));
		verifyZeroInteractions(zoneRepository, impedance);
	}

	@Test
	void distanceInExtendedStudyArea() throws Exception {
		float distanceToStudyArea = 1.0f;
		Zone inExtendedArea = ExampleZones.zoneClassification(ZoneClassificationType.extendedStudyArea);
		when(zoneRepository.getZones()).thenReturn(asList(inArea));
		when(impedance.getTravelTime(eq(inExtendedArea.getId()), eq(inArea.getId()), eq(mode), any()))
				.thenReturn(distanceToStudyArea);
		DistanceTo distanceTo = new DistanceTo(zoneRepository, impedance);

		double distance = distanceTo.studyAreaFrom(inExtendedArea, mode);

		assertThat(distance, is(closeTo(distanceToStudyArea, withinMargin)));
	}
}
