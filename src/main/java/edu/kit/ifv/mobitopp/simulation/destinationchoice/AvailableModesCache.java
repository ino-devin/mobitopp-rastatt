package edu.kit.ifv.mobitopp.simulation.destinationchoice;

import java.util.Collection;
import java.util.Set;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.modeChoice.ModeAvailabilityModel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AvailableModesCache {

	private final ModeAvailabilityModel modeAvailabilityModel;
	private final Collection<Mode> choiceSet;
	private Person person;
	private Zone origin;
	private Set<Mode> cached;

	public synchronized Set<Mode> get(Person person, Zone origin) {
		if (this.person != person || this.origin != origin) {
			this.cached = updateAvailableModes(person, origin);
			this.person = person;
			this.origin = origin;
		}
		return cached;
	}

	private Set<Mode> updateAvailableModes(Person person, Zone origin) {
		ActivityIfc currentActivity = person.currentActivity();
		return modeAvailabilityModel.availableModes(person, origin, currentActivity, choiceSet);
	}

}
