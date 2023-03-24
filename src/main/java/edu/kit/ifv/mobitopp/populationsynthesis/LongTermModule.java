package edu.kit.ifv.mobitopp.populationsynthesis;

import static edu.kit.ifv.mobitopp.util.collections.StreamUtils.toLinkedMap;
import static java.util.function.Function.identity;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.BiPredicate;
import java.util.function.DoubleSupplier;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.concurrent.ParallelHouseholdStep;
import edu.kit.ifv.mobitopp.concurrent.ParallelPersonStep;
import edu.kit.ifv.mobitopp.data.DemandRegion;
import edu.kit.ifv.mobitopp.data.FixedDistributionMatrix;
import edu.kit.ifv.mobitopp.data.PanelDataRepository;
import edu.kit.ifv.mobitopp.data.local.Convert;
import edu.kit.ifv.mobitopp.data.local.configuration.DynamicParameters;
import edu.kit.ifv.mobitopp.populationsynthesis.calculator.DemandDataCalculator;
import edu.kit.ifv.mobitopp.populationsynthesis.carownership.AssignCars;
import edu.kit.ifv.mobitopp.populationsynthesis.carownership.CarOwnershipModel;
import edu.kit.ifv.mobitopp.populationsynthesis.carownership.CarSegmentModel;
import edu.kit.ifv.mobitopp.populationsynthesis.carownership.ElectricCarOwnership;
import edu.kit.ifv.mobitopp.populationsynthesis.carownership.GenericElectricCarOwnershipModel;
import edu.kit.ifv.mobitopp.populationsynthesis.carownership.LogitBasedCarSegmentModel;
import edu.kit.ifv.mobitopp.populationsynthesis.carownership.MobilityProviderCustomerModel;
import edu.kit.ifv.mobitopp.populationsynthesis.carownership.NumberOfCarsSelector;
import edu.kit.ifv.mobitopp.populationsynthesis.carownership.ProbabilityForElectricCarOwnershipModel;
import edu.kit.ifv.mobitopp.populationsynthesis.carownership.SimpleCarOwnershipModel;
import edu.kit.ifv.mobitopp.populationsynthesis.carownership.StaticSelector;
import edu.kit.ifv.mobitopp.populationsynthesis.carownership.StochasticSelector;
import edu.kit.ifv.mobitopp.populationsynthesis.commutation.CommuterRelations;
import edu.kit.ifv.mobitopp.populationsynthesis.commutationticket.CommutationTicketOwnershipHelper;
import edu.kit.ifv.mobitopp.populationsynthesis.commutationticket.GeneratedCommutationTicketOwnership;
import edu.kit.ifv.mobitopp.populationsynthesis.commutationticket.GeneratedCommutationTicketOwnershipHelper;
import edu.kit.ifv.mobitopp.populationsynthesis.commutationticket.GeneratedCommutationTicketOwnershipUtilityFunction;
import edu.kit.ifv.mobitopp.populationsynthesis.commutationticket.LogitBasedCommutationTicketOwnershipModel;
import edu.kit.ifv.mobitopp.populationsynthesis.fixeddestination.EducationDestinationSelector;
import edu.kit.ifv.mobitopp.populationsynthesis.fixeddestinations.DemandRegionBasedZoneSelector;
import edu.kit.ifv.mobitopp.populationsynthesis.fixeddestinations.DemandRegionDestinationSelector;
import edu.kit.ifv.mobitopp.populationsynthesis.fixeddestinations.HasWorkActivity;
import edu.kit.ifv.mobitopp.populationsynthesis.fixeddestinations.ZoneSelector;
import edu.kit.ifv.mobitopp.populationsynthesis.householdlocation.EdgeFilter;
import edu.kit.ifv.mobitopp.populationsynthesis.householdlocation.HouseholdLocationSelector;
import edu.kit.ifv.mobitopp.populationsynthesis.householdlocation.RoadBasedHouseholdLocationSelector;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.AttributeType;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.DemandCreatorFactory;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.ProbabilityBasedSelector;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.StandardAttribute;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.StructuralDataDemandCreatorFactory;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.WeightedHouseholdSelector;
import edu.kit.ifv.mobitopp.populationsynthesis.opportunities.OpportunityLocationSelector;
import edu.kit.ifv.mobitopp.populationsynthesis.opportunities.RoadBasedOpportunitySelector;
import edu.kit.ifv.mobitopp.populationsynthesis.region.DemandRegionOdPairCreator;
import edu.kit.ifv.mobitopp.populationsynthesis.region.DemandRegionOdPairSelector;
import edu.kit.ifv.mobitopp.populationsynthesis.region.DemandRegionRelationsParser;
import edu.kit.ifv.mobitopp.populationsynthesis.region.DemandRegionRelationsRepository;
import edu.kit.ifv.mobitopp.populationsynthesis.region.FilterStep;
import edu.kit.ifv.mobitopp.populationsynthesis.region.HouseholdBasedStep;
import edu.kit.ifv.mobitopp.populationsynthesis.region.PanelDistanceSelector;
import edu.kit.ifv.mobitopp.populationsynthesis.region.PersonBasedStep;
import edu.kit.ifv.mobitopp.populationsynthesis.region.PopulationSynthesisStep;
import edu.kit.ifv.mobitopp.populationsynthesis.region.ProcessOnLowerRegionLevelStep;
import edu.kit.ifv.mobitopp.populationsynthesis.region.ZonePredicates;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.IdSequence;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.emobility.EmobilityPersonCreator;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;
import edu.kit.ifv.mobitopp.util.parameter.LogitParameters;
import edu.kit.ifv.mobitopp.util.parameter.ParameterFormularParser;
import edu.kit.ifv.mobitopp.visum.export.VisumDmdExportLongTerm;

public class LongTermModule extends PopulationSynthesis {

	private static final RegionalLevel commuterLevel = RegionalLevel.community;
	private static final double maxDistance = 0.001d;
	private final Random seedGenerator;
	private VisumDmdExportLongTerm export;

	public LongTermModule(SynthesisContext context) {
		super(context);
		seedGenerator = new Random(context.seed());
		
		try {
			export = new VisumDmdExportLongTerm(context);
			export.init(context);
			
		} catch (IOException e) {
			e.printStackTrace();
			export = null;
		}
	}

	@Override
	protected DemandDataCalculator createCalculator(
		Map<ActivityType, FixedDistributionMatrix> commuterMatrices) {
		DemandRegionRelationsRepository commuterRelations = loadCommuterRelations();
		LinkedList<PopulationSynthesisStep> steps = new LinkedList<>();
		steps.add(activityScheduleAssignment());
		steps.add(workDestinationSelector(commuterRelations));
		steps.add(educationDestinationSelector());
		steps.add(carOwnershipModel());
		steps.add(commutationTicketOwnershipModel());
		steps.add(export.asSynthesisStep());
		steps.add(storeData());
		steps.add(cleanData());
		DemandDataForDemandRegionCalculator regionCalculator = createZoneCalculator();
		DemandDataForDemandRegionCalculator onlyAllZones = new FilteredDemandCalculator(
			regionCalculator);
		DemandDataForDemandRegionCalculator adaptiveCalculator = new AdaptiveCalculator(
			onlyAllZones);
		return new DemandRegionDemandCalculator(regions(), adaptiveCalculator, steps, impedance());
	}

	private List<DemandRegion> regions() {
		return context()
			.dataRepository()
			.demandRegionRepository()
			.getRegionsOf(RegionalLevel.community);
	}

	private LogitParameters parse(String file) {
		return new ParameterFormularParser().parseToParameter(new File(file));
	}

	private DemandRegionRelationsRepository loadCommuterRelations() {
		DemandRegionRepository regions = context().dataRepository().demandRegionRepository();
		File communityRelationsFile = experimentalParameters().valueAsFile("communityCommuters");
		int maxCommutingTime = experimentalParameters().valueAsInteger("maxCommutingTime");
		BiPredicate<DemandRegion, DemandRegion> commuterTimePredicate = CommuterRelations
			.filterByTravelTime(impedance(), maxCommutingTime);
		return new DemandRegionRelationsParser(commuterLevel, regions, commuterTimePredicate)
			.parse(communityRelationsFile);
	}

	private PopulationSynthesisStep workDestinationSelector(
		DemandRegionRelationsRepository commuterRelations) {
		PanelDataRepository dataRepository = panelDataRepository();
		DemandRegionOdPairSelector communityPairCreator = DemandRegionOdPairCreator
			.forWork(commuterRelations);
		ImpedanceIfc impedance = impedance();
		int range = experimentalParameters().valueAsInteger("workPoleDistanceRange");
		DemandRegionOdPairSelector odPairSelector = new PanelDistanceSelector(dataRepository,
			communityPairCreator, impedance, range);
		ZoneSelector zoneSelector = new DemandRegionBasedZoneSelector(commuterRelations,
			newRandom());
		Predicate<PersonBuilder> personFilter = new HasWorkActivity();
		DemandRegionDestinationSelector workDestinationSelector = new DemandRegionDestinationSelector(
			odPairSelector, zoneSelector, personFilter, newRandom());
		PopulationSynthesisStep onlyGeneratedZones = new FilterStep(workDestinationSelector,
			ZonePredicates.generatesAnyZone());
		return new ProcessOnLowerRegionLevelStep(onlyGeneratedZones, commuterLevel);
	}

	private PanelDataRepository panelDataRepository() {
		return context().dataRepository().panelDataRepository();
	}

	private PopulationSynthesisStep educationDestinationSelector() {
		double range = experimentalParameters().valueAsDouble("educationPoleDistanceRange");

		Map<ActivityType, Double> rangeMap = Stream
			.of(ActivityType.values())
			.filter(actvityType -> experimentalParameters().hasValue(parameterNameOf(actvityType)))
			.collect(toLinkedMap(identity(),
				a -> experimentalParameters().valueAsDouble(parameterNameOf(a))));

		File input = experimentalParameters().valueAsFile("educationDestinationChoiceParameters");
		LogitParameters parameters = new ParameterFormularParser().parseToParameter(input);
		return new ParallelPersonStep(EducationDestinationSelector
			.standard(panelDataRepository(), demandZoneRepository().zoneRepository(), impedance(),
				newRandom(), range, rangeMap, parameters));
	}

	private String parameterNameOf(ActivityType a) {
		return "educationPoleDistanceRange_" + a.toString();
	}

	private PopulationSynthesisStep activityScheduleAssignment() {
		return new ParallelHouseholdStep(activityScheduleAssigner()::assignActivitySchedule);
	}

	protected PopulationSynthesisStep storeData() {
		return community -> {
			community.zones().forEach(dataRepository().demandDataRepository()::store);
			System.gc();
		};
	}

	private DemandDataForDemandRegionCalculator createZoneCalculator() {
		AttributeType attributeType = StandardAttribute.householdSize;
		WeightedHouseholdSelector householdSelector = createHouseholdSelector();
		HouseholdCreator householdCreator = createHouseholdCreator();
		PersonCreator personCreator = createPersonCreator();
		Function<DemandRegion, Predicate<HouseholdOfPanelData>> householdFilter = this::createHouseholdFilter;

		DemandCreatorFactory demandCreatorFactory = new StructuralDataDemandCreatorFactory(
			householdCreator, personCreator, panelDataRepository(), attributeType, householdFilter,
			householdSelector, demandZoneRepository());
		return new DemandRegionBasedIpu(results(), dataRepository(), context(),
			demandCreatorFactory);
	}

	private Predicate<HouseholdOfPanelData> createHouseholdFilter(DemandRegion zone) {
		AllowedHouseholdFilter filter = new AllowedHouseholdFilter(
			demandZoneRepository().zoneRepository(), impedance(), context());

		return zone.zones().map(filter::touchesStudyArea).reduce(Predicate::or).get();
	}

	private ActivityScheduleAssigner activityScheduleAssigner() {
		File basePath = experimentalParameters().valueAsFile("actiToppPath");
		PanelDataRepository panelDataRepository = this
			.context()
			.dataRepository()
			.panelDataRepository();
		long seed = context().seed();
		File resultfolder = Convert.asFile(context().configuration().getResultFolder());
		return new ActiToppScheduleAssigner(seed, panelDataRepository, resultfolder, basePath);
	}

	private WeightedHouseholdSelector createHouseholdSelector() {
		return new ProbabilityBasedSelector(newRandom());
	}

	private DoubleSupplier newRandom() {
		long seed = seedGenerator.nextLong();
		return new Random(seed)::nextDouble;
	}

	private HouseholdBasedStep carOwnershipModel() {
		Map<Integer, NumberOfCarsSelector> numberSelectors = loadNumberOfCarsSelectors();
		GenericElectricCarOwnershipModel electricCarModel = loadElectricCarModel();
		CarOwnershipModel carOwnershipModel = new SimpleCarOwnershipModel(numberSelectors::get,
			electricCarModel);
		AssignCars assigner = new AssignCars(carOwnershipModel);
		return new ParallelHouseholdStep(assigner::assignCars);
	}

	private GenericElectricCarOwnershipModel loadElectricCarModel() {
		IdSequence idSequence = new IdSequence();
		long seed = context().seed();
		File engineFile = context().carEngineFile();
		String segmentFile = context().configuration().getCarOwnership().getSegment();
		CarSegmentModel segmentModel = new LogitBasedCarSegmentModel(impedance(), seed,
			segmentFile);
		ProbabilityForElectricCarOwnershipModel calculator = new ElectricCarOwnership(impedance(),
			engineFile.getAbsolutePath());
		String configFile = context().configuration().getCarOwnership().getOwnership();
		return new GenericElectricCarOwnershipModel(idSequence, segmentModel, seed, calculator,
			configFile);
	}

	private Map<Integer, NumberOfCarsSelector> loadNumberOfCarsSelectors() {
		Map<Integer, NumberOfCarsSelector> numberSelectors = new HashMap<>();
		NumberOfCarsSelector town = createStochasticSelectorFor("town");
		NumberOfCarsSelector smalltown = createStaticSelectorFor("smalltown");
		NumberOfCarsSelector urbanArea = createStaticSelectorFor("urbanArea");
		NumberOfCarsSelector ruralArea = createStaticSelectorFor("ruralArea");
		numberSelectors.put(111, town);
		numberSelectors.put(112, town);
		numberSelectors.put(121, town);
		numberSelectors.put(211, town);
		numberSelectors.put(113, smalltown);
		numberSelectors.put(123, smalltown);
		numberSelectors.put(213, smalltown);
		numberSelectors.put(223, smalltown);
		numberSelectors.put(221, smalltown);
		numberSelectors.put(114, urbanArea);
		numberSelectors.put(124, urbanArea);
		numberSelectors.put(214, urbanArea);
		numberSelectors.put(115, ruralArea);
		numberSelectors.put(125, ruralArea);
		numberSelectors.put(215, ruralArea);
		numberSelectors.put(224, ruralArea);
		numberSelectors.put(225, ruralArea);
		return numberSelectors;
	}

	private NumberOfCarsSelector createStaticSelectorFor(String type) {
		LogitParameters parameters = carOwnershipParametersFor(type);
		return new StaticSelector(newRandom(), parameters);
	}

	private NumberOfCarsSelector createStochasticSelectorFor(String type) {
		LogitParameters parameters = carOwnershipParametersFor(type);
		DoubleSupplier ascDraw = new Random(context().seed())::nextGaussian;
		return new StochasticSelector(newRandom(), parameters, ascDraw);
	}

	private DynamicParameters experimentalParameters() {
		return context().experimentalParameters();
	}

	private LogitParameters carOwnershipParametersFor(String configurationEntry) {
		try {
			File input = experimentalParameters().valueAsFile(configurationEntry);
			return new ParameterFormularParser().parseToParameter(input);
		} catch (IllegalArgumentException cause) {
			throw new RuntimeException(
				"Could not load car ownership parameters for: " + configurationEntry, cause);
		}
	}

	private PopulationSynthesisStep commutationTicketOwnershipModel() {
		LogitParameters logitParameters = parse(context().configuration().getCommuterTicket());
		GeneratedCommutationTicketOwnershipHelper helper = new CommutationTicketOwnershipHelper(
			newRandom());
		GeneratedCommutationTicketOwnershipUtilityFunction utilities = new GeneratedCommutationTicketOwnershipUtilityFunction(
			logitParameters, helper);
		GeneratedCommutationTicketOwnership generatedModel = new GeneratedCommutationTicketOwnership(
			utilities, helper);
		LogitBasedCommutationTicketOwnershipModel commuterOwnershipModel = new LogitBasedCommutationTicketOwnershipModel(
			generatedModel);
		
		return new ParallelPersonStep(commuterOwnershipModel);
	}

	private HouseholdCreator createHouseholdCreator() {
		HouseholdLocationSelector householdLocationSelector = createHouseholdLocationSelector();
		ChargePrivatelySelector chargePrivatelySelector = createChargePrivatelySelector();
		EconomicalStatusCalculator economicalStatusCalculator = createEconomicalStatusCalculator();
		return new DefaultHouseholdCreator(householdLocationSelector, economicalStatusCalculator,
			chargePrivatelySelector);
	}

	private EconomicalStatusCalculator createEconomicalStatusCalculator() {
		return EconomicalStatusCalculators.oecd2017();
	}

	private HouseholdLocationSelector createHouseholdLocationSelector() {
		return new RoadBasedHouseholdLocationSelector(context(), maxDistance, edgeFilter());
	}

	private ChargePrivatelySelector createChargePrivatelySelector() {
		return new AllowChargingProbabilityBased(seed());
	}

	private PersonCreator createPersonCreator() {
		CommutationTicketModelIfc commuterTicketModel = commuterTickets();
		Map<String, MobilityProviderCustomerModel> carSharing = Map.of();
		return new EmobilityPersonCreator(commuterTicketModel, carSharing, seed());
	}

	private CommutationTicketModelIfc commuterTickets() {
		return (p, h, z) -> p.hasCommuterTicket();
	}

	@Override
	protected OpportunityLocationSelector createOpportunityLocationSelector() {
		OpportunityLocationSelector other = new RoadBasedOpportunitySelector(context(),
			edgeFilter(), maxDistance) {

			@Override
			protected double averageLocationSize(ActivityType activityType) {
				return super.averageLocationSize(activityType) * 10.0d;
			}

		};
		return new FixedNumberOfLocationsForOuterZones(context().zoneRepository().zoneRepository(),
			other);
	}

	private static EdgeFilter edgeFilter() {
		return EdgeFilter.allEdges;
	}
	
	@Override
	protected void executeAfterCreation() {
		export.finish();
		ParallelHouseholdStep.shutDown();
		ParallelPersonStep.shutDown();
	}

}
