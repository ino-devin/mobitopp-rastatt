package edu.kit.ifv.mobitopp.populationsynthesis.fixeddestination;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.DoubleSupplier;

import edu.kit.ifv.mobitopp.data.PanelDataRepository;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.data.ZoneRepository;
import edu.kit.ifv.mobitopp.data.person.PersonId;
import edu.kit.ifv.mobitopp.populationsynthesis.PersonBuilder;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.FixedDestination;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.util.panel.PersonOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.PersonOfPanelDataId;
import edu.kit.ifv.mobitopp.util.parameter.LogitParameters;
import edu.kit.ifv.mobitopp.util.randomvariable.DiscreteRandomVariable;

public class OtherStudentsModel implements FixedDestinationModel {

	private final PanelDataRepository panelDataRepository;
	private final ZoneRepository zoneRepository;
	private final ImpedanceIfc impedance;
	private final DoubleSupplier random;
	private final double defaultRange;
	private final Map<ActivityType, Double> rangeMap;
	private final double a_distance;
	private final double b_distance;

	public OtherStudentsModel(PanelDataRepository panelDataRepository,
		ZoneRepository zoneRepository, ImpedanceIfc impedance, DoubleSupplier random,
		double defaultRange, LogitParameters logitParameters) {
		this.panelDataRepository = panelDataRepository;
		this.zoneRepository = zoneRepository;
		this.impedance = impedance;
		this.random = random;
		this.defaultRange = defaultRange;
		this.a_distance = logitParameters.get("a_distance");
		this.b_distance = logitParameters.get("b_distance");
		this.rangeMap = new LinkedHashMap<ActivityType, Double>();
	}

	public OtherStudentsModel(PanelDataRepository panelDataRepository,
		ZoneRepository zoneRepository, ImpedanceIfc impedance, DoubleSupplier random,
		double defaultRange, Map<ActivityType, Double> rangeMap, LogitParameters logitParameters) {
		this(panelDataRepository, zoneRepository, impedance, random, defaultRange, logitParameters);

		this.rangeMap.putAll(rangeMap);
	}

	@Override
	public FixedDestination selectFor(PersonBuilder person) {
		ZoneId homeZoneId = person.homeZone().getId();
		PersonId id = person.getId();
		PersonOfPanelData panelPerson = panelDataRepository
			.getPerson(PersonOfPanelDataId.fromPersonId(id));
		float distanceEducation = panelPerson.getDistanceEducation();
		ActivityType activityType = activityTypeFor(person);

		Map<Zone, Double> destinations = findPossibleDestinations(homeZoneId, distanceEducation,
			activityType);
		Zone destination = new DiscreteRandomVariable<>(destinations).realization(nextRandom());
		Location location = destination
			.opportunities()
			.selectRandomLocation(activityType, nextRandom());
		return new FixedDestination(activityType, destination, location);
	}

	private Map<Zone, Double> findPossibleDestinations(ZoneId homeZoneId, float distanceEducation,
		ActivityType activityType) {
		List<ZoneDistance> possibleDestinations = zonesWithLocationsFor(homeZoneId, activityType);
		List<ZoneDistance> inRange = findInRange(distanceEducation, rangeFor(activityType),
			possibleDestinations);

		if (inRange.isEmpty()) {
			return mapToAttractivity(activityType, possibleDestinations);
		}

		return mapToAttractivity(activityType, inRange);
	}

	private Map<Zone, Double> mapToAttractivity(ActivityType activityType,
		List<ZoneDistance> possibleDestinations) {
		return possibleDestinations
			.stream()
			.collect(
				toMap(z -> z.zone, zoneDistance -> calculateUtility(activityType, zoneDistance)));
	}

	private double calculateUtility(ActivityType activityType, ZoneDistance zoneDistance) {
		return Math.log(zoneDistance.zone.getAttractivity(activityType)+1)
			/ (b_distance * Math.pow(zoneDistance.distance, a_distance));
	}

	private double rangeFor(ActivityType activityType) {
		return rangeMap.getOrDefault(activityType, this.defaultRange);
	}

	private List<ZoneDistance> findInRange(float distanceEducation, double range,
		List<ZoneDistance> possibleDestinations) {
		return possibleDestinations
			.stream()
			.filter(d -> d.distance <= distanceEducation + range)
			.filter(d -> d.distance >= distanceEducation - range)
			.collect(toList());
	}

	private List<ZoneDistance> zonesWithLocationsFor(ZoneId homeZoneId, ActivityType activityType) {
		return zoneRepository
			.getZones()
			.stream()
			.filter(zone -> zone.isDestinationFor(activityType))
			.map(zone -> new ZoneDistance(zone, impedance.getDistance(homeZoneId, zone.getId())))
			.collect(toList());
	}

	private ActivityType activityTypeFor(PersonBuilder person) {
		if (person.hasActivityOfType(ActivityType.EDUCATION_SECONDARY)) {
			return ActivityType.EDUCATION_SECONDARY;
		}
		if (person.hasActivityOfType(ActivityType.EDUCATION_TERTIARY)) {
			return ActivityType.EDUCATION_TERTIARY;
		}
		return ActivityType.EDUCATION_SECONDARY;
	}

	private double nextRandom() {
		return random.getAsDouble();
	}

	private static final class ZoneDistance {

		private final Zone zone;
		private final float distance;

		public ZoneDistance(Zone zone, float distance) {
			super();
			this.zone = zone;
			this.distance = distance;
		}

	}

}
