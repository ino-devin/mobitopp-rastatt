package edu.kit.ifv.mobitopp.populationsynthesis.commutationticket;

import edu.kit.ifv.mobitopp.populationsynthesis.PersonBuilder;
import edu.kit.ifv.mobitopp.util.parameter.LogitParameters;

public class GeneratedCommutationTicketOwnershipUtilityFunction {
	
	private final double asc_Ticket;
	private final double b_EINKO2;
	private final double b_EINKO3;
	private final double b_EINKO4;
	private final double b_EINKO5;
	private final double b_EINKO6;
	private final double b_alter1a;
	private final double b_alter1b;
	private final double b_alter3;
	private final double b_alter4;
	private final double b_alter5;
	private final double b_alter6;
	private final double b_alter7;
	private final double b_alter8;
	private final double b_hhgro_2;
	private final double b_hhgro_3;
	private final double b_hhgro_4;
	private final double b_hhgro_5;
	private final double b_hhgro_6;
	private final double b_hhgro_7;
	private final double b_hhgro_8;
	private final double b_nofspkw;
	private final double b_p05;
	private final double b_p617;
	private final double b_pkwhh1;
	private final double b_pkwhh2;
	private final double b_pkwhh3;
	private final double b_pkwhh4;
	private final double b_schueler;
	private final double b_student;
	private final double b_home;
	private final double b_parttime;
	private final double b_weibl;
	
	private final GeneratedCommutationTicketOwnershipHelper helper;
	
	public GeneratedCommutationTicketOwnershipUtilityFunction(LogitParameters logitParameters, GeneratedCommutationTicketOwnershipHelper helper) {
		super();
		this.helper = helper;
		
		this.asc_Ticket = logitParameters.get("asc_Ticket");
		this.b_EINKO2 = logitParameters.get("b_EINKO2");
		this.b_EINKO3 = logitParameters.get("b_EINKO3");
		this.b_EINKO4 = logitParameters.get("b_EINKO4");
		this.b_EINKO5 = logitParameters.get("b_EINKO5");
		this.b_EINKO6 = logitParameters.get("b_EINKO6");
		this.b_alter1a = logitParameters.get("b_alter1a");
		this.b_alter1b = logitParameters.get("b_alter1b");
		this.b_alter3 = logitParameters.get("b_alter3");
		this.b_alter4 = logitParameters.get("b_alter4");
		this.b_alter5 = logitParameters.get("b_alter5");
		this.b_alter6 = logitParameters.get("b_alter6");
		this.b_alter7 = logitParameters.get("b_alter7");
		this.b_alter8 = logitParameters.get("b_alter8");
		this.b_hhgro_2 = logitParameters.get("b_hhgro_2");
		this.b_hhgro_3 = logitParameters.get("b_hhgro_3");
		this.b_hhgro_4 = logitParameters.get("b_hhgro_4");
		this.b_hhgro_5 = logitParameters.get("b_hhgro_5");
		this.b_hhgro_6 = logitParameters.get("b_hhgro_6");
		this.b_hhgro_7 = logitParameters.get("b_hhgro_7");
		this.b_hhgro_8 = logitParameters.get("b_hhgro_8");
		this.b_nofspkw = logitParameters.get("b_nofspkw");
		this.b_p05 = logitParameters.get("b_p05");
		this.b_p617 = logitParameters.get("b_p617");
		this.b_pkwhh1 = logitParameters.get("b_pkwhh1");
		this.b_pkwhh2 = logitParameters.get("b_pkwhh2");
		this.b_pkwhh3 = logitParameters.get("b_pkwhh3");
		this.b_pkwhh4 = logitParameters.get("b_pkwhh4");
		this.b_schueler = logitParameters.get("b_schueler");
		this.b_student = logitParameters.get("b_student");
		this.b_home = logitParameters.get("b_home");
		this.b_parttime = logitParameters.get("b_parttime");
		this.b_weibl = logitParameters.get("b_weibl");
	}
	
	public double calculateU_transit_pass(PersonBuilder person) {
		String category = "Transit_pass";
		return   (asc_Ticket)
				+(b_hhgro_2*((helper.getHH_SIZE(category, person) == 2.0d) ? 1.0d : 0.0d))
				+(b_hhgro_3*((helper.getHH_SIZE(category, person) == 3.0d) ? 1.0d : 0.0d))
				+(b_hhgro_4*((helper.getHH_SIZE(category, person) == 4.0d) ? 1.0d : 0.0d))
				+(b_hhgro_5*((helper.getHH_SIZE(category, person) == 5.0d) ? 1.0d : 0.0d))
				+(b_hhgro_6*((helper.getHH_SIZE(category, person) == 6.0d) ? 1.0d : 0.0d))
				+(b_hhgro_7*((helper.getHH_SIZE(category, person) == 7.0d) ? 1.0d : 0.0d))
				+(b_hhgro_8*((8.0d <= helper.getHH_SIZE(category, person) && helper.getHH_SIZE(category, person) <= 20.0d) ? 1.0d : 0.0d))
				+(b_weibl*((helper.getHP_SEX(category, person) == 2.0d) ? 1.0d : 0.0d))
				+(b_alter1a*((0.0d <= helper.getAGE(category, person) && helper.getAGE(category, person) <= 9.0d) ? 1.0d : 0.0d))
				+(b_alter1b*((10.0d <= helper.getAGE(category, person) && helper.getAGE(category, person) <= 17.0d) ? 1.0d : 0.0d))
				+(b_alter3*((30.0d <= helper.getAGE(category, person) && helper.getAGE(category, person) <= 39.0d) ? 1.0d : 0.0d))
				+(b_alter4*((40.0d <= helper.getAGE(category, person) && helper.getAGE(category, person) <= 49.0d) ? 1.0d : 0.0d))
				+(b_alter5*((50.0d <= helper.getAGE(category, person) && helper.getAGE(category, person) <= 59.0d) ? 1.0d : 0.0d))
				+(b_alter6*((60.0d <= helper.getAGE(category, person) && helper.getAGE(category, person) <= 69.0d) ? 1.0d : 0.0d))
				+(b_alter7*((70.0d <= helper.getAGE(category, person) && helper.getAGE(category, person) <= 79.0d) ? 1.0d : 0.0d))
				+(b_alter8*((80.0d <= helper.getAGE(category, person)) ? 1.0d : 0.0d))
				+(b_nofspkw*((helper.getHAS_NO_LICENSE(category, person) == 1.0d) ? 1.0d : 0.0d))
				+(b_pkwhh1*((helper.getPKWHH(category, person) == 1.0d) ? 1.0d : 0.0d))
				+(b_pkwhh2*((helper.getPKWHH(category, person) == 2.0d) ? 1.0d : 0.0d))
				+(b_pkwhh3*((helper.getPKWHH(category, person) == 3.0d) ? 1.0d : 0.0d))
				+(b_pkwhh4*((helper.getPKWHH(category, person) >= 4.0d) ? 1.0d : 0.0d))
				+(b_schueler*((helper.getEMPLOYMENT(category, person) == helper.getSTUDENT_SECONDARY(category, person)) ? 1.0d : 0.0d))
				+(b_schueler*((helper.getEMPLOYMENT(category, person) == helper.getSTUDENT_PRIMARY(category, person)) ? 1.0d : 0.0d))
				+(b_schueler*((helper.getEMPLOYMENT(category, person) == helper.getEDUCATION(category, person)) ? 1.0d : 0.0d))
				+(b_student*((helper.getEMPLOYMENT(category, person) == helper.getSTUDENT_TERTIARY(category, person)) ? 1.0d : 0.0d))
				+(b_home*((helper.getEMPLOYMENT(category, person) == helper.getHOMEKEEPER(category, person)) ? 1.0d : 0.0d))
				+(b_parttime*((helper.getEMPLOYMENT(category, person) == helper.getPARTTIME(category, person)) ? 1.0d : 0.0d))
				+(b_EINKO2*((helper.getINCOME(category, person) == 2.0d) ? 1.0d : 0.0d))
				+(b_EINKO3*((helper.getINCOME(category, person) == 3.0d) ? 1.0d : 0.0d))
				+(b_EINKO4*((helper.getINCOME(category, person) == 4.0d) ? 1.0d : 0.0d))
				+(b_EINKO5*((helper.getINCOME(category, person) == 5.0d) ? 1.0d : 0.0d))
				+(b_EINKO6*((helper.getINCOME(category, person) == 6.0d) ? 1.0d : 0.0d))
				+(b_p05*helper.getP0_5(category, person))
				+(b_p617*helper.getP6_17(category, person));
	}
	public double calculateU_no_transit_pass(PersonBuilder person) {
		String category = "No_Transit_pass";
		return   (0.0d);
	}
}

