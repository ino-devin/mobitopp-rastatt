package edu.kit.ifv.mobitopp.populationsynthesis.carownership;

import java.util.function.DoubleSupplier;

import edu.kit.ifv.mobitopp.util.parameter.LogitParameters;

public class StaticSelector extends NestedLogitSelector {

	private final double asc_0;
	private final double asc_1;
	private final double asc_2;
	private final double asc_3;
	private final double asc_4;
	
	public StaticSelector(DoubleSupplier randomGenerator, LogitParameters parameters) {
		super(randomGenerator, parameters);
    asc_0 = parameters.get("asc_0");
  	asc_1 = parameters.get("asc_1");
  	asc_2 = parameters.get("asc_2");
  	asc_3 = parameters.get("asc_3");
  	asc_4 = parameters.get("asc_4");
	}

	protected double asc_0() {
		return asc_0;
	}

	protected double asc_1() {
		return asc_1;
	}

	protected double asc_2() {
		return asc_2;
	}

	protected double asc_3() {
		return asc_3;
	}

	protected double asc_4() {
		return asc_4;
	}

}
