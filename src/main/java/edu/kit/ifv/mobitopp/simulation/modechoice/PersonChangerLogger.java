package edu.kit.ifv.mobitopp.simulation.modechoice;

import java.util.Map;

import edu.kit.ifv.mobitopp.populationsynthesis.PersonBuilder;
import edu.kit.ifv.mobitopp.simulation.modechoice.preference.gen.PersonChangerFunctionLogger;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PersonChangerLogger implements PersonChangerFunctionLogger,
		edu.kit.ifv.mobitopp.simulation.modechoice.sensitivity.gen.PersonChangerFunctionLogger {

	private final int n;
	private int count;
	
	public PersonChangerLogger(int n) {
		this.n = n;
		this.count = -1;
	}
		
	@Override
	public void logStart(PersonBuilder person) {
		if (n != 0) {
			count = (count + 1) % n;
		}
		if (count==0) {
			log.debug("Start choice model PersonChangerFunction for " + String.format("person %s", String.valueOf(person.getId())));
		}
	}
	
	@Override
	public void logProbabilities(Map<String, Double> probabilities, PersonBuilder person) {
		if (count==0) {
			StringBuilder message = new StringBuilder();
			message.append("Probabilities for " + String.format("person %s", String.valueOf(person.getId())));
			message.append(": ");
			probabilities.entrySet().forEach(entry -> message
					.append(String.format("%s = %s, ", entry.getKey(), entry.getValue())));
			log.debug(message.toString());
		}
	}
	
	@Override
	public void log(PersonBuilder person, String category, String key, double value) {
		if (count==0) {
			log.debug("Utility Parameter - "  + String.format("%s: %s = %s", String.valueOf(category), key, value) + " for " + String.format("person %s", String.valueOf(person.getId())));
		}
	}

	@Override
	public void log(PersonBuilder person, String key, double value) {
		if (count==0) {
			log.info(String.format("Utility %s = %s", String.valueOf(key), value) + " for " + String.format("person %s", String.valueOf(person.getId())));
		}
	}

}
