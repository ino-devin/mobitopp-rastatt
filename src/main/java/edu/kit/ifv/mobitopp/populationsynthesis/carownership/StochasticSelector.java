package edu.kit.ifv.mobitopp.populationsynthesis.carownership;

import java.util.function.DoubleSupplier;

import edu.kit.ifv.mobitopp.util.parameter.LogitParameters;

public class StochasticSelector extends NestedLogitSelector {

	private final double asc_0_mu;
	private final double asc_0_sig;
	private final double asc_1_mu;
	private final double asc_1_sig;
	private final double asc_2_mu;
	private final double asc_2_sig;
	private final double asc_3_mu;
	private final double asc_3_sig;
	private final double asc_4;

	private final DoubleSupplier drawAsc;

	public StochasticSelector(
			DoubleSupplier randomGenerator, LogitParameters parameters, DoubleSupplier drawAsc) {
		super(randomGenerator, parameters);
		this.drawAsc = drawAsc;
		asc_0_mu = parameters.get("asc_0_mu");
		asc_0_sig = parameters.get("asc_0_sig");
		asc_1_mu = parameters.get("asc_1_mu");
		asc_1_sig = parameters.get("asc_1_sig");
		asc_2_mu = parameters.get("asc_2_mu");
		asc_2_sig = parameters.get("asc_2_sig");
		asc_3_mu = parameters.get("asc_3_mu");
		asc_3_sig = parameters.get("asc_3_sig");
		asc_4 = parameters.get("asc_4");
	}

	@Override
	protected double asc_0() {
		return asc_0_mu + asc_0_sig * nextAsc();
	}

	private double nextAsc() {
		return drawAsc.getAsDouble();
	}

	@Override
	protected double asc_1() {
		return asc_1_mu + asc_1_sig * nextAsc();
	}

	@Override
	protected double asc_2() {
		return asc_2_mu + asc_2_sig * nextAsc();
	}

	@Override
	protected double asc_3() {
		return asc_3_mu + asc_3_sig * nextAsc();
	}

	@Override
	protected double asc_4() {
		return asc_4;
	}

}
