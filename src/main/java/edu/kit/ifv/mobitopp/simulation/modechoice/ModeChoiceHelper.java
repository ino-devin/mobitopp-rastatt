package edu.kit.ifv.mobitopp.simulation.modechoice;

import static edu.kit.ifv.mobitopp.simulation.StandardMode.BIKE;
import static edu.kit.ifv.mobitopp.simulation.StandardMode.CAR;
import static edu.kit.ifv.mobitopp.simulation.StandardMode.CARSHARING_FREE;
import static edu.kit.ifv.mobitopp.simulation.StandardMode.CARSHARING_STATION;
import static edu.kit.ifv.mobitopp.simulation.StandardMode.PASSENGER;
import static edu.kit.ifv.mobitopp.simulation.StandardMode.PEDESTRIAN;
import static edu.kit.ifv.mobitopp.simulation.StandardMode.PUBLICTRANSPORT;
import static edu.kit.ifv.mobitopp.time.DayOfWeek.SATURDAY;
import static edu.kit.ifv.mobitopp.time.DayOfWeek.SUNDAY;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import edu.kit.ifv.mobitopp.data.CostMatrix;
import edu.kit.ifv.mobitopp.data.TravelTimeMatrix;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.StandardMode;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.modeChoice.ModeAvailabilityModel;
import edu.kit.ifv.mobitopp.simulation.modechoice.gen.DefaultMixedModeChoiceHelper;
import edu.kit.ifv.mobitopp.time.DayOfWeek;

public final class ModeChoiceHelper extends DefaultMixedModeChoiceHelper implements ModeChoiceHelperForDestinationChoice {

	private final ModeAvailabilityModel modeAvailabilityModel;
	private final ImpedanceIfc impedance;
	private final ModeResolver resolver;

	public ModeChoiceHelper(final ModeAvailabilityModel modeAvailabilityModel,
		final ImpedanceIfc impedance, final ModeResolver resolver) {
		super(impedance);
		this.modeAvailabilityModel = modeAvailabilityModel;
		this.impedance = impedance;
		this.resolver = resolver;
	}

	@Override
	public Mode resolve(final String category) {
		return resolver.resolve(category);
	}

	@Override
	public boolean isAvailable(final String category, final Person person, final Zone origin,
		final Zone destination, final ActivityIfc previousActivity, final ActivityIfc nextActivity,
		final Set<Mode> choiceSet, final double randomNumber) {
		Mode mode = resolve(category);
		return modeAvailabilityModel
			.filterAvailableModes(person, origin, destination, previousActivity, nextActivity,
				choiceSet)
			.contains(mode) && choiceSet.contains(mode);
	}

	@Override
	public double getRandom(Person person, Zone origin, Zone destination,
		ActivityIfc previousActivity, final ActivityIfc nextActivity, final Set<Mode> choiceSet,
		final double randomNumber) {
		return randomNumber;
	}

	@Override
	public double getRELIEF(final String category, final Person person, final Zone origin,
		final Zone destination, final ActivityIfc previousActivity, final ActivityIfc nextActivity,
		final Set<Mode> choiceSet, final double randomNumber) {
		return Math.max(origin.getRelief(), destination.getRelief());
	}

	@Override
	public double getPARKING_STRESS(String category, Person person, Zone origin, Zone destination, ActivityIfc previousActivity, ActivityIfc nextActivity, Set<Mode> choiceSet, double randomNumber) {
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

//	@Override
//	public double getFZ_RP(final String category, final Person person, final Zone origin,
//		final Zone destination, final ActivityIfc previousActivity, final ActivityIfc nextActivity,
//		final Set<Mode> choiceSet, final double randomNumber) {
//		return getTravelTime(origin, destination, previousActivity, StandardMode.RIDE_POOLING);
//	}
//
//	@Override
//	public double getFZ_PUT(final String category, final Person person, final Zone origin,
//		final Zone destination, final ActivityIfc previousActivity, final ActivityIfc nextActivity,
//		final Set<Mode> choiceSet, final double randomNumber) {
//		Time currentTime = previousActivity.calculatePlannedEndDate();
//		BiFunction<ZoneId, ZoneId, Float> toTravelTime = (originId, destinationId) -> impedance
//			.getTravelTime(originId, destinationId, StandardMode.PUBLICTRANSPORT, currentTime);
//		ZoneAndLocation originLocation = new ZoneAndLocation(origin, origin.centroidLocation());
//		ZoneAndLocation destinationLocation = new ZoneAndLocation(destination,
//			destination.centroidLocation());
//		return builder
//			.build(person, originLocation, destinationLocation, impedance, currentTime)
//			.calculateWeightedMainTravelTime(person.modeChoicePreferences(), toTravelTime);
//	}

	private double getPreferenceFor(Mode mode, Person person) {
		return person.modeChoicePreferences().get(mode);
	}

	@Override
	public double getPREF_BIKE(final String category, final Person person, final Zone origin,
		final Zone destination, final ActivityIfc previousActivity, final ActivityIfc nextActivity,
		final Set<Mode> choiceSet, final double randomNumber) {
		return getPreferenceFor(StandardMode.BIKE, person);
	}

	@Override
	public double getPREF_BS(final String category, final Person person, final Zone origin,
		final Zone destination, final ActivityIfc previousActivity, final ActivityIfc nextActivity,
		final Set<Mode> choiceSet, final double randomNumber) {
		return getPreferenceFor(StandardMode.BIKESHARING, person);
	}

	@Override
	public double getPREF_CAR_P(final String category, final Person person, final Zone origin,
		final Zone destination, final ActivityIfc previousActivity, final ActivityIfc nextActivity,
		final Set<Mode> choiceSet, final double randomNumber) {
		return getPreferenceFor(StandardMode.PASSENGER, person);
	}

	@Override
	public double getPREF_CAR_D(final String category, final Person person, final Zone origin,
		final Zone destination, final ActivityIfc previousActivity, final ActivityIfc nextActivity,
		final Set<Mode> choiceSet, final double randomNumber) {
		return getPreferenceFor(StandardMode.CAR, person);
	}

	@Override
	public double getPREF_PUT(final String category, final Person person, final Zone origin,
		final Zone destination, final ActivityIfc previousActivity, final ActivityIfc nextActivity,
		final Set<Mode> choiceSet, final double randomNumber) {
		return getPreferenceFor(StandardMode.PUBLICTRANSPORT, person);
	}

	@Override
	public double getPREF_CS_FF(final String category, final Person person, final Zone origin,
		final Zone destination, final ActivityIfc previousActivity, final ActivityIfc nextActivity,
		final Set<Mode> choiceSet, final double randomNumber) {
		return getPreferenceFor(StandardMode.CARSHARING_FREE, person);
	}

	@Override
	public double getPREF_CS_SB(final String category, final Person person, final Zone origin,
		final Zone destination, final ActivityIfc previousActivity, final ActivityIfc nextActivity,
		final Set<Mode> choiceSet, final double randomNumber) {
		return getPreferenceFor(StandardMode.CARSHARING_STATION, person);
	}

	@Override
	public double getPREF_PED(final String category, final Person person, final Zone origin,
		final Zone destination, final ActivityIfc previousActivity, final ActivityIfc nextActivity,
		final Set<Mode> choiceSet, final double randomNumber) {
		return getPreferenceFor(StandardMode.PEDESTRIAN, person);
	}

	@Override
	public double getPREF_RP(final String category, final Person person, final Zone origin,
		final Zone destination, final ActivityIfc previousActivity, final ActivityIfc nextActivity,
		final Set<Mode> choiceSet, final double randomNumber) {
		return getPreferenceFor(StandardMode.RIDE_POOLING, person);
	}
	
	@Override
	public double getPREF_TAXI(String category, Person person, Zone origin, Zone destination,
			ActivityIfc previousActivity, ActivityIfc nextActivity, Set<Mode> choiceSet, double randomNumber) {
		return getPreferenceFor(StandardMode.TAXI, person);
	}

	@Override
	public double getLOGSUM_ACC_CS(final String category, final Person person, final Zone origin,
		final Zone destination, final ActivityIfc previousActivity, final ActivityIfc nextActivity,
		final Set<Mode> choiceSet, final double randomNumber) {
		return 0.0;
	}

	@Override
	public double getLOGSUM_ACC_PUT(final String category, final Person person, final Zone origin,
		final Zone destination, final ActivityIfc previousActivity, final ActivityIfc nextActivity,
		final Set<Mode> choiceSet, final double randomNumber) {
		return 0.0;
	}

	@Override
	public double getLOGSUM_EGR_PUT(final String category, final Person person, final Zone origin,
		final Zone destination, final ActivityIfc previousActivity, final ActivityIfc nextActivity,
		Set<Mode> choiceSet, double randomNumber) {
		return 0.0;
	}

	@Override
	public double getACCESS_TIME_RIDE_POOLING(String category, Person person, Zone origin, Zone destination,
			ActivityIfc previousActivity, ActivityIfc nextActivity, Set<Mode> choiceSet, double randomNumber) {
		return this.getACCESS_TIME_RIDEPOOLING(origin, destination);
	}
	
	@Override
	public double getACCESS_TIME_RIDEPOOLING(Zone origin, Zone destination) {
		return 0.0;
	}
	
	@Override
	public double getACCESS_TIME_PUBLICTRANSPORT(Zone origin, Zone destination) {
		return 0.0;
	}
	
	@Override
	public double getEGRESS_TIME_PUBLICTRANSPORT(Zone origin, Zone destination) {
		return 0.0;
	}
	
	@Override
	public double getB_TT_BIKE(String category, Person person, Zone origin, Zone destination,
			ActivityIfc previousActivity, ActivityIfc nextActivity, Set<Mode> choiceSet, double randomNumber) {
		return person.travelTimeSensitivity().get(BIKE);
	}
	
	@Override
	public double getB_TT_CAR_D(String category, Person person, Zone origin, Zone destination,
			ActivityIfc previousActivity, ActivityIfc nextActivity, Set<Mode> choiceSet, double randomNumber) {
		return person.travelTimeSensitivity().get(CAR);
	}

	@Override
	public double getB_TT_CAR_P(String category, Person person, Zone origin, Zone destination,
			ActivityIfc previousActivity, ActivityIfc nextActivity, Set<Mode> choiceSet, double randomNumber) {
		return person.travelTimeSensitivity().get(PASSENGER);
	}

	@Override
	public double getB_TT_CS_FF(String category, Person person, Zone origin, Zone destination,
			ActivityIfc previousActivity, ActivityIfc nextActivity, Set<Mode> choiceSet, double randomNumber) {
		return person.travelTimeSensitivity().get(CARSHARING_FREE);
	}

	@Override
	public double getB_TT_CS_SB(String category, Person person, Zone origin, Zone destination,
			ActivityIfc previousActivity, ActivityIfc nextActivity, Set<Mode> choiceSet, double randomNumber) {
		return person.travelTimeSensitivity().get(CARSHARING_STATION);
	}

	@Override
	public double getB_TT_PED(String category, Person person, Zone origin, Zone destination,
			ActivityIfc previousActivity, ActivityIfc nextActivity, Set<Mode> choiceSet, double randomNumber) {
		return person.travelTimeSensitivity().get(PEDESTRIAN);
	}

	@Override
	public double getB_TT_PUT(String category, Person person, Zone origin, Zone destination,
			ActivityIfc previousActivity, ActivityIfc nextActivity, Set<Mode> choiceSet, double randomNumber) {
		return person.travelTimeSensitivity().get(PUBLICTRANSPORT);
	}

	@Override
	public double getB_TT_RP(String category, Person person, Zone origin, Zone destination,
			ActivityIfc previousActivity, ActivityIfc nextActivity, Set<Mode> choiceSet, double randomNumber) {
		return person.travelTimeSensitivity().get(StandardMode.RIDE_POOLING);
	}

	@Override
	public double getB_TT_TAXI(String category, Person person, Zone origin, Zone destination,
			ActivityIfc previousActivity, ActivityIfc nextActivity, Set<Mode> choiceSet, double randomNumber) {
		return person.travelTimeSensitivity().get(StandardMode.TAXI);
	}
	

	@Override
	public double getB_TT_BS(String category, Person person, Zone origin, Zone destination,
			ActivityIfc previousActivity, ActivityIfc nextActivity, Set<Mode> choiceSet, double randomNumber) {
		return person.travelTimeSensitivity().get(StandardMode.BIKESHARING);
	}

	@Override
	public double getIS_WEEKDAY(String category, Person person, Zone origin, Zone destination,
			ActivityIfc previousActivity, ActivityIfc nextActivity, Set<Mode> choiceSet, double randomNumber) {
		DayOfWeek day = previousActivity.calculatePlannedEndDate().weekDay();
		return day != SUNDAY && day != SATURDAY ? 1.0d : 0.0d;
	}

	
	@Override
	public double getTRANSFER_PUBLICTRANSPORT(String category, Person person, Zone origin, Zone destination,
			ActivityIfc previousActivity, ActivityIfc nextActivity, Set<Mode> choiceSet, double randomNumber) {
		return 0.0;
	}
	
	@Override
	public double getWAITING_TIME_RIDE_POOLING(String category, Person person, Zone origin, Zone destination,
			ActivityIfc previousActivity, ActivityIfc nextActivity, Set<Mode> choiceSet, double randomNumber) {
		return 0.0;
	}
	
	@Override
	public Collection<String> getChoiceSet(Person person, Zone origin, Zone destination, ActivityIfc previousActivity,
			ActivityIfc nextActivity, Set<Mode> choiceSet, double randomNumber) {
		return choiceSet.stream().map(m -> resolver.getString(m)).collect(Collectors.toList());
	}

	@Override
	public double getNEXT_ACTIVITY_DURATION(String category, Person person, Zone origin, Zone destination,
			ActivityIfc previousActivity, ActivityIfc nextActivity, Set<Mode> choiceSet, double randomNumber) {
		return nextActivity.duration();
	}
	
	@Override
	public double getZONE_ID_DESTINATION(String category, Person person, Zone origin, Zone destination,
			ActivityIfc previousActivity, ActivityIfc nextActivity, Set<Mode> choiceSet, double randomNumber) {
		return Integer.parseInt(destination.getId().getExternalId());
	}


}