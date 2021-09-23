package edu.kit.ifv.mobitopp.populationsynthesis.carownership;

import static java.lang.Math.exp;
import static java.lang.Math.log;
import static java.util.stream.Collectors.toMap;

import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.function.DoubleSupplier;

import edu.kit.ifv.mobitopp.populationsynthesis.HouseholdForSetup;
import edu.kit.ifv.mobitopp.populationsynthesis.PersonBuilder;
import edu.kit.ifv.mobitopp.simulation.Employment;
import edu.kit.ifv.mobitopp.simulation.Graduation;
import edu.kit.ifv.mobitopp.util.parameter.LogitParameters;
import edu.kit.ifv.mobitopp.util.randomvariable.DiscreteRandomVariable;

public abstract class NestedLogitSelector implements NumberOfCarsSelector {

	private final double b_hh_size_1_on_1;
	private final double b_hh_size_1_on_2;
	private final double b_hh_size_1_on_3;
	private final double b_hh_size_1_on_4;
	private final double b_hh_size_2_on_1;
	private final double b_hh_size_2_on_2;
	private final double b_hh_size_2_on_3;
	private final double b_hh_size_2_on_4;
	private final double b_hh_size_4_on_1;
	private final double b_hh_size_4_on_2;
	private final double b_hh_size_4_on_3;
	private final double b_hh_size_4_on_4;
	private final double b_income_low_on_1;
	private final double b_income_low_on_2;
	private final double b_income_low_on_3;
	private final double b_income_low_on_4;
	private final double b_income_high_on_1;
	private final double b_income_high_on_2;
	private final double b_income_high_on_3;
	private final double b_income_high_on_4;
	private final double b_education_1_on_1;
	private final double b_education_1_on_2;
	private final double b_education_1_on_3;
	private final double b_education_1_on_4;
	private final double b_education_2_on_1;
	private final double b_education_2_on_2;
	private final double b_education_2_on_3;
	private final double b_education_2_on_4;
	private final double b_person_age_0_9_on_1;
	private final double b_person_age_0_9_on_2;
	private final double b_person_age_0_9_on_3;
	private final double b_person_age_0_9_on_4;
	private final double b_person_age_10_17_on_1;
	private final double b_person_age_10_17_on_2;
	private final double b_person_age_10_17_on_3;
	private final double b_person_age_10_17_on_4;
	private final double b_person_working_1_on_1;
	private final double b_person_working_1_on_2;
	private final double b_person_working_1_on_3;
	private final double b_person_working_1_on_4;
	private final double b_person_working_2_on_1;
	private final double b_person_working_2_on_2;
	private final double b_person_working_2_on_3;
	private final double b_person_working_2_on_4;
	private final double b_license_1_on_1;
	private final double b_license_1_on_2;
	private final double b_license_1_on_3;
	private final double b_license_1_on_4;
	private final double b_license_2_on_1;
	private final double b_license_2_on_2;
	private final double b_license_2_on_3;
	private final double b_license_2_on_4;
	private final double b_license_3_on_1;
	private final double b_license_3_on_2;
	private final double b_license_3_on_3;
	private final double b_license_3_on_4;
	private final double b_license_4_on_1;
	private final double b_license_4_on_2;
	private final double b_license_4_on_3;
	private final double b_license_4_on_4;
	private final double b_shared_flat_on_1;
	private final double b_shared_flat_on_2;
	private final double b_shared_flat_on_3;
	private final double b_shared_flat_on_4;
	private final double b_hh_retired_on_1;
	private final double b_hh_retired_on_2;
	private final double b_hh_retired_on_3;
	private final double b_hh_retired_on_4;
	private final double b_hh_unemployed_on_1;
	private final double b_hh_unemployed_on_2;
	private final double b_hh_unemployed_on_3;
	private final double b_hh_unemployed_on_4;
	private final double lambda_root;
	private final double lambda_car;
	private final double lambda_two_more_car;

  private final DoubleSupplier randomGenerator;

  public NestedLogitSelector(DoubleSupplier randomGenerator, LogitParameters parameters) {
    super();
    this.randomGenerator = randomGenerator;
  	b_hh_size_1_on_1 = parameters.get("b_hh_size_1_on_1");
  	b_hh_size_1_on_2 = parameters.get("b_hh_size_1_on_2");
  	b_hh_size_1_on_3 = parameters.get("b_hh_size_1_on_3");
  	b_hh_size_1_on_4 = parameters.get("b_hh_size_1_on_4");
  	b_hh_size_2_on_1 = parameters.get("b_hh_size_2_on_1");
  	b_hh_size_2_on_2 = parameters.get("b_hh_size_2_on_2");
  	b_hh_size_2_on_3 = parameters.get("b_hh_size_2_on_3");
  	b_hh_size_2_on_4 = parameters.get("b_hh_size_2_on_4");
  	b_hh_size_4_on_1 = parameters.get("b_hh_size_4_on_1");
  	b_hh_size_4_on_2 = parameters.get("b_hh_size_4_on_2");
  	b_hh_size_4_on_3 = parameters.get("b_hh_size_4_on_3");
  	b_hh_size_4_on_4 = parameters.get("b_hh_size_4_on_4");
  	b_income_low_on_1 = parameters.get("b_income_low_on_1");
  	b_income_low_on_2 = parameters.get("b_income_low_on_2");
  	b_income_low_on_3 = parameters.get("b_income_low_on_3");
  	b_income_low_on_4 = parameters.get("b_income_low_on_4");
  	b_income_high_on_1 = parameters.get("b_income_high_on_1");
  	b_income_high_on_2 = parameters.get("b_income_high_on_2");
  	b_income_high_on_3 = parameters.get("b_income_high_on_3");
  	b_income_high_on_4 = parameters.get("b_income_high_on_4");
  	b_education_1_on_1 = parameters.get("b_education_1_on_1");
  	b_education_1_on_2 = parameters.get("b_education_1_on_2");
  	b_education_1_on_3 = parameters.get("b_education_1_on_3");
  	b_education_1_on_4 = parameters.get("b_education_1_on_4");
  	b_education_2_on_1 = parameters.get("b_education_2_on_1");
  	b_education_2_on_2 = parameters.get("b_education_2_on_2");
  	b_education_2_on_3 = parameters.get("b_education_2_on_3");
  	b_education_2_on_4 = parameters.get("b_education_2_on_4");
  	b_person_age_0_9_on_1 = parameters.get("b_person_age_0_9_on_1");
  	b_person_age_0_9_on_2 = parameters.get("b_person_age_0_9_on_2");
  	b_person_age_0_9_on_3 = parameters.get("b_person_age_0_9_on_3");
  	b_person_age_0_9_on_4 = parameters.get("b_person_age_0_9_on_4");
  	b_person_age_10_17_on_1 = parameters.get("b_person_age_10_17_on_1");
  	b_person_age_10_17_on_2 = parameters.get("b_person_age_10_17_on_2");
  	b_person_age_10_17_on_3 = parameters.get("b_person_age_10_17_on_3");
  	b_person_age_10_17_on_4 = parameters.get("b_person_age_10_17_on_4");
  	b_person_working_1_on_1 = parameters.get("b_person_working_1_on_1");
  	b_person_working_1_on_2 = parameters.get("b_person_working_1_on_2");
  	b_person_working_1_on_3 = parameters.get("b_person_working_1_on_3");
  	b_person_working_1_on_4 = parameters.get("b_person_working_1_on_4");
  	b_person_working_2_on_1 = parameters.get("b_person_working_2_on_1");
  	b_person_working_2_on_2 = parameters.get("b_person_working_2_on_2");
  	b_person_working_2_on_3 = parameters.get("b_person_working_2_on_3");
  	b_person_working_2_on_4 = parameters.get("b_person_working_2_on_4");
  	b_license_1_on_1 = parameters.get("b_license_1_on_1");
  	b_license_1_on_2 = parameters.get("b_license_1_on_2");
  	b_license_1_on_3 = parameters.get("b_license_1_on_3");
  	b_license_1_on_4 = parameters.get("b_license_1_on_4");
  	b_license_2_on_1 = parameters.get("b_license_2_on_1");
  	b_license_2_on_2 = parameters.get("b_license_2_on_2");
  	b_license_2_on_3 = parameters.get("b_license_2_on_3");
  	b_license_2_on_4 = parameters.get("b_license_2_on_4");
  	b_license_3_on_1 = parameters.get("b_license_3_on_1");
  	b_license_3_on_2 = parameters.get("b_license_3_on_2");
  	b_license_3_on_3 = parameters.get("b_license_3_on_3");
  	b_license_3_on_4 = parameters.get("b_license_3_on_4");
  	b_license_4_on_1 = parameters.get("b_license_4_on_1");
  	b_license_4_on_2 = parameters.get("b_license_4_on_2");
  	b_license_4_on_3 = parameters.get("b_license_4_on_3");
  	b_license_4_on_4 = parameters.get("b_license_4_on_4");
  	b_shared_flat_on_1 = parameters.get("b_shared_flat_on_1");
  	b_shared_flat_on_2 = parameters.get("b_shared_flat_on_2");
  	b_shared_flat_on_3 = parameters.get("b_shared_flat_on_3");
  	b_shared_flat_on_4 = parameters.get("b_shared_flat_on_4");
  	b_hh_retired_on_1 = parameters.get("b_hh_retired_on_1");
  	b_hh_retired_on_2 = parameters.get("b_hh_retired_on_2");
  	b_hh_retired_on_3 = parameters.get("b_hh_retired_on_3");
  	b_hh_retired_on_4 = parameters.get("b_hh_retired_on_4");
  	b_hh_unemployed_on_1 = parameters.get("b_hh_unemployed_on_1");
  	b_hh_unemployed_on_2 = parameters.get("b_hh_unemployed_on_2");
  	b_hh_unemployed_on_3 = parameters.get("b_hh_unemployed_on_3");
  	b_hh_unemployed_on_4 = parameters.get("b_hh_unemployed_on_4");
  	lambda_root = 1.0d;
  	lambda_car = parameters.get("lambda_car");
  	lambda_two_more_car = parameters.get("lambda_two_more_car");
  }

  @Override
  public int calculateNumberOfCarsFor(HouseholdForSetup household) {
  	double u_no_car = asc_0();
	  double u_one_car = asc_1()  +b_hh_size_1_on_1*(hhgr_gr(household, 1)) +  b_hh_size_2_on_1*(hhgr_gr(household, 2)) + b_hh_size_4_on_1*(hhgr_gr(household, 4))
	                      + b_income_low_on_1*(oek_status(household, 1, 2)) + b_income_high_on_1*(oek_status(household, 4, 5)) 
	                      + b_education_1_on_1 * (education(household, 1)) + b_education_2_on_1 * (education(household, 2))
	                      + b_person_age_0_9_on_1*(p0_9(household)) + b_person_age_10_17_on_1*(person_age_10_17(household)) 
	                      + b_person_working_1_on_1*(p_erw(household, 1)) +b_person_working_2_on_1*(p_erw(household, 2))
	                      + b_license_1_on_1*(p_fs(household, 1)) + b_license_2_on_1*(p_fs(household, 2)) + b_license_3_on_1*(p_fs(household, 3))  + b_license_4_on_1*(p_fs(household, 4))
	                      + b_shared_flat_on_1*(wg(household)) + b_hh_retired_on_1*(hh_retired(household)) + b_hh_unemployed_on_1*(hh_unemployed(household));
	                     
	  double u_two_car = asc_2()  +b_hh_size_1_on_2*(hhgr_gr(household, 1)) +  b_hh_size_2_on_2*(hhgr_gr(household, 2)) + b_hh_size_4_on_2*(hhgr_gr(household, 4)) 
	                      + b_income_low_on_2*(oek_status(household, 1, 2)) + b_income_high_on_2*(oek_status(household, 4, 5))
	                      + b_education_1_on_2 * (education(household, 1)) + b_education_2_on_2 * (education(household, 2))
	                      + b_person_age_0_9_on_2*(p0_9(household)) + b_person_age_10_17_on_2*(person_age_10_17(household))
	                      + b_person_working_1_on_2*(p_erw(household, 1)) + b_person_working_2_on_2*(p_erw(household, 2)) 
	                      + b_license_1_on_2*(p_fs(household, 1)) + b_license_2_on_2*(p_fs(household, 2)) + b_license_3_on_2*(p_fs(household, 3))  + b_license_4_on_2*(p_fs(household, 4))
	                      + b_shared_flat_on_2*(wg(household)) + b_hh_retired_on_2*(hh_retired(household)) + b_hh_unemployed_on_2*(hh_unemployed(household));
	                      
	  double u_three_car = asc_3()  +b_hh_size_1_on_3*(hhgr_gr(household, 1)) +  b_hh_size_2_on_3*(hhgr_gr(household, 2)) + b_hh_size_4_on_3*(hhgr_gr(household, 4)) 
	                        + b_income_low_on_3*(oek_status(household, 1, 2)) + b_income_high_on_3*(oek_status(household, 4, 5))
	                        + b_education_1_on_3 * (education(household, 1)) + b_education_2_on_3 * (education(household, 2))
	                        + b_person_age_0_9_on_3*(p0_9(household)) + b_person_age_10_17_on_3*(person_age_10_17(household))
	                        + b_person_working_1_on_3*(p_erw(household, 1)) + b_person_working_2_on_3*(p_erw(household, 2)) 
	                        + b_license_1_on_3*(p_fs(household, 1)) + b_license_2_on_3*(p_fs(household, 2)) + b_license_3_on_3*(p_fs(household, 3))  + b_license_4_on_3*(p_fs(household, 4))
	                        + b_shared_flat_on_3*(wg(household)) + b_hh_retired_on_3*(hh_retired(household)) + b_hh_unemployed_on_3*(hh_unemployed(household));
	                      
	  double u_four_car = asc_4()  +b_hh_size_1_on_4*(hhgr_gr(household, 1)) +  b_hh_size_2_on_4*(hhgr_gr(household, 2)) + b_hh_size_4_on_4*(hhgr_gr(household, 4)) 
	                       + b_income_low_on_4*(oek_status(household, 1, 2)) + b_income_high_on_4*(oek_status(household, 4, 5))
	                       + b_education_1_on_4 * (education(household, 1)) + b_education_2_on_4 * (education(household, 2))
	                       + b_person_age_0_9_on_4*(p0_9(household)) + b_person_age_10_17_on_4*(person_age_10_17(household))
	                       + b_person_working_1_on_4*(p_erw(household, 1)) + b_person_working_2_on_4*(p_erw(household, 2)) 
	                       + b_license_1_on_4*(p_fs(household, 1)) + b_license_2_on_4*(p_fs(household, 2)) + b_license_3_on_4*(p_fs(household, 3))  + b_license_4_on_4*(p_fs(household, 4))
	                       + b_shared_flat_on_4*(wg(household)) + b_hh_retired_on_4*(hh_retired(household)) + b_hh_unemployed_on_4*(hh_unemployed(household));
	   
	  double exp_u_two_car = exp(u_two_car / lambda_two_more_car);
		double exp_u_three_car = exp(u_three_car / lambda_two_more_car);
		double exp_u_four_car = exp(u_four_car / lambda_two_more_car);
		double nest_two_more_car_sum = exp_u_two_car + exp_u_three_car + exp_u_four_car;
	  double p_two_car_in_nest_two_more_car = exp_u_two_car / nest_two_more_car_sum;
	  double p_three_car_in_nest_two_more_car = exp_u_three_car / nest_two_more_car_sum;
	  double p_four_car_in_nest_two_more_car = exp_u_four_car / nest_two_more_car_sum;
	  
	  double logsum_nest_two_more_car = log(exp_u_two_car + exp_u_three_car + exp_u_four_car);
	  
	  double exp_u_one_car = exp(u_one_car / lambda_car);
	  double nest_one_car_sum = exp_u_one_car + exp((logsum_nest_two_more_car * lambda_two_more_car) / lambda_car);
	  double p_one_car_in_nest_one_car = exp_u_one_car / nest_one_car_sum;
	  double p_two_more_in_nest_one_car = exp((logsum_nest_two_more_car * lambda_two_more_car) / lambda_car) / nest_one_car_sum;
	  
	  double logsum_nest_one_car = log(exp_u_one_car + exp((logsum_nest_two_more_car * lambda_two_more_car)/lambda_car));
	  
	  double exp_u_no_car = exp(u_no_car / lambda_root);
	  double nest_root_sum = exp_u_no_car + exp((logsum_nest_one_car * lambda_car) / lambda_root);
	  
	  double p_no_car = exp_u_no_car / nest_root_sum;
	  double p_nest_one_car = exp(lambda_car * logsum_nest_one_car) / nest_root_sum;
	  double p_one_car = p_nest_one_car * p_one_car_in_nest_one_car;
	  double p_two_car = p_nest_one_car * p_two_more_in_nest_one_car * p_two_car_in_nest_two_more_car;
	  double p_three_car = p_nest_one_car * p_two_more_in_nest_one_car * p_three_car_in_nest_two_more_car;
	  double p_four_car = p_nest_one_car * p_two_more_in_nest_one_car * p_four_car_in_nest_two_more_car;
	  
	  Map<Integer, Double> probabilities = new TreeMap<>();
    probabilities.put(0, p_no_car);
    probabilities.put(1, p_one_car);
    probabilities.put(2, p_two_car);
    probabilities.put(3, p_three_car);
    probabilities.put(4, p_four_car);

    Map<Integer, Double> filteredProbabilities = probabilities
        .entrySet()
        .stream()
        .filter(e -> !Double.isNaN(e.getValue()))
        .collect(toMap(Entry::getKey, Entry::getValue));
      
    DiscreteRandomVariable<Integer> distribution = new DiscreteRandomVariable<>(filteredProbabilities);
    return distribution.realization(nextRandom());
  }

  protected abstract double asc_0();
  protected abstract double asc_1();
  protected abstract double asc_2();
  protected abstract double asc_3();
	protected abstract double asc_4();
  
  private int hhgr_gr(HouseholdForSetup household, int size) {
  	return Math.min(household.getSize(), 4) == size ? 1 : 0;
  }
  
  private int oek_status(HouseholdForSetup household, int lowerBound, int upperBound) {
  	int status = household.economicalStatus().getCode();
		return lowerBound <= status || upperBound >= status ? 1 : 0;
  }
  
  private int education(HouseholdForSetup household, int highesteducationation) {
		return highesteducationation == getHighestEducationationOf(household) ? 1 : 0;
  }

	private int getHighestEducationationOf(HouseholdForSetup household) {
		return household
				.persons()
				.map(PersonBuilder::graduation)
				.mapToInt(Graduation::getNumeric)
				.max()
				.orElseThrow(
						() -> new IllegalArgumentException("Household is empty or no person is graduated"));
	}
  
  private int p0_9(HouseholdForSetup household) {
  	return household.persons().anyMatch(p -> 0 <= p.age() && 9 >= p.age()) ? 1 : 0;
  }
  
  private int person_age_10_17(HouseholdForSetup household) {
  	return household.persons().anyMatch(p -> 10 <= p.age() && 17 >= p.age()) ? 1 : 0;
  }
  
	private int p_erw(HouseholdForSetup household, int count) {
		return is_erw(household, count) ? 1 : 0;
	}

	private boolean is_erw(HouseholdForSetup household, int count) {
		return count == Math
				.min(Math.toIntExact(household.persons().filter(this::isWorking).count()), 2);
	}
	
	private int p_fs(HouseholdForSetup household, int count) {
		return count == Math.min(drivingLicensesIn(household), 4) ? 1 : 0;
	}
	
	private int wg(HouseholdForSetup household) {
		return is_wg(household) ? 1 : 0;
	}

	private boolean is_wg(HouseholdForSetup household) {
		long numberOfStudents = household.persons().filter(this::isStudent).count();
		return 3 <= numberOfStudents || numberOfStudents == household.getSize();
	}
	
	private int hh_retired(HouseholdForSetup household) {
		return is_hh_retired(household) ? 1 : 0;
	}

	private boolean is_hh_retired(HouseholdForSetup household) {
		return household.persons().filter(this::isRetired).count() == household.getSize();
	}
	
	private int hh_unemployed(HouseholdForSetup household) {
		return is_erw(household, 0) && !is_hh_retired(household) && !is_wg(household) ? 1 : 0;
	}
  
  private boolean isWorking(PersonBuilder person) {
    return Is.working(person.employment());
  }
  
  private boolean isStudent(PersonBuilder person) {
  	return Employment.STUDENT_TERTIARY.equals(person.employment());
  }
  
  private boolean isRetired(PersonBuilder person) {
  	return Employment.RETIRED.equals(person.employment());
  }

  private int drivingLicensesIn(HouseholdForSetup household) {
    return Math
        .toIntExact(
            household.getPersons().stream().filter(PersonBuilder::hasDrivingLicense).count());
  }

  private double nextRandom() {
    return randomGenerator.getAsDouble();
  }

}
