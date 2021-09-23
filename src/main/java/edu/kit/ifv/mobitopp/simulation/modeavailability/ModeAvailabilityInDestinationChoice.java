package edu.kit.ifv.mobitopp.simulation.modeavailability;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.Household;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.StandardChoiceSet;
import edu.kit.ifv.mobitopp.simulation.StandardMode;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.carsharing.CarSharingCar;
import edu.kit.ifv.mobitopp.simulation.modeChoice.ModeAvailabilityModel;


public class ModeAvailabilityInDestinationChoice implements ModeAvailabilityModel {


	@Override
	public Set<Mode> availableModes(Person person, Zone zone, ActivityIfc previousActivity) {
		return availableModes(person, zone, previousActivity, StandardChoiceSet.CHOICE_SET_FULL);
	}

	@Override
	public Set<Mode> availableModes(Person person, Zone zone, ActivityIfc previousActivity,
		Collection<Mode> allModes) {
		LinkedHashSet<Mode> choiceSet = new LinkedHashSet<>(allModes);
		
		if(isAtHome(previousActivity)) {
			if(!carAvailable(person)) {
				choiceSet.remove(StandardMode.CAR);
			} 

			if(!person.hasBike()) {
				choiceSet.remove(StandardMode.BIKE);
			}
		} else {

			Mode previousMode = previousActivity.mode().mainMode();

			if (previousMode.isFlexible())
			{
				choiceSet.removeAll(StandardChoiceSet.FIXED_MODES);
			} 
			else if (previousMode.equals(StandardMode.CARSHARING_FREE) && !zone.carSharing().isFreeFloatingZone((CarSharingCar) person.whichCar())) 
			{
				choiceSet.clear();
				choiceSet.add(StandardMode.CAR);
				
				return choiceSet;
			}
			else if (previousMode.equals(StandardMode.CARSHARING_STATION) && !person.hasParkedCar()) 
			{
				choiceSet.clear();
				choiceSet.add(StandardMode.CAR);
				
				return choiceSet;
			}
			else if (previousMode.equals(StandardMode.BIKESHARING) && !zone.bikeSharing().isBikeSharingArea()) 
			{
				choiceSet.clear();
				choiceSet.add(StandardMode.BIKE);
				
				return choiceSet;
			} else {
				choiceSet.clear();
				choiceSet.add(previousMode);
			}
		}
		
		return choiceSet;
	}
	
	@Override
	public Set<Mode> filterAvailableModes(Person person, Zone source, Zone target,
		ActivityIfc previousActivity, ActivityIfc nextActivity, Collection<Mode> allModes) {
		return availableModes(person, target, previousActivity, allModes);
	}

	@Override
	public Set<Mode> modesWithReasonableTravelTime(Person person, Zone source, Zone target,
		ActivityIfc previousActivity, ActivityIfc nextActivity, Collection<Mode> possibleModes,
		boolean keepAtLeastOne) {
		throw new IllegalStateException("Should never happen");
	}
	
	protected boolean isAtHome(
			ActivityIfc previousActivity
		) {

			return previousActivity.activityType().isHomeActivity();
		}
	
	protected boolean carAvailable(
			Person person
		) {
			Household theHousehold = person.household();

			return person.hasDrivingLicense()
							&& theHousehold.getNumberOfAvailableCars() > 0;
		}
		

}
