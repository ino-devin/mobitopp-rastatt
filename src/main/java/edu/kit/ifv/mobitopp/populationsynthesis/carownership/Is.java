package edu.kit.ifv.mobitopp.populationsynthesis.carownership;

import java.util.EnumSet;

import edu.kit.ifv.mobitopp.simulation.Employment;

public abstract class Is {

  private static final EnumSet<Employment> workingEmployments = EnumSet
      .of(Employment.FULLTIME, Employment.PARTTIME);
  private static final EnumSet<Employment> educationEmployments = EnumSet
      .of(Employment.EDUCATION, Employment.STUDENT, Employment.STUDENT_PRIMARY,
          Employment.STUDENT_SECONDARY, Employment.STUDENT_TERTIARY);

  private Is() {
  }

  public static boolean working(Employment employment) {
    return workingEmployments.contains(employment);
  }

  public static boolean education(Employment employment) {
    return educationEmployments.contains(employment);
  }
}
