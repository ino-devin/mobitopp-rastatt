package edu.kit.ifv.mobitopp.populationsynthesis;

import edu.kit.ifv.mobitopp.simulation.StandardMode;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelDataId;

public class HouseholdOfPanelDataBuilder {

	public static final short defaultYear = 2000;
	public static final int aNumber = 3;
	public static final int otherNumber = 4;
	public static final HouseholdOfPanelDataId anId = new HouseholdOfPanelDataId(defaultYear,
			aNumber);
	public static final HouseholdOfPanelDataId otherId = new HouseholdOfPanelDataId(defaultYear,
			otherNumber);
	public static final int defaultAreaType = 4;
	public static final int defaultSize = 1;
	public static final int defaultType = 0;
	public static final int aDomCode = 6;
	public static final int otherDomCode = 7;
	public static final int defaultReportingPersons = 8;
	public static final int defaultMinors = 9;
	public static final int defaultNotReportingChildren = 10;
	public static final int defaultCars = 11;
	public static final int defaultIncome = 12;
	public static final int defaultIncomeClass = 1;
  public static final int defaultActivityRadiusMode = StandardMode.CAR.getCode();

	private HouseholdOfPanelDataId id;
	private int domCode;
	private int size;
	private float activityRadius;
	private int activityRadiusMode;
	private float weight = 1.0f;

	public HouseholdOfPanelDataBuilder() {
		this.id = anId;
		this.domCode = aDomCode;
		this.size = defaultSize;
		this.activityRadiusMode = defaultActivityRadiusMode;
	}

	public static HouseholdOfPanelDataBuilder householdOfPanelData() {
		return new HouseholdOfPanelDataBuilder();
	}

	public HouseholdOfPanelDataBuilder withId(final HouseholdOfPanelDataId id) {
		this.id = id;
		return this;
	}

	public HouseholdOfPanelDataBuilder withDomCode(final int domCode) {
		this.domCode = domCode;
		return this;
	}

	public HouseholdOfPanelDataBuilder withSize(final int size) {
		this.size = size;
		return this;
	}

	public HouseholdOfPanelDataBuilder activityRadius(final float activityRadius, final StandardMode mode) {
		this.activityRadius = activityRadius;
		this.activityRadiusMode = mode.getCode();
		return this;
	}

	public HouseholdOfPanelData build() {
		return new HouseholdOfPanelData(id, defaultAreaType, size, defaultType, domCode, defaultReportingPersons,
				defaultMinors, defaultNotReportingChildren, defaultCars, defaultIncome, defaultIncomeClass,
				activityRadius, activityRadiusMode, weight);
	}

}
