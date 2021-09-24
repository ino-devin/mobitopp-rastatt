package edu.kit.ifv.mobitopp.populationsynthesis.commutationticket;

import java.util.function.DoubleSupplier;

import edu.kit.ifv.mobitopp.populationsynthesis.PersonBuilder;
import edu.kit.ifv.mobitopp.simulation.Employment;

public class CommutationTicketOwnershipHelper implements GeneratedCommutationTicketOwnershipHelper {

	private final DoubleSupplier random;

	public CommutationTicketOwnershipHelper(DoubleSupplier random) {
		super();
		this.random = random;
	}

	@Override
	public double getAGE(String category, PersonBuilder person) {
		return person.age();
	}

	@Override
	public double getINCOME(String category, PersonBuilder person) {
		int income = person.household().monthlyIncomeEur();
		if (750 > income) {
			return 1;
		}
		if (750 <= income && 1500 > income) {
			return 2;
		}
		if (1500 <= income && 2250 > income) {
			return 3;
		}
		if (2250 <= income && 3000 > income) {
			return 4;
		}
		if (3000 <= income && 4000 > income) {
			return 5;
		}
		return 6;
	}

	@Override
	public double getHH_SIZE(String category, PersonBuilder person) {
		return Math.min(8, person.household().getSize());
	}

	@Override
	public double getHP_SEX(String category, PersonBuilder person) {
		return person.gender().getTypeAsInt();
	}

	@Override
	public double getP0_5(String category, PersonBuilder person) {
		return person.household().persons().filter(p -> 5 >= p.age()).count();
	}

	@Override
	public double getP6_17(String category, PersonBuilder person) {
		return person.household().persons().filter(p -> 6 <= p.age() && 17 >= p.age()).count();
	}

	@Override
	public double getPKWHH(String category, PersonBuilder person) {
		return Math.min(4, person.household().getNumberOfOwnedCars());
	}

	@Override
	public double getEMPLOYMENT(String category, PersonBuilder person) {
		return person.employment().getTypeAsInt();
	}

	@Override
	public double getHAS_NO_LICENSE(String category, PersonBuilder person) {
		return person.hasDrivingLicense() ? 0.0d : 1.0d;
	}

	@Override
	public double getSTUDENT_PRIMARY(String category, PersonBuilder person) {
		return Employment.STUDENT_PRIMARY.getTypeAsInt();
	}

	@Override
	public double getSTUDENT_SECONDARY(String category, PersonBuilder person) {
		return Employment.STUDENT_SECONDARY.getTypeAsInt();
	}

	@Override
	public double getSTUDENT_TERTIARY(String category, PersonBuilder person) {
		return Employment.STUDENT_TERTIARY.getTypeAsInt();
	}
	
	@Override
	public double getEDUCATION(String category, PersonBuilder person) {
		return Employment.EDUCATION.getTypeAsInt();
	}
	
	@Override
	public double getHOMEKEEPER(String category, PersonBuilder person) {
		return Employment.HOMEKEEPER.getTypeAsInt();
	}
	
	@Override
	public double getPARTTIME(String category, PersonBuilder person) {
		return Employment.PARTTIME.getTypeAsInt();
	}

	@Override
	public double getRandom(PersonBuilder person) {
		return random.getAsDouble();
	}

	@Override
	public boolean isAvailable(String category, PersonBuilder person) {
		return true;
	}

	@Override
	public boolean resolve(String category) {
		return "Transit_pass".equals(category);
	}

}
