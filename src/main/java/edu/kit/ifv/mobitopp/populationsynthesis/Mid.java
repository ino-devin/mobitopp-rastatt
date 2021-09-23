package edu.kit.ifv.mobitopp.populationsynthesis;

public class Mid {

  static final int maxAge = 200;

  public int calculateTypeOf(HouseholdForSetup household) {
    if (isYoung(household)) {
      return 1;
    }
    if (isFamily(household)) {
      return 2;
    }
    if (isElderly(household)) {
      return 4;
    }
    return 0;
  }

  private boolean isYoung(HouseholdForSetup household) {
    return household.getSize() == household.getNumberOfPersonsInAgeRange(18, 34);
  }

  private boolean isFamily(HouseholdForSetup household) {
    return 0 < household.getNumberOfPersonsInAgeRange(0, 17);
  }

  private boolean isElderly(HouseholdForSetup household) {
    return household.getSize() == household.getNumberOfPersonsInAgeRange(65, maxAge);
  }
}
