package edu.kit.ifv.mobitopp.simulation.modechoice;

import java.util.Map;

import edu.kit.ifv.mobitopp.populationsynthesis.PersonBuilder;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.modechoice.preference.gen.ModePreferenceHelper;
import edu.kit.ifv.mobitopp.simulation.modechoice.preference.gen.ModePreferenceUtilityFunction;
import edu.kit.ifv.mobitopp.simulation.modechoice.preference.gen.PersonChangerFunctionLogger;
import edu.kit.ifv.mobitopp.util.parameter.LogitParameters;

public class ModePreferencesFuntions {
	
	private final ModePreferenceUtilityFunction functions;
	
	public ModePreferencesFuntions(LogitParameters logitParameters, ModePreferenceHelper helper, PersonChangerFunctionLogger logger) {
		super();
		this.functions = new ModePreferenceUtilityFunction(logitParameters, helper, logger);
	}
	
	public double calculateAsc_car_d(PersonBuilder person, Map<Mode, Double> draws) {
		return  this.functions.calculateAsc_car_d(person, draws);
	}

	public double calculateAsc_bike(PersonBuilder person, Map<Mode, Double> draws) {
		return  this.functions.calculateAsc_bike(person, draws);
	}
	public double calculateAsc_put(PersonBuilder person, Map<Mode, Double> draws) {
		return  this.functions.calculateAsc_put(person, draws);
	}
	public double calculateAsc_car_p(PersonBuilder person, Map<Mode, Double> draws) {
		return  this.functions.calculateAsc_car_p(person, draws);
	}
	public double calculateAsc_ped(PersonBuilder person, Map<Mode, Double> draws) {
		return  this.functions.calculateAsc_ped(person, draws);
	}
	public double calculateAsc_taxi(PersonBuilder person, Map<Mode, Double> draws) {
		return  this.functions.calculateAsc_taxi(person, draws);
	}
	public double calculateAsc_cs_sb(PersonBuilder person, Map<Mode, Double> draws) {
		return  this.functions.calculateAsc_cs_sb(person, draws);
	}
	public double calculateAsc_cs_ff(PersonBuilder person, Map<Mode, Double> draws) {
		return  this.functions.calculateAsc_cs_ff(person, draws);
	}
	public double calculateAsc_bs(PersonBuilder person, Map<Mode, Double> draws) {
		return  this.functions.calculateAsc_bs(person, draws);
	}
	public double calculateAsc_rp(PersonBuilder person, Map<Mode, Double> draws) {
		return  this.functions.calculateAsc_rp(person, draws);
	}
}

