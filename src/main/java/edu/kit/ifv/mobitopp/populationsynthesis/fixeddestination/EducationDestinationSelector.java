package edu.kit.ifv.mobitopp.populationsynthesis.fixeddestination;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.DoubleSupplier;

import edu.kit.ifv.mobitopp.data.PanelDataRepository;
import edu.kit.ifv.mobitopp.data.ZoneRepository;
import edu.kit.ifv.mobitopp.populationsynthesis.PersonBuilder;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.FixedDestination;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.util.parameter.LogitParameters;

public class EducationDestinationSelector implements Consumer<PersonBuilder> {

	private final FixedDestinationModel primaryStudentModel;
	private final FixedDestinationModel otherStudentsModel;

	public EducationDestinationSelector(FixedDestinationModel primaryStudentModel,
		FixedDestinationModel otherStudentsModel) {
		super();
		this.primaryStudentModel = primaryStudentModel;
		this.otherStudentsModel = otherStudentsModel;
	}

	public static EducationDestinationSelector standard(PanelDataRepository panelDataRepository,
		ZoneRepository zoneRepository, ImpedanceIfc impedance, DoubleSupplier random,
		double defaultRange, Map<ActivityType, Double> rangeMap, LogitParameters logitParameters) {
		FixedDestinationModel primaryStudentModel = new PrimaryStudentsModel(zoneRepository,
			impedance, random);
		FixedDestinationModel otherStudentsModel = new OtherStudentsModel(panelDataRepository,
			zoneRepository, impedance, random, defaultRange, rangeMap, logitParameters);
		return new EducationDestinationSelector(primaryStudentModel, otherStudentsModel);
	}

	@Override
	public void accept(PersonBuilder person) {
		if (person.hasActivityOfType(ActivityType.EDUCATION_PRIMARY)) {
			FixedDestination destination = primaryStudentModel.selectFor(person);
			person.addFixedDestination(destination);
		}
		if (person
			.hasActivityOfType(ActivityType.EDUCATION, ActivityType.EDUCATION_SECONDARY,
				ActivityType.EDUCATION_TERTIARY, ActivityType.EDUCATION_OCCUP)) {
			FixedDestination destination = otherStudentsModel.selectFor(person);
			person.addFixedDestination(destination);
		}
	}

}
