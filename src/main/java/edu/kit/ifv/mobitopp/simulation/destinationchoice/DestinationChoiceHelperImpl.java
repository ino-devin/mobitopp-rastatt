package edu.kit.ifv.mobitopp.simulation.destinationchoice;

import java.util.Collection;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.StandardMode;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.destinationchoice.generated.DefaultDestinationChoiceHelper;
import edu.kit.ifv.mobitopp.simulation.modeChoice.ModeAvailabilityModel;
import edu.kit.ifv.mobitopp.simulation.modechoice.ModeChoiceHelperForDestinationChoice;
import edu.kit.ifv.mobitopp.time.Time;

public class DestinationChoiceHelperImpl extends DefaultDestinationChoiceHelper {

	private final AvailableModesCache availableModesCache;
	private final ImpedanceIfc impedance;
	private final ModeChoiceHelperForDestinationChoice helper;

	public DestinationChoiceHelperImpl(ImpedanceIfc impedance, ModeAvailabilityModel modeAvailabilityModel,
			Collection<Mode> choiceSet, ModeChoiceHelperForDestinationChoice helper) {
		super(impedance);
		this.impedance = impedance;
		this.availableModesCache = new AvailableModesCache(modeAvailabilityModel, choiceSet);
		this.helper = helper;
	}

	private double isModeAvailable(Person person, Zone origin, Zone destination, Mode mode) {
		return toDouble(this.availableModesCache.get(person, origin).contains(mode));
	}

	private double toDouble(boolean condition) {
		return condition ? 1.0d : 1e-6d;
	}

	private long adultsInHousehold(Person person) {
		return person.household().persons().filter(p -> 18 <= p.age()).count();
	}

	private double getTravelCostToFixed(Person person, Zone destination, Time time, Mode mode) {
		ZoneId nextFixedZone = nextFixedZone(person, person.currentActivity()).getId();
		try {
			float travelCost = impedance.getTravelCost(destination.getId(), nextFixedZone, mode, time);
			return 1000.0f > travelCost ? travelCost : 999.0f;
		} catch (AssertionError error) {
			throw new RuntimeException(
					String.format("Could not determine travel cost from %s to %s.", destination.getId(), nextFixedZone),
					error);
		}
	}

	private double getTravelTimeToFixed(Person person, Zone destination, Time time, Mode mode) {
		ZoneId nextFixedZone = nextFixedZone(person, person.currentActivity()).getId();
		try {
			float travelTime = impedance.getTravelTime(destination.getId(), nextFixedZone, mode, time);
			return 1000.0f > travelTime ? travelTime : 999.0f;
		} catch (AssertionError error) {
			throw new RuntimeException(
					String.format("Could not determine travel time from %s to %s.", destination.getId(), nextFixedZone),
					error);
		}
	}
	
	private Zone nextFixedZone(Person person, ActivityIfc activity) {
		return person.nextFixedActivityZone(activity);
	}

	@Override
	public double getRandom(Person person, Zone origin, Zone destination, ActivityIfc previousActivity,
			ActivityIfc nextActivity, Time time, double randomNumber) {
		throw new UnsupportedOperationException(
				"getRandom should not be called. Only utility functions should be used.");
	}

	@Override
	public boolean isAvailable(String category, Person person, Zone origin, Zone destination,
			ActivityIfc previousActivity, ActivityIfc nextActivity, Time time, double randomNumber) {
		throw new UnsupportedOperationException(
				"isAvailable should not be called. Only utility functions should be used.");
	}

	@Override
	public Zone resolve(String category) {
		throw new UnsupportedOperationException("resolve should not be called. Only utility functions should be used.");
	}

	@Override
	public Collection<Zone> getChoiceSet(Person person, Zone origin, Zone destination, ActivityIfc previousActivity,
			ActivityIfc nextActivity, Time time, double randomNumber) {
		throw new UnsupportedOperationException(
				"getChoiceSet should not be called. Only utility functions should be used.");
	}

	@Override
	public double getATTRACTIVITY(Zone category, Person person, Zone origin, Zone destination,
			ActivityIfc previousActivity, ActivityIfc nextActivity, Time time, double randomNumber) {
		return destination.getAttractivity(nextActivity.activityType());
	}

	@Override
	public double getIS_INTRAZONAL(Zone category, Person person, Zone origin, Zone destination,
			ActivityIfc previousActivity, ActivityIfc nextActivity, Time time, double randomNumber) {
		return (origin.equals(destination)) ? 1.0 : 0.0;
	}
/*
	@Override
	public double getIS_UMLAND(Zone category, Person person, Zone origin, Zone destination,
			ActivityIfc previousActivity, ActivityIfc nextActivity, Time time, double randomNumber) {
		return (Integer.parseInt(category.getId().getExternalId()) > 9000) ? 1.0 : 0.0;
	}
*/
	@Override
	public double getCARS_PER_ADULT(Zone category, Person person, Zone origin, Zone destination,
			ActivityIfc previousActivity, ActivityIfc nextActivity, Time time, double randomNumber) {
		double adults = (adultsInHousehold(person) > 0 ? adultsInHousehold(person) : 1.0d);
		return person.household().getNumberOfOwnedCars() / adults;
	}
	
	@Override
	public double getAVAIL_BS(Zone category, Person person, Zone origin, Zone destination, ActivityIfc previousActivity,
			ActivityIfc nextActivity, Time time, double randomNumber) {
		return isModeAvailable(person, origin, destination, StandardMode.BIKESHARING);
	}

	@Override
	public double getAVAIL_FUSS(Zone category, Person person, Zone origin, Zone destination, 
			ActivityIfc previousActivity, ActivityIfc nextActivity, Time time, double randomNumber) {
		return isModeAvailable(person, origin, destination, StandardMode.PEDESTRIAN);
	}

	@Override
	public double getAVAIL_OEV(Zone category, Person person, Zone origin, Zone destination,
			ActivityIfc previousActivity, ActivityIfc nextActivity, Time time, double randomNumber) {
		return isModeAvailable(person, origin, destination, StandardMode.PUBLICTRANSPORT);
	}

	@Override
	public double getAVAIL_RAD(Zone category, Person person, Zone origin, Zone destination,
			ActivityIfc previousActivity, ActivityIfc nextActivity, Time time, double randomNumber) {
		return isModeAvailable(person, origin, destination, StandardMode.BIKE);
	}
/*
	@Override
	public double getAVAIL_CSFF(Zone category, Person person, Zone origin, Zone destination,
			ActivityIfc previousActivity, ActivityIfc nextActivity, Time time, double randomNumber) {
		return isModeAvailable(person, origin, destination, StandardMode.CARSHARING_FREE);
	}

	@Override
	public double getAVAIL_CSSB(Zone category, Person person, Zone origin, Zone destination,
			ActivityIfc previousActivity, ActivityIfc nextActivity, Time time, double randomNumber) {
		return isModeAvailable(person, origin, destination, StandardMode.CARSHARING_STATION);
	}
*/
	@Override
	public double getAVAIL_MF(Zone category, Person person, Zone origin, Zone destination, ActivityIfc previousActivity,
			ActivityIfc nextActivity, Time time, double randomNumber) {
		return isModeAvailable(person, origin, destination, StandardMode.PASSENGER);
	}

	@Override
	public double getAVAIL_PKW(Zone category, Person person, Zone origin, Zone destination,
			ActivityIfc previousActivity, ActivityIfc nextActivity, Time time, double randomNumber) {
		return isModeAvailable(person, origin, destination, StandardMode.CAR);
	}
/*
	@Override
	public double getLOGSUM_ACC_PUT(Zone category, Person person, Zone origin, Zone destination,
			ActivityIfc previousActivity, ActivityIfc nextActivity, Time time, double randomNumber) {
		return helper.getLOGSUM_ACC_PUT("", person, origin, destination, previousActivity, nextActivity, emptySet(),
				0.42d);
	}

	@Override
	public double getLOGSUM_EGR_PUT(Zone category, Person person, Zone origin, Zone destination,
			ActivityIfc previousActivity, ActivityIfc nextActivity, Time time, double randomNumber) {
		return helper.getLOGSUM_EGR_PUT("", person, origin, destination, previousActivity, nextActivity,emptySet(),
				0.42d);
	}


	@Override
	public double getLOGSUM_ACC_PUT_FIX(Zone category, Person person, Zone origin, Zone destination,
			ActivityIfc previousActivity, ActivityIfc nextActivity, Time time, double randomNumber) {
		Zone nextFixedZone = nextFixedZone(person, person.currentActivity());
		return helper.getLOGSUM_ACC_PUT("", person, destination, nextFixedZone, previousActivity, nextActivity, emptySet(),
				0.42d);
	}

	@Override
	public double getLOGSUM_EGR_PUT_FIX(Zone category, Person person, Zone origin, Zone destination,
			ActivityIfc previousActivity, ActivityIfc nextActivity, Time time, double randomNumber) {
		Zone nextFixedZone = nextFixedZone(person, person.currentActivity());
		return helper.getLOGSUM_EGR_PUT("", person, destination, nextFixedZone, previousActivity, nextActivity, emptySet(),
				0.42d);
	}

	@Override
	public double getLOGSUM_CS_SB(Zone category, Person person, Zone origin, Zone destination,
			ActivityIfc previousActivity, ActivityIfc nextActivity, Time time, double randomNumber) {
		return helper.getLOGSUM_ACC_CS("", person, origin, destination, previousActivity, nextActivity,emptySet(),
				0.42d);
	}

	
	
*/
		
	//@Override
	public double getACCESS_TIME_PUBLICTRANSPORT(Zone category, Person person, Zone origin,
		Zone destination, ActivityIfc previousActivity, ActivityIfc nextActivity, Time time, double randomNumber) {
		return helper.getACCESS_TIME_PUBLICTRANSPORT(origin, destination);
	}

	//@Override
	public double getEGRESS_TIME_PUBLICTRANSPORT(Zone category, Person person, Zone origin,
		Zone destination, ActivityIfc previousActivity, ActivityIfc nextActivity, Time time, double randomNumber) {
		return helper.getEGRESS_TIME_PUBLICTRANSPORT(origin, destination);
	}
	
	//@Override
	public double getACCESS_TIME_RIDEPOOLING(Zone category, Person person, Zone origin,
			Zone destination, ActivityIfc previousActivity, ActivityIfc nextActivity, Time time, double randomNumber) {
			return helper.getACCESS_TIME_RIDEPOOLING(origin, destination);
	}
	
	@Override
	public double getTRAVEL_COST_BIKESHARING_FIX(Zone category, Person person, Zone origin, Zone destination,
			ActivityIfc previousActivity, ActivityIfc nextActivity, Time time, double randomNumber) {
		return getTravelCostToFixed(person, destination, time, StandardMode.BIKESHARING);
	}

	@Override
	public double getTRAVEL_COST_CAR_FIX(Zone category, Person person, Zone origin, Zone destination,
			ActivityIfc previousActivity, ActivityIfc nextActivity, Time time, double randomNumber) {
		return getTravelCostToFixed(person, destination, time, StandardMode.CAR);
	}
/*
	@Override
	public double getTRAVEL_COST_CS_FF_FIX(Zone category, Person person, Zone origin, Zone destination,
			ActivityIfc previousActivity, ActivityIfc nextActivity, Time time, double randomNumber) {
		return getTravelCostToFixed(person, destination, time, StandardMode.CARSHARING_FREE);
	}

	@Override
	public double getTRAVEL_COST_CS_SB_FIX(Zone category, Person person, Zone origin, Zone destination,
			ActivityIfc previousActivity, ActivityIfc nextActivity, Time time, double randomNumber) {
		return getTravelCostToFixed(person, destination, time, StandardMode.CARSHARING_STATION);
	}
*/
	@Override
	public double getTRAVEL_COST_PUBLICTRANSPORT_FIX(Zone category, Person person, Zone origin, Zone destination,
			ActivityIfc previousActivity, ActivityIfc nextActivity, Time time, double randomNumber) {
		return getTravelCostToFixed(person, destination, time, StandardMode.PUBLICTRANSPORT);
	}

	@Override
	public double getTRAVEL_TIME_BIKE_FIX(Zone category, Person person, Zone origin, Zone destination,
			ActivityIfc previousActivity, ActivityIfc nextActivity, Time time, double randomNumber) {
		return getTravelTimeToFixed(person, destination, time, StandardMode.BIKE);
	}

	@Override
	public double getTRAVEL_TIME_CAR_FIX(Zone category, Person person, Zone origin, Zone destination,
			ActivityIfc previousActivity, ActivityIfc nextActivity, Time time, double randomNumber) {
		return getTravelTimeToFixed(person, destination, time, StandardMode.CAR);
	}

	@Override
	public double getTRAVEL_TIME_PEDESTRIAN_FIX(Zone category, Person person, Zone origin, Zone destination,
			ActivityIfc previousActivity, ActivityIfc nextActivity, Time time, double randomNumber) {
		return getTravelTimeToFixed(person, destination, time, StandardMode.PEDESTRIAN);
	}

	@Override
	public double getTRAVEL_TIME_PUBLICTRANSPORT_FIX(Zone category, Person person, Zone origin, Zone destination,
			ActivityIfc previousActivity, ActivityIfc nextActivity, Time time, double randomNumber) {
		return getTravelTimeToFixed(person, destination, time, StandardMode.PUBLICTRANSPORT);
	}

	

}
