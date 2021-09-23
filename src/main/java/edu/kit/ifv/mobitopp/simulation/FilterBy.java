package edu.kit.ifv.mobitopp.simulation;

import java.util.function.BiPredicate;

import edu.kit.ifv.mobitopp.simulation.person.FinishedTrip;
import edu.kit.ifv.mobitopp.time.DayOfWeek;

public class FilterBy {

	public static BiPredicate<Person, FinishedTrip> dayOfWeek(DayOfWeek dayType) {
		return (person, trip) -> trip.startDate().weekDay().equals(dayType);
	}

	public static BiPredicate<Person, FinishedTrip> weekend() {
		return dayOfWeek(DayOfWeek.SATURDAY).or(dayOfWeek(DayOfWeek.SUNDAY));
	}

	public static BiPredicate<Person, FinishedTrip> weekday() {
		return dayOfWeek(DayOfWeek.MONDAY)
				.or(dayOfWeek(DayOfWeek.TUESDAY))
				.or(dayOfWeek(DayOfWeek.WEDNESDAY))
				.or(dayOfWeek(DayOfWeek.THURSDAY))
				.or(dayOfWeek(DayOfWeek.FRIDAY));
	}

	public static BiPredicate<Person, FinishedTrip> hour(int hour) {
		return (person, trip) -> trip.startDate().getHour() == hour;
	}

	public static BiPredicate<Person, FinishedTrip> mode(Mode mode) {
		return (person, trip) -> trip.mode().equals(mode);
	}

}
