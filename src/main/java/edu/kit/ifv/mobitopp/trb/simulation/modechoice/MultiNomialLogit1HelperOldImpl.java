package edu.kit.ifv.mobitopp.trb.simulation.modechoice;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.modeChoice.ModeAvailabilityModel;
import edu.kit.ifv.mobitopp.simulation.modechoice.ModeResolver;
import edu.kit.ifv.mobitopp.trb.simulation.modechoice.gen.DefaultMultiNomialLogit1HelperOld;

public class MultiNomialLogit1HelperOldImpl extends DefaultMultiNomialLogit1HelperOld {

	private final ModeResolver resolver;
	private final ModeAvailabilityModel modeAvailabilityModel;
	
	public MultiNomialLogit1HelperOldImpl(ImpedanceIfc impedance, ModeResolver resolver, ModeAvailabilityModel modeAvailabilityModel) {
		super(impedance);
		this.resolver = resolver;
		this.modeAvailabilityModel = modeAvailabilityModel;
	}

	@Override
	public double getACCESS_TIME_PUBLICTRANSPORT(String category, Person person, Zone origin, Zone destination,
			ActivityIfc previousActivity, ActivityIfc nextActivity, Set<Mode> choiceSet, double randomNumber) {
		return 0.0;
	}

	@Override
	public double getECONOMICAL_STATUS(String category, Person person, Zone origin, Zone destination,
			ActivityIfc previousActivity, ActivityIfc nextActivity, Set<Mode> choiceSet, double randomNumber) {
		return person.household().economicalStatus().ordinal();
	}

	@Override
	public double getEGRESS_TIME_PUBLICTRANSPORT(String category, Person person, Zone origin, Zone destination,
			ActivityIfc previousActivity, ActivityIfc nextActivity, Set<Mode> choiceSet, double randomNumber) {
		return 0.0;
	}

	@Override
	public double getTRANSFER_PUBLICTRANSPORT(String category, Person person, Zone origin, Zone destination,
			ActivityIfc previousActivity, ActivityIfc nextActivity, Set<Mode> choiceSet, double randomNumber) {
		return 0.0;
	}

	@Override
	public double getRandom(Person person, Zone origin, Zone destination, ActivityIfc previousActivity,
			ActivityIfc nextActivity, Set<Mode> choiceSet, double randomNumber) {
		return randomNumber;
	}

	@Override
	public boolean isAvailable(String category, Person person, Zone origin, Zone destination,
			ActivityIfc previousActivity, ActivityIfc nextActivity, Set<Mode> choiceSet, double randomNumber) {
		Mode mode = resolve(category);
		return modeAvailabilityModel
			.filterAvailableModes(person, origin, destination, previousActivity, nextActivity,
				choiceSet)
			.contains(mode) && choiceSet.contains(mode);
	}

	@Override
	public Mode resolve(String category) {
		return resolver.resolve(category);
	}

	@Override
	public Collection<String> getChoiceSet(Person person, Zone origin, Zone destination, ActivityIfc previousActivity,
			ActivityIfc nextActivity, Set<Mode> choiceSet, double randomNumber) {
		return choiceSet.stream().map(m -> resolver.getString(m)).collect(Collectors.toList());
	}

}
