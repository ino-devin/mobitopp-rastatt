package edu.kit.ifv.mobitopp.simulation;

import java.util.List;
import java.util.Random;
import java.util.function.DoubleSupplier;

import edu.kit.ifv.mobitopp.data.local.configuration.DynamicParameters;
import edu.kit.ifv.mobitopp.populationsynthesis.serialiser.PersonChanger;
import edu.kit.ifv.mobitopp.simulation.modechoice.ModeChoicePreferencesChanger;
import edu.kit.ifv.mobitopp.simulation.modechoice.ModePreferenceHelperImpl;
import edu.kit.ifv.mobitopp.simulation.modechoice.PersonChangerLogger;
import edu.kit.ifv.mobitopp.simulation.modechoice.TimeSensitivityHelperImpl;
import edu.kit.ifv.mobitopp.simulation.modechoice.TravelTimeSensitivitySelector;
import edu.kit.ifv.mobitopp.simulation.modechoice.preference.gen.ModePreferenceHelper;
import edu.kit.ifv.mobitopp.simulation.modechoice.sensitivity.gen.TimeSensitivityHelper;
import edu.kit.ifv.mobitopp.util.parameter.LogitParameters;
import edu.kit.ifv.mobitopp.util.parameter.ParameterFormularParser;

public class MixedPersonChangerFactory implements PersonChangerFactory {

	public static final String preferencesSeedAttribute = "modePreferencesSeed";
	public static final String travelTimeSensitivitySeedAttribute = "travelTimeSensitivitySeed";

	@Override
	public PersonChanger create(WrittenConfiguration configuration) {
		return new MultipleChanges(List
				.of(createModePreferencesChanger(configuration),
						createTravelTimeSensitivitySelector(configuration)));
	}

	private PersonChanger createTravelTimeSensitivitySelector(WrittenConfiguration configuration) {
		DynamicParameters parameters = new DynamicParameters(configuration.getModeChoice());
		DoubleSupplier randomGenerator = createRandomGenerator(parameters,
				travelTimeSensitivitySeedAttribute);
		LogitParameters logitParameters = new ParameterFormularParser()
				.parseToParameter(parameters.valueAsFile("main"));
		TimeSensitivityHelper helper = new TimeSensitivityHelperImpl();
		
		PersonChangerLogger logger = createTimeSensitivityLogger(configuration);
		return new TravelTimeSensitivitySelector(randomGenerator, logitParameters, helper, logger);
	}

	private PersonChanger createModePreferencesChanger(WrittenConfiguration configuration) {
		DynamicParameters parameters = new DynamicParameters(configuration.getModeChoice());
		DoubleSupplier randomGenerator = createRandomGenerator(parameters, preferencesSeedAttribute);
		LogitParameters logitParameters = new ParameterFormularParser()
				.parseToParameter(parameters.valueAsFile("main"));
		ModePreferenceHelper modePreferencesHelper = new ModePreferenceHelperImpl();
		PersonChangerLogger logger = createModePreferenceLogger(configuration);
		return new ModeChoicePreferencesChanger(randomGenerator, logitParameters, modePreferencesHelper, logger);
	}

	private DoubleSupplier createRandomGenerator(DynamicParameters parameters, String seed) {
		return new Random(parameters.valueAsLong(seed))::nextGaussian;
	}

	private PersonChangerLogger createTimeSensitivityLogger(WrittenConfiguration configuration) {
		DynamicParameters experimental = new DynamicParameters(configuration.getExperimental());
		int n = 0;
		if (experimental.hasValue("logTimeSensitivityN")) {
			n = experimental.valueAsInteger("logTimeSensitivityN");
		}

		return new PersonChangerLogger(n);
	}
	
	private PersonChangerLogger createModePreferenceLogger(WrittenConfiguration configuration) {
		DynamicParameters experimental = new DynamicParameters(configuration.getExperimental());
		int n = 0;
		if (experimental.hasValue("logModePreferenceN")) {
			n = experimental.valueAsInteger("logModePreferenceN");
		}

		return new PersonChangerLogger(n);
	}
}
