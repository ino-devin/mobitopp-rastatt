package edu.kit.ifv.mobitopp.populationsynthesis.carownership;

import edu.kit.ifv.mobitopp.populationsynthesis.HouseholdForSetup;

public interface NumberOfCarsSelector {

  int calculateNumberOfCarsFor(HouseholdForSetup household);
}
