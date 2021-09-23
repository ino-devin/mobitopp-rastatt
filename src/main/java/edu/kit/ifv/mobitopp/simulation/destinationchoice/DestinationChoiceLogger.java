package edu.kit.ifv.mobitopp.simulation.destinationchoice;

import java.util.Map;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.destinationchoice.generated.DestinationChoiceUtilityModelLogger;
import edu.kit.ifv.mobitopp.time.Time;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DestinationChoiceLogger implements DestinationChoiceUtilityModelLogger {
	private int cnt;
	private int n;
	
	
	public DestinationChoiceLogger(int logNthChoice) {
		this.cnt = -1;
		this.n = logNthChoice;
	}
	
	@Override
	public void logStart(Person person, Zone origin, Zone destination, ActivityIfc previousActivity,
			ActivityIfc nextActivity, Time time, double randomNumber) {
		if (n != 0) {
			cnt = (cnt+1) % n;
		}
		if (cnt == 0) {
			log.debug("Start choice model DestinationChoiceUtilityModel for " + String.format("person %s, origin %s, destination %s, previousActivity %s, nextActivity %s, time %s, randomNumber %s", String.valueOf(person.getOid()), String.valueOf(origin), String.valueOf(destination), String.valueOf(previousActivity), String.valueOf(nextActivity), String.valueOf(time), String.valueOf(randomNumber)));
		}
	}

	@Override
	public void logProbabilities(Map<Zone, Double> probabilities, Person person, Zone origin, Zone destination,
			ActivityIfc previousActivity, ActivityIfc nextActivity, Time time, double randomNumber) {
		if (cnt == 0) {
			StringBuilder message = new StringBuilder();
			message.append("Probabilities for " + String.format("person %s, origin %s, destination %s, previousActivity %s, nextActivity %s, time %s, randomNumber %s", String.valueOf(person.getOid()), String.valueOf(origin), String.valueOf(destination), String.valueOf(previousActivity), String.valueOf(nextActivity), String.valueOf(time), String.valueOf(randomNumber)));
			message.append(": ");
			probabilities.entrySet().forEach(entry -> message
					.append(String.format("%s = %s, ", entry.getKey(), entry.getValue())));
			log.debug(message.toString());
		}
	}

	@Override
	public void log(Person person, Zone origin, Zone destination, ActivityIfc previousActivity,
			ActivityIfc nextActivity, Time time, double randomNumber, Zone category, String key, double value) {
		if (cnt == 0) {
			log.debug("Utility Parameter - "  + String.format("%s: %s = %s", String.valueOf(category), key, value) + " for " + String.format("person %s, origin %s, destination %s, previousActivity %s, nextActivity %s, time %s, randomNumber %s", String.valueOf(person.getOid()), String.valueOf(origin), String.valueOf(destination), String.valueOf(previousActivity), String.valueOf(nextActivity), String.valueOf(time), String.valueOf(randomNumber)));
		}
	}

	@Override
	public void log(Person person, Zone origin, Zone destination, ActivityIfc previousActivity,
			ActivityIfc nextActivity, Time time, double randomNumber, String key, double value) {
		if (cnt == 0) {
			log.info(String.format("Utility %s = %s", String.valueOf(key), value) + " for " + String.format("person %s, origin %s, destination %s, previousActivity %s, nextActivity %s, time %s, randomNumber %s", String.valueOf(person.getOid()), String.valueOf(origin), String.valueOf(destination), String.valueOf(previousActivity), String.valueOf(nextActivity), String.valueOf(time), String.valueOf(randomNumber)));
		}
	}

}
