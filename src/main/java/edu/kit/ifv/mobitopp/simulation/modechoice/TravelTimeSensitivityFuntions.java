package edu.kit.ifv.mobitopp.simulation.modechoice;

import java.util.Map;

import edu.kit.ifv.mobitopp.populationsynthesis.PersonBuilder;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.modechoice.sensitivity.gen.PersonChangerFunctionLogger;
import edu.kit.ifv.mobitopp.simulation.modechoice.sensitivity.gen.TimeSensitivityHelper;
import edu.kit.ifv.mobitopp.simulation.modechoice.sensitivity.gen.TimeSensitivityUtilityFunction;
import edu.kit.ifv.mobitopp.util.parameter.LogitParameters;


public class TravelTimeSensitivityFuntions {
	
	private final TimeSensitivityUtilityFunction functions;
	
	public TravelTimeSensitivityFuntions(LogitParameters logitParameters, TimeSensitivityHelper helper, PersonChangerFunctionLogger logger) {
		this.functions = new TimeSensitivityUtilityFunction(logitParameters, helper, logger);
	}

	public double calculateAsc_car_p(PersonBuilder person, Map<Mode, Double> draws) {
		return this.functions.calculateU_tt_car_p(person, draws);
				
	}

	public double calculateAsc_ped(PersonBuilder person, Map<Mode, Double> draws) {
		return this.functions.calculateU_tt_ped(person, draws);
	}

	public double calculateAsc_taxi(PersonBuilder person, Map<Mode, Double> draws) {
		return this.functions.calculateU_tt_taxi(person, draws);
	}

	public double calculateAsc_cs_sb(PersonBuilder person, Map<Mode, Double> draws) {
		return this.functions.calculateU_tt_cs_sb(person, draws);
	}

	public double calculateAsc_cs_ff(PersonBuilder person, Map<Mode, Double> draws) {
		return this.functions.calculateU_tt_cs_ff(person, draws);
	}

	public double calculateAsc_bs(PersonBuilder person, Map<Mode, Double> draws) {
		return this.functions.calculateU_tt_bs(person, draws);
	}

	public double calculateAsc_rp(PersonBuilder person, Map<Mode, Double> draws) {
		return this.functions.calculateU_tt_rp(person, draws);
	}

	public double calculateAsc_car_d(PersonBuilder person, Map<Mode, Double> draws) {
		return this.functions.calculateU_tt_car_d(person, draws);
	}

	public double calculateAsc_bike(PersonBuilder person, Map<Mode, Double> draws) {
		return this.functions.calculateU_tt_bike(person, draws);
	}

	public double calculateAsc_put(PersonBuilder person, Map<Mode, Double> draws) {
	return this.functions.calculateU_tt_put(person, draws);
	}
}
