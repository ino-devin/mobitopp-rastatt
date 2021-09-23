package edu.kit.ifv.mobitopp.simulation.destinationchoice;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.time.Time;

public interface DestinationChoiceUtilityModel {

	Zone selectDestination(Person person, Zone origin, Zone destination, ActivityIfc previousActivity,
			ActivityIfc nextActivity, Time time, double randomNumber);

}
