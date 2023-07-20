package edu.kit.ifv.mobitopp.simulation.modeavailability;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.StandardMode;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.carsharing.CarSharingCar;
import edu.kit.ifv.mobitopp.simulation.modeChoice.BasicModeAvailabilityModel;
import edu.kit.ifv.mobitopp.simulation.modeChoice.ModeAvailabilityModel;

public class ModeAvailabilityModelConsideringBikeSharing extends BasicModeAvailabilityModel
		implements ModeAvailabilityModel {

	public ModeAvailabilityModelConsideringBikeSharing(ImpedanceIfc impedance) {
		super(impedance);
	}

	@Override
	public Set<Mode> availableModes(
		Person person,
		Zone currentZone,
		ActivityIfc previousActivity,
		Collection<Mode> allModes
	) {
		Mode previousMode = previousActivity.mode();
		
		Set<Mode> choiceSet = new LinkedHashSet<>(
				super.availableModes(person, currentZone, previousActivity, allModes));

		assert choiceSet != null;

		Set<Mode> handledCarSharing = handleCarSharing(person, currentZone, previousActivity,
				previousMode, choiceSet);
		return handleBikeSharing(person, currentZone, previousMode, handledCarSharing);
	}

	private Set<Mode> handleBikeSharing(
			Person person, Zone currentZone, Mode previousMode, Set<Mode> choiceSet) {
		if (person.hasParkedBike() && StandardMode.BIKESHARING.equals(previousMode)) {
			return Set.of(StandardMode.BIKESHARING);
		}
		if (!currentZone.bikeSharing().isBikeAvailableFor(person)) {
			choiceSet.remove(StandardMode.BIKESHARING);
		}
		return choiceSet;
	}

	private Set<Mode> handleCarSharing(
			Person person, Zone currentZone, ActivityIfc previousActivity, Mode previousMode,
			Set<Mode> choiceSet) {
		if (isAtHome(previousActivity)) {

			if ( person.hasDrivingLicense() && person.household().getNumberOfAvailableCars() == 0) {

				if( currentZone.carSharing().isStationBasedCarSharingCarAvailable(person)) {
					choiceSet.add(StandardMode.CARSHARING_STATION);
				}

				if( currentZone.carSharing().isFreeFloatingCarSharingCarAvailable(person)) {
					choiceSet.add(StandardMode.CARSHARING_FREE);
				}
			}

			if(previousActivity.isModeSet()
					&& previousActivity.mode()==StandardMode.CARSHARING_FREE && person.hasParkedCar()) {

				assert !currentZone.carSharing().isFreeFloatingZone((CarSharingCar)person.whichCar());

				choiceSet = Collections.singleton(StandardMode.CARSHARING_FREE);
			} 
		} else {
			if(previousMode==StandardMode.CARSHARING_STATION) {

					choiceSet = Collections.singleton(StandardMode.CARSHARING_STATION);

			} else if(previousMode==StandardMode.CARSHARING_FREE) {

				if(person.hasParkedCar()) {
	
					assert !currentZone.carSharing().isFreeFloatingZone((CarSharingCar)person.whichCar());
	
					choiceSet = Collections.singleton(StandardMode.CARSHARING_FREE);

				} 
				else {
					choiceSet.remove(StandardMode.CAR);
					choiceSet.remove(StandardMode.BIKE);
	
					if(currentZone.carSharing().isFreeFloatingCarSharingCarAvailable(person)) {
						choiceSet.add(StandardMode.CARSHARING_FREE);
					}
				}
			} 
		}
		return choiceSet;
	}

	@Override
	public Set<Mode> filterAvailableModes(
			Person person, Zone origin, Zone destination, ActivityIfc previousActivity,
			ActivityIfc nextActivity, Collection<Mode> proposedChoiceSet) {
		Set<Mode> filteredModes = super.filterAvailableModes(person, origin, destination,
				previousActivity, nextActivity, proposedChoiceSet);
		if (previousModeIsFixed(filteredModes)) {
			return filteredModes;
		}
		if (previousActivity.activityType().isHomeActivity()
				&& nextActivity.activityType().isHomeActivity()) {
			filteredModes.remove(StandardMode.CARSHARING_STATION);
		}
		return filteredModes;
	}

}
