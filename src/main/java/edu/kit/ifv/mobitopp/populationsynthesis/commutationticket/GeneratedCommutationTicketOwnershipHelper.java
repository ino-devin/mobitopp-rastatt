package edu.kit.ifv.mobitopp.populationsynthesis.commutationticket;

import edu.kit.ifv.mobitopp.populationsynthesis.PersonBuilder;

public interface GeneratedCommutationTicketOwnershipHelper {
	
	double getAGE(String category, PersonBuilder person);
		
	double getEMPLOYMENT(String category, PersonBuilder person);
		
	double getHAS_NO_LICENSE(String category, PersonBuilder person);
		
	double getHH_SIZE(String category, PersonBuilder person);
		
	double getHP_SEX(String category, PersonBuilder person);
		
	double getINCOME(String category, PersonBuilder person);
		
	double getP0_5(String category, PersonBuilder person);
		
	double getP6_17(String category, PersonBuilder person);
		
	double getPKWHH(String category, PersonBuilder person);
		
	double getSTUDENT_PRIMARY(String category, PersonBuilder person);
		
	double getSTUDENT_SECONDARY(String category, PersonBuilder person);
		
	double getSTUDENT_TERTIARY(String category, PersonBuilder person);
	
	double getEDUCATION(String category, PersonBuilder person);
	
	double getHOMEKEEPER(String category, PersonBuilder person);
	
	double getPARTTIME(String category, PersonBuilder person);
		
	
	/**
	  * Returns a random double value. 
	  * This random value can be based on the given parameters as well as the implementing classes fields.
	  */
	double getRandom(PersonBuilder person);
	
	
	/**
	  * Checks if the given category is available (category = e.g. mode of transportation, depends on the kind of choice model)
	  */
	boolean isAvailable(String category, PersonBuilder person);
	
	/**
	  * Converts the given category String to the return type boolean of CommutationTicketModelOwnershipModel.hasCommutationTicket(...).
	  */
	boolean resolve(String category);
}

