package edu.kit.ifv.mobitopp.populationsynthesis;

import edu.kit.ifv.mobitopp.util.panel.PersonOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.PersonOfPanelDataId;

public class PersonOfPanelDataBuilder {

	public static final int aNumber = 1;
	public static final int otherNumber = 2;
	public static final PersonOfPanelDataId anId = new PersonOfPanelDataId(
			ExampleHouseholdOfPanelData.anId, aNumber);
	public static final PersonOfPanelDataId otherId = new PersonOfPanelDataId(
			ExampleHouseholdOfPanelData.otherId, otherNumber);
	public static final int genderMale = 1;
	public static final int genderFemale = 2;
	public static final int defaultGender = genderMale;
	private static final int graduationUndefined = -1;
	private static final byte graduation = graduationUndefined;
	public static final short defaultBirthyear = 4;
	public static final int defaultAge = 5;
	public static final byte defaultEmployment = 6;
	public static final float defaultPoleDistance = 7;
	public static final float defaultDistanceWork = 1.0f;
	public static final float defaultDistanceEducation = 2.0f;
	public static final boolean defaultCommuterTicket = false;
	public static final boolean defaultBicycle = true;
	public static final boolean defaultPersonalCar = false;
	public static final boolean defaultCarAvailable = true;
	public static final boolean defaultHasLicense = true;
	public static final Integer defaultIncome = 9;
	public static final String defaultActivityPattern = "pattern";
	public static final float defaultPreferenceCardriver = 0.5f;
	public static final float defaultPreferenceCarpassenger = 0.1f;
	public static final float defaultPreferenceWalking = 0.1f;
	public static final float defaultPrefCycling = 0.1f;
	public static final float defaultPrefPublictransport = 0.1f;

	private PersonOfPanelDataId id;
	private byte gender;
	private short age;
	private int income;
	private float distanceWork;
	
	public PersonOfPanelDataBuilder() {
		id = anId;
		gender = defaultGender;
		age = defaultAge;
		income = defaultIncome;
		distanceWork = defaultDistanceWork;
	}

	public static PersonOfPanelDataBuilder personOfPanelData() {
		return new PersonOfPanelDataBuilder();
	}

	public PersonOfPanelDataBuilder withId(PersonOfPanelDataId id) {
		this.id = id;
		return this;
	}

	public PersonOfPanelDataBuilder withGender(byte gender) {
		this.gender = gender;
		return this;
	}

	public PersonOfPanelDataBuilder withAge(short age) {
		this.age = age;
		return this;
	}

	public PersonOfPanelDataBuilder withIncome(int income) {
		this.income = income;
		return this;
	}
	
	public PersonOfPanelDataBuilder withDistanceWork(float distance) {
		this.distanceWork = distance;
		return this;
	}

	public PersonOfPanelData build() {
		return new PersonOfPanelData(
				id,
				gender,
				graduation,
				defaultBirthyear,
				age,
				defaultEmployment,
				distanceWork,
				defaultDistanceEducation,
				defaultCommuterTicket,
				defaultBicycle,
				defaultPersonalCar,
				defaultCarAvailable,
				defaultHasLicense,
				income,
				defaultActivityPattern,
				defaultPreferenceCardriver,
				defaultPreferenceCarpassenger,
				defaultPreferenceWalking,
				defaultPrefCycling,
				defaultPrefPublictransport
				);
	}

}
