package edu.kit.ifv.mobitopp.simulation;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

import edu.kit.ifv.mobitopp.Disclaimer;
import edu.kit.ifv.mobitopp.VisumDmdExportShortTerm;
import edu.kit.ifv.mobitopp.data.local.DynamicTypeMapping;
import edu.kit.ifv.mobitopp.data.local.configuration.TravelTimeMatrixType;
import edu.kit.ifv.mobitopp.populationsynthesis.DefaultMappings;
import edu.kit.ifv.mobitopp.util.dataimport.Bbsr17Repository;
import edu.kit.ifv.mobitopp.visum.StandardNetfileLanguages;

public class ShortTermModule extends Simulation {

	private final ModePreferencesWriter writer;
	private VisumDmdExportShortTerm export;

	public ShortTermModule(SimulationContext context) {
		super(context);
		writer = new ModePreferencesWriter(context());
	}

	@Override
	public void simulate() {
		writer.writeInfluencingFaktors();
		super.simulate();
		
		if (export != null) {
			export.finish();
		}
	}

	@Override
	protected DemandSimulator simulator() {
		SimulatorBuilder builder = new SimulatorBuilder(context());
		this.export = builder.getExport();
		return builder.build();
	}
	
	public static void main(String... args) throws IOException {
		if (1 > args.length) {
			System.out.println("Usage: ... <configuration file>");
			System.exit(-1);
		}
		
		Disclaimer.disclaim();

		File configurationFile = new File(args[0]);
		LocalDateTime start = LocalDateTime.now();
		startSimulation(configurationFile);
		LocalDateTime end = LocalDateTime.now();
		Duration runtime = Duration.between(start, end);
		System.out.println("Simulation took " + runtime);
	}

	public static void startSimulation(File configurationFile) throws IOException {
		PersonChangerFactory factory = new MixedPersonChangerFactory();
		SimulationContext context = new ContextBuilder(new Bbsr17Repository())
				.use(StandardNetfileLanguages::english)
				.use(typeMapping())
				.use(factory)
				.buildFrom(configurationFile);
		startSimulation(context);
	}

	private static DynamicTypeMapping typeMapping() {
		DynamicTypeMapping mapping = DefaultMappings.autonomousModes();
		mapping.add(StandardMode.TAXI, TravelTimeMatrixType.car);
		return mapping;
	}

	public static void startSimulation(SimulationContext context) {
		new ShortTermModule(context).simulate();
	}
	
}
