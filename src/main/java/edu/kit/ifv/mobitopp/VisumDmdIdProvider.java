package edu.kit.ifv.mobitopp;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.BaseHousehold;
import edu.kit.ifv.mobitopp.simulation.BasePerson;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.opportunities.Opportunity;

public class VisumDmdIdProvider {
	
	public static int personId(BasePerson person) {
		return person.getId().getOid(); //+1??
	}
	
	public static int householdId(BaseHousehold household) {
		return  1+ household.getId().getOid();
	}
	

	
	public static int locationIdOf(Opportunity opportunity) {
		return locationIdOf(opportunity.location());
	}
	

	public static int locationIdOf(Zone zone) {
		return locationIdOf(zone.centroidLocation());
	}
	

	public static int locationIdOf(BaseHousehold household) {
		return locationIdOf(household.homeLocation());
	}
	

	public static int locationIdOf(Location location) {
		return Math.max(Math.abs(location.hashCode()), 1);
	}
}
