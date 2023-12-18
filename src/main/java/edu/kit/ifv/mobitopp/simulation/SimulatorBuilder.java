package edu.kit.ifv.mobitopp.simulation;

import static edu.kit.ifv.mobitopp.time.DayOfWeek.FRIDAY;
import static edu.kit.ifv.mobitopp.time.DayOfWeek.MONDAY;
import static edu.kit.ifv.mobitopp.time.DayOfWeek.SATURDAY;
import static edu.kit.ifv.mobitopp.time.DayOfWeek.SUNDAY;
import static edu.kit.ifv.mobitopp.time.DayOfWeek.THURSDAY;
import static edu.kit.ifv.mobitopp.time.DayOfWeek.TUESDAY;
import static edu.kit.ifv.mobitopp.time.DayOfWeek.WEDNESDAY;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import edu.kit.ifv.mobitopp.concurrent.ParallelExecutor;
import edu.kit.ifv.mobitopp.concurrent.WaitingExecutor;
import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.data.ZoneRepository;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityPeriodFixer;
import edu.kit.ifv.mobitopp.simulation.activityschedule.LeisureWalkActivityPeriodFixer;
import edu.kit.ifv.mobitopp.simulation.activityschedule.randomizer.DefaultActivityDurationRandomizer;
import edu.kit.ifv.mobitopp.simulation.destinationChoice.DestinationChoiceModel;
import edu.kit.ifv.mobitopp.simulation.destinationchoice.DestinationChoiceHelperImpl;
import edu.kit.ifv.mobitopp.simulation.destinationchoice.DestinationChoiceLogger;
import edu.kit.ifv.mobitopp.simulation.destinationchoice.DestinationChoiceModelLoader;
import edu.kit.ifv.mobitopp.simulation.destinationchoice.generated.DestinationChoiceUtilityModelLogger;
import edu.kit.ifv.mobitopp.simulation.destinationchoice.generated.NullDestinationChoiceUtilityModelLogger;
import edu.kit.ifv.mobitopp.simulation.modeChoice.ModeAvailabilityModel;
import edu.kit.ifv.mobitopp.simulation.modeavailability.ModeAvailabilityInDestinationChoice;
import edu.kit.ifv.mobitopp.simulation.modeavailability.ModeAvailabilityModelConsideringBikeSharing;
import edu.kit.ifv.mobitopp.simulation.modechoice.ModeChoiceHelperForDestinationChoice;
import edu.kit.ifv.mobitopp.simulation.modechoice.ModeChoiceLogger;
import edu.kit.ifv.mobitopp.simulation.modechoice.RastattModeResolver;
import edu.kit.ifv.mobitopp.simulation.modechoice.gen.ModeChoiceModelLogger;
import edu.kit.ifv.mobitopp.simulation.modechoice.gen.NullModeChoiceModelLogger;
import edu.kit.ifv.mobitopp.simulation.person.PersonStateSimple;
import edu.kit.ifv.mobitopp.simulation.person.TripFactory;
import edu.kit.ifv.mobitopp.simulation.tour.TourBasedModeChoiceModel;
import edu.kit.ifv.mobitopp.util.parameter.LogitParameters;
import edu.kit.ifv.mobitopp.util.parameter.ParameterFormularParser;
import edu.kit.ifv.mobitopp.visum.export.VisumDmdExportShortTerm;
import lombok.Getter;

public class SimulatorBuilder {

	private static final int SINGLE_THREAD = 1;

	private static final Set<Mode> CHOICE_SET = Set
		.of(StandardMode.CAR, //
			StandardMode.PASSENGER, //
			StandardMode.BIKE, //
			StandardMode.PEDESTRIAN, //
			StandardMode.PUBLICTRANSPORT, //
			StandardMode.CARSHARING_STATION, //
			StandardMode.CARSHARING_FREE //
		);

	private final SimulationContext context;
	private ModeAvailabilityModel modeAvailabilityModel;
	@Getter private VisumDmdExportShortTerm export;

	public SimulatorBuilder(SimulationContext context) {
		this.context = context;
		
		try {
			export = new VisumDmdExportShortTerm(context);
			export.init();
			context.personResults().addListener(export);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private SimulationContext context() {
		return context;
	}

	private ImpedanceIfc impedance() {
		return context.impedance();
	}

	private ZoneRepository zoneRepository() {
		return context.zoneRepository();
	}

	public DemandSimulator build() {
		modeAvailabilityModel = createModeAvailabilityModel();
		DestinationChoiceModel targetSelector = createDestinationChoiceModel();
		TourBasedModeChoiceModel modeSelector = createModeChoiceModel();
		ZoneBasedRouteChoice routeChoice = createRouteChoice();
		ActivityPeriodFixer fixer = new LeisureWalkActivityPeriodFixer();
		DefaultActivityDurationRandomizer randomizer = createDurationRandomizer();
		TripFactory tripFactory = createTripFactory();
		ReschedulingStrategy rescheduling = createReschedulingStrategy();

		System.out.println("Initializing simulator...");
		DemandSimulatorPassenger simulator = new DemandSimulatorPassenger(targetSelector,
				modeSelector, routeChoice, fixer, randomizer, tripFactory, rescheduling, CHOICE_SET,
				PersonStateSimple.UNINITIALIZED, context());

		createMatrixWriters(simulator);
	
		return simulator;
	}


	private ModeAvailabilityModel createModeAvailabilityModel() {
		return new ModeAvailabilityModelConsideringBikeSharing(impedance());
	}

	private DestinationChoiceModel createDestinationChoiceModel() {
		LogitParameters baseParameters = new ParameterFormularParser()
				.parseToParameter(context().destinationChoiceParameters().valueAsFile("base"));

		ModeAvailabilityModel modeAvailabilityDestination = new ModeAvailabilityInDestinationChoice();
		ModeChoiceHelperForDestinationChoice helper = newModeChoiceModelLoader()
				.loadModeChoiceHelper(modeAvailabilityDestination, impedance());
		DestinationChoiceHelperImpl destinationHelper = new DestinationChoiceHelperImpl(impedance(),
				modeAvailabilityDestination, CHOICE_SET, helper);

		DestinationChoiceUtilityModelLogger logger = destinationChoiceLogger();

		return new DestinationChoiceModelLoader(baseParameters, destinationHelper, logger,
				executor()).loadFrom(context());
	}

	private DestinationChoiceUtilityModelLogger destinationChoiceLogger() {
		DestinationChoiceUtilityModelLogger logger = new NullDestinationChoiceUtilityModelLogger();
		if (isParameterSet("logDestinationChoice")) {
			int n = 1;
			if (context().experimentalParameters().hasValue("logDestinationChoiceN")) {
				n = context().experimentalParameters().valueAsInteger("logDestinationChoiceN");
			}
			logger = new DestinationChoiceLogger(n);
		}
		return logger;
	}

	private WaitingExecutor executor() {
		return new ParallelExecutor(numberOfThreads());
	}

	private int numberOfThreads() {
		if (context.experimentalParameters().hasValue("threads")) {
			return context.experimentalParameters().valueAsInteger("threads");
		}
		return SINGLE_THREAD;
	}

	private TourBasedModeChoiceModel createModeChoiceModel() {
		return newModeChoiceModelLoader().loadModel();
	}

	private NoRouteChoice createRouteChoice() {
		return new NoRouteChoice();
	}

	private DefaultActivityDurationRandomizer createDurationRandomizer() {
		return new DefaultActivityDurationRandomizer(context().seed());
	}

	private TripFactory createTripFactory() {
		return newModeChoiceModelLoader().createTripFactory();
	}

	private ReschedulingSkipTillHome createReschedulingStrategy() {
		return new ReschedulingSkipTillHome(context().simulationDays());
	}

	private ModeChoiceModelLoader newModeChoiceModelLoader() {
		ModeChoiceModelLogger loggerMain = new NullModeChoiceModelLogger();
		if (isParameterSet("logModeChoice")) {
			int n = 1;
			if (context().experimentalParameters().hasValue("logModeChoiceN")) {
				n = context().experimentalParameters().valueAsInteger("logModeChoiceN");
			}
			loggerMain = new ModeChoiceLogger(n, context().results(), new RastattModeResolver());
		}

		return new ModeChoiceModelLoader(modeAvailabilityModel, context(),
				impedance(), loggerMain);
	}


	private void createMatrixWriters(DemandSimulatorPassenger simulator) {
		if (isParameterSet("writeMatrices")) {
			List<ZoneId> zoneIds = context.dataRepository().zoneRepository().getZoneIds();


			ActiveListenersManagerBuilder builder = new ActiveListenersManagerBuilder();

			if (isParameterSet("dayMatrices")) {
				builder.addTimeSlicesBetween(zoneIds, CHOICE_SET, Set.of(MONDAY), context, 0, 24, 24);
				builder.addTimeSlicesBetween(zoneIds, CHOICE_SET, Set.of(TUESDAY), context, 0, 24, 24);
				builder.addTimeSlicesBetween(zoneIds, CHOICE_SET, Set.of(WEDNESDAY), context, 0, 24, 24);
				builder.addTimeSlicesBetween(zoneIds, CHOICE_SET, Set.of(THURSDAY), context, 0, 24, 24);
				builder.addTimeSlicesBetween(zoneIds, CHOICE_SET, Set.of(FRIDAY), context, 0, 24, 24);
				builder.addTimeSlicesBetween(zoneIds, CHOICE_SET, Set.of(SATURDAY), context, 0, 24, 24);
				builder.addTimeSlicesBetween(zoneIds, CHOICE_SET, Set.of(SUNDAY), context, 0, 24, 24);

			} else if (isParameterSet("hourMatrices")) {
				builder
						.addHourlyWeekdayListeners(zoneIds, matrixModes(), context)
						.addHourlyDayListeners(zoneIds, matrixModes(), SATURDAY, context)
						.addHourlyDayListeners(zoneIds, matrixModes(), SUNDAY, context);
			}

			builder.logPlannedSchedule()
					.performActionsAtEnd()
					.useThreads(context.configuration().getThreadCount())
					.register(simulator)
					.build();
		}
	}

	private Set<Mode> matrixModes() {
		return Set
				.of(StandardMode.BIKE, //
						StandardMode.BIKESHARING, //
						StandardMode.CAR, //
						StandardMode.CARSHARING_STATION, //
						StandardMode.CARSHARING_FREE, //
						StandardMode.PUBLICTRANSPORT, //
						StandardMode.RIDE_POOLING, //
						StandardMode.TAXI //
				);
	}

	private boolean isParameterSet(String parameter) {
		return context.experimentalParameters().hasValue(parameter)
				&& context.experimentalParameters().valueAsBoolean(parameter);
	}

}
