package edu.kit.ifv.mobitopp.simulation.destinationchoice;

import java.util.Map;
import java.util.Optional;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.destinationChoice.DestinationChoiceModel;

public class FixedLocationDestinationChoice implements DestinationChoiceModel {

  private final Map<ActivityType, DestinationChoiceModel> choiceModels;
	private final DestinationChoiceModel defaultModel;

	public FixedLocationDestinationChoice(
			Map<ActivityType, DestinationChoiceModel> choiceModels, DestinationChoiceModel defaultModel) {
		super();
		this.choiceModels = choiceModels;
		this.defaultModel = defaultModel;
	}

	@Override
	public Zone selectDestination(
			Person person, Optional<Mode> tourMode, ActivityIfc previousActivity,
			ActivityIfc nextActivity, double randomNumber) {
		ActivityType activityType = nextActivity.activityType();
		if (ActivityType.HOME.equals(activityType)) {
			return person.homeZone();
		}
		if (person.hasFixedZoneFor(activityType)) {
			return person.fixedZoneFor(activityType);
		}
		if (choiceModels.containsKey(activityType)) {
			return choiceModels
					.get(activityType)
					.selectDestination(person, tourMode, previousActivity, nextActivity, randomNumber);
		}
		System.out
				.println("Destination choice model for activity type " + activityType
						+ " is missing, using default model. Available activity types: "
						+ choiceModels.keySet());
		return defaultModel
				.selectDestination(person, tourMode, previousActivity, nextActivity, randomNumber);
	}

  @Override
  public boolean isTourBased() {
    return false;
  }

}
