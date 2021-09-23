package edu.kit.ifv.mobitopp.simulation;

import static java.util.stream.Collectors.joining;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.simulation.modeChoice.ModeChoicePreferences;

public class ModePreferencesWriter {

	private static final String delimiter = ",";

	private final SimulationContext context;

	public ModePreferencesWriter(SimulationContext context) {
		this.context = context;
	}

	public void writeInfluencingFaktors() {
		writePreferences();
		writeSensitivities();
	}


	private void writePreferences() {
		File file = context.modeChoiceParameters().valueAsFile("preferencesFile");
		Function<Person, ModeChoicePreferences> convert = Person::modeChoicePreferences;
		write(file, convert);
	}
	
	private void writeSensitivities() {
		File file = context.modeChoiceParameters().valueAsFile("travelTimeSensitivitiesFile");
		Function<Person, ModeChoicePreferences> convert = Person::travelTimeSensitivity;
		write(file, convert);
	}

	private void write(File preferencesFile, Function<Person, ModeChoicePreferences> convert) {
		if (!preferencesFile.getParentFile().exists()) {
			preferencesFile.getParentFile().mkdirs();
		}
		try (PrintStream writer = new PrintStream(preferencesFile)) {
			writer.println(header());
			context
					.dataRepository()
					.personLoader()
					.households()
					.flatMap(Household::persons)
					.map(person -> toModeChoicePreferences(person, convert))
					.forEach(writer::println);
		} catch (FileNotFoundException cause) {
			throw new UncheckedIOException(cause);
		}
	}

	private String header() {
		return "personId" + delimiter + modes().map(Mode::toString).collect(joining(delimiter));
	}

	private Stream<Mode> modes() {
		return Arrays.stream(StandardMode.values());
	}

	private String toModeChoicePreferences(
			Person person, Function<Person, ModeChoicePreferences> convert) {
		Map<Mode, Double> preferences = convert.apply(person).asMap();
		return person.getId().getOid() + delimiter
				+ modes()
						.map(p -> preferences.getOrDefault(p, Double.NaN))
						.map(Objects::toString)
						.collect(joining(delimiter));
	}

}
