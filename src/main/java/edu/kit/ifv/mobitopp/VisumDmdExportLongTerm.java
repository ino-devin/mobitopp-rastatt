package edu.kit.ifv.mobitopp;
import static edu.kit.ifv.mobitopp.VisumDmdIdProvider.householdId;
import static edu.kit.ifv.mobitopp.VisumDmdIdProvider.locationIdOf;
import static edu.kit.ifv.mobitopp.VisumDmdIdProvider.personId;
import static edu.kit.ifv.mobitopp.WriterFactory.finishWriter;
import static edu.kit.ifv.mobitopp.WriterFactory.getWriter;
import static edu.kit.ifv.mobitopp.simulation.ActivityType.HOME;
import static edu.kit.ifv.mobitopp.simulation.ActivityType.PRIVATE_VISIT;
import static edu.kit.ifv.mobitopp.simulation.ActivityType.UNDEFINED;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.populationsynthesis.SynthesisContext;
import edu.kit.ifv.mobitopp.populationsynthesis.region.DemandZoneBasedStep;
import edu.kit.ifv.mobitopp.populationsynthesis.region.PopulationSynthesisStep;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.BaseHousehold;
import edu.kit.ifv.mobitopp.simulation.BasePerson;
import edu.kit.ifv.mobitopp.simulation.FixedDestination;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.opportunities.Opportunity;

public class VisumDmdExportLongTerm {

	private static final String SEP = ";";

	private static final String NEW_LINE = "\r\n";

//	private final static Category resultCategoryLocations = createResultCategoryLocations();
//
//	@Getter
//	private final Results results;
//	
//	
//	public VisumDmdExport(Results results) {
//		this.results = results;
//	}
//	
//	public void logFixedLocations(PersonBuilder person) {
//		person.fixedDestinations()
//			  .forEach(f-> logLocation(f.zone().getId(), f.location()));
//	}
//	
//	public void logFixedLocations(Person person) {
//		person.getFixedDestinations()
//			  .forEach(f-> logLocation(f.zone().getId(), f.location()));
//	}
//	
//	public void logHomeLocation(HouseholdForSetup household) {
//		logLocation(household.homeZone().getId(), household.homeLocation());
//	}
//	
//	public void logHomeLocation(Household household) {
//		logLocation(household.homeZone().getId(), household.homeLocation());
//	}
//	
//	public void logCenter(Zone zone) {
//		logLocation(zone.getId(), zone.centroidLocation());
//	}
//	
//	public void logOpportunity(Opportunity opportunity) {
//		logLocation(opportunity.zone(), opportunity.location());
//	}
//	
//	public void logLocation(ZoneId zone, Location location) {
//		String msg = zone.getExternalId() + SEP + location.hashCode() + SEP + location.coordinatesP().getX() + SEP + location.coordinatesP().getY();
//		
//		results.write(resultCategoryLocations, msg);
//	}
//	
//	
//	private static Category createResultCategoryLocations() {
//		return new Category("locationIds", List.of("zone", "hash", "x", "y"));
//	}
//
//
//	public void logIdsOfZone(DemandZone zone) {
//		this.logCenter(zone.zone());
//		
//		zone.opportunities().forEach(this::logOpportunity);
//		zone.getPopulation().households().forEach(this::logHomeLocation);
//		zone.getPopulation().households().flatMap(h -> h.persons()).forEach(this::logFixedLocations);
//	}
//	
//	public void logIdsOfZone(Zone zone) {
//		this.logCenter(zone);
//		
//		zone.opportunities().forEach(this::logOpportunity);
//		PopulationDataForZone populationData = zone.getDemandData().getPopulationData();	
//		populationData.getHouseholds().forEach(this::logHomeLocation);
//		populationData.getHouseholds().stream().flatMap(h -> h.persons()).forEach(this::logFixedLocations);
//	}
//	
//	public PopulationSynthesisStep logIdsStep() {
//		return new DemandZoneBasedStep(this::logIdsOfZone);
//	}
	
	
	
	
	
	
	private final Writer locationWriter;
	private final Writer activityLocationWriter;
	private final Writer householdWriter;
	private final Writer personWriter;
	private final Writer longTermChoiceWriter;
	private final Writer dailyScheduleWriter;
	
	private final Map<Integer, List<ActivityType>> locationIds;
	
	
	public VisumDmdExportLongTerm(SynthesisContext context) throws IOException {
		
		File folder = new File(context.configuration().getResultFolder());
		
		this.locationWriter = getWriter(new File(folder, "locations.dmd"));
		this.activityLocationWriter = getWriter(new File(folder, "activityLocations.dmd"));
		this.householdWriter = getWriter(new File(folder, "households.dmd"));
		this.personWriter = getWriter(new File(folder, "persons.dmd"));
		this.longTermChoiceWriter = getWriter(new File(folder, "longTermChoices.dmd"));
		this.dailyScheduleWriter = getWriter(new File(folder, "dailySchedule.dmd"));
		
		this.locationIds = new HashMap<>();
	}
	
	public void init(SynthesisContext context) {
		try {
			locationWriter.write(generateVersoinHeader());
			locationWriter.write(NEW_LINE);
			locationWriter.write(generateDemandmodelTable());
			locationWriter.write(NEW_LINE);
			locationWriter.write(generateActicityTable());
			locationWriter.write(NEW_LINE);
			locationWriter.write(generateLoactionHeader());

			activityLocationWriter.write(generateActivityLoactionHeader());
			householdWriter.write(generateHouseholdHeader());
			personWriter.write(generatePersonHeader());
			longTermChoiceWriter.write(generateLongTermChoiceHeader());
			dailyScheduleWriter.write(generateDailyScheduleHeader());

		} catch (IOException e) {
			System.err.println("Could not init .dmd files");
		}
		
		context.zoneRepository().getZones().forEach(dz -> logZone(dz.zone()));
	}
	
	public PopulationSynthesisStep asSynthesisStep() {
		return new DemandZoneBasedStep(this::logDemandZone);
	}
	
	public void finish() {
		try {
			finishWriter(locationWriter);
			finishWriter(activityLocationWriter);
			finishWriter(householdWriter);
			finishWriter(personWriter);
			finishWriter(longTermChoiceWriter);
			finishWriter(dailyScheduleWriter);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
				
	}
	
	
	
	
	
	private void logZone(Zone zone) {
		try {
			int id = locationIdOf(zone);
			
			if (!locationIds.containsKey(id)) {
				locationWriter.write(generateLocationRow(zone));
				activityLocationWriter.write(generateActivityLocationRow(zone));
				
				locationIds.put(id, new ArrayList<>(List.of(UNDEFINED)));
			}
			
			zone.opportunities().forEach(this::logOpportunity);

		} catch (IOException e) {
			System.err.println("Could not log location of zone " + String.valueOf(zone));
			e.printStackTrace();
		}

	}

	private void logDemandZone(DemandZone zone) {
		logZone(zone.zone());
		
		zone.opportunities().forEach(this::logOpportunity);
		zone.getPopulation().households().forEach(this::logHousehold);	
	}
	
	private void logOpportunity(Opportunity opportunity) {
		try {
			int id = locationIdOf(opportunity);
			
			if (!locationIds.containsKey(id)) {
				locationWriter.write(generateLocationRow(opportunity));
				locationIds.put(id, new ArrayList<>());
			}
			
			List<ActivityType> locationActivities = locationIds.get(id);
			if (!locationActivities.contains(opportunity.activityType())) {
				activityLocationWriter.write(generateActivityLocationRow(opportunity));
				locationActivities.add(opportunity.activityType());
			}

		} catch (IOException e) {
			System.err.println("Could not log location of opportunity " + String.valueOf(opportunity));
			e.printStackTrace();
		}
	}

	private void logHousehold(BaseHousehold household) {
		try {
			int id = locationIdOf(household);
			
			if (!locationIds.containsKey(id)) {
				locationWriter.write(generateLocationRow(household));
				locationIds.put(id, new ArrayList<>());
			}
			
			
			List<ActivityType> locationActivities = locationIds.get(id);
			
			if (!locationActivities.contains(HOME)) {
				activityLocationWriter.write(generateActivityLocationRow(household));
				locationActivities.add(HOME);
			}
			
			if (!locationActivities.contains(PRIVATE_VISIT)) {
				activityLocationWriter.write(generateActivityLocationRowVisit(household));
				locationActivities.add(PRIVATE_VISIT);
			}
			
			
			householdWriter.write(generateHouseholdRow(household));
			household.persons().forEach(this::logPerson);

		} catch (IOException e) {
			System.err.println("Could not log household " + String.valueOf(household));
			e.printStackTrace();
		}
	}
	
	private void logPerson(BasePerson person) {
		try {
			personWriter.write(generatePersonRow(person));
			dailyScheduleWriter.write(generateDailyScheduleRow(person));
				
			person.getFixedDestinations().forEach(f -> logLongTermChoice(person, f));

		} catch (IOException e) {
			System.err.println("Could not log person " + String.valueOf(person));
			e.printStackTrace();
		}
	}
	
	private void logLongTermChoice(BasePerson person, FixedDestination fixed) {
		try {
			int id = locationIdOf(fixed.location());
			
			if (!locationIds.containsKey(id)) {
				locationWriter.write(generateLocationRow(fixed));
				locationIds.put(id, new ArrayList<>());
			}
			
			List<ActivityType> locationActivities = locationIds.get(id);
			if (!locationActivities.contains(fixed.activityType())) {
				activityLocationWriter.write(generateActivityLocationRow(fixed));
				locationActivities.add(fixed.activityType());
			}
			
			longTermChoiceWriter.write(generateLongTermChoiceRow(person, fixed));

		} catch (IOException e) {
			System.err.println("Could not log fixed destination " + String.valueOf(fixed) + "of person " + String.valueOf(person));
			e.printStackTrace();
		}
	}
	
	
	public static String generateVersoinHeader() {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");  
		LocalDateTime now = LocalDateTime.now();
		String date = dtf.format(now);
		
		return "$VISION" + NEW_LINE
			 + "* KIT Karlsruher Institut für Technologie Fakultät Bau, Geo + Umwelt Karlsruhe" + NEW_LINE
			 + "* " + date + NEW_LINE
			 + "*" + NEW_LINE
			 + "* Tabelle: Versionsblock" + NEW_LINE
			 + "*" + NEW_LINE
			 + "$VERSION:VERSNR;FILETYPE;LANGUAGE;UNIT" + NEW_LINE
			 + "13.000;Demand;ENG;KM"  + NEW_LINE;
	}
	
	private String generateDemandmodelTable() {
		return "*" + NEW_LINE
			 + "* Tabelle: Nachfragemodelle" + NEW_LINE
			 + "*" + NEW_LINE
			 + "$DEMANDMODEL:CODE;NAME;TYPE;MODESET" + NEW_LINE
			 + "ABM;ABM;ABM;"  + NEW_LINE;
	}
	
	private String generateActicityTable() {
		StringBuilder table = new StringBuilder();
		table.append("*" + NEW_LINE);
		table.append("* Tabelle: Aktivitäten" + NEW_LINE);
		table.append("*" + NEW_LINE);
		table.append("$ACTIVITY:CODE;NAME;DEMANDMODELCODE;RANK;ISHOMEACTIVITY;STRUCTURALPROPCODES;CONSTRAINTDEST;CONSTRAINTDESTTYPE;CONSTRAINTMINFACTORDEST;CONSTRAINTMAXFACTORDEST" + NEW_LINE);

		table.append("-1;TRANSIT;ABM;1;0;;0;SEPARATELYPERDEMANDSTRATUM;1;1" + NEW_LINE);
		
		for (ActivityType activityType : ActivityType.values()) {
			table.append(activityType.getTypeAsInt() + SEP);
			table.append(activityType.name().toUpperCase() + SEP);
			table.append("ABM;1;");
			table.append((activityType.equals(HOME)) ? 1 : 0);
			table.append(";;0;SEPARATELYPERDEMANDSTRATUM;1;1");
			table.append(NEW_LINE);
		}
		
		return table.toString();
	}
	
	
	
	private String generateLoactionHeader() {
		return "*" + NEW_LINE
				+ "* Tabelle: Standorte" + NEW_LINE
				+ "*" + NEW_LINE
				+ "$LOCATION:NO;XCOORD;YCOORD;ZONENO;ACTTYPE;ATTR;ISHH" + NEW_LINE;
	}
	
	private String generateLocationRow(BaseHousehold household) {
		return generateLocationRow(household.homeLocation(), household.homeZone(), HOME, 0);
	}
	
	private String generateLocationRow(Opportunity opportunity) {
		return generateLocationRow(opportunity.location(), opportunity.zone(), opportunity.activityType(), opportunity.attractivity());
	}
	
	private String generateLocationRow(Zone zone) {
		return generateLocationRow(zone.centroidLocation(), zone, UNDEFINED, 0);
	}

	private String generateLocationRow(FixedDestination fixed) {
		return generateLocationRow(fixed.location(), fixed.zone(), fixed.activityType(), 0);
	}
	
	private String generateLocationRow(Location location, Zone zone, ActivityType activity, int attraktivity) {
		return generateLocationRow(location, zone.getId(), activity, attraktivity);
	}
	
	private String generateLocationRow(Location location, ZoneId zone, ActivityType activity, int attraktivity) {
		StringBuilder row = new StringBuilder();
		
		row.append(locationIdOf(location)); //NO
		row.append(SEP);
		row.append(location.coordinatesP().getX()); //XCOORD
		row.append(SEP);
		row.append(location.coordinatesP().getY()); //YCOORD
		row.append(SEP);
		row.append(zone.getExternalId()); //ZONENO
//		row.append(SEP);
//		//POIKEY
		row.append(SEP);
		row.append(activity.getTypeAsInt());
		row.append(SEP);
		row.append(attraktivity);
		row.append(SEP);
		row.append(activity.equals(HOME) ? 1 : 0);
		row.append(NEW_LINE);
		
		return row.toString();
	}
	
	
	
	private String generateActivityLoactionHeader() {
		return "*" + NEW_LINE
				+ "* Tabelle: Aktivitätenstandorte" + NEW_LINE
				+ "*" + NEW_LINE
				+ "$ACTIVITYLOCATION:ACTIVITYCODE;LOCATIONNO;ATTRACTIONPOTENTIAL" + NEW_LINE;
	}
	
	private String generateActivityLocationRow(BaseHousehold household) {
		return generateActivityLocationRow(household.homeLocation(), HOME, 0);
	}
	
	private String generateActivityLocationRowVisit(BaseHousehold household) {
		return generateActivityLocationRow(household.homeLocation(), PRIVATE_VISIT, 0);
	}
	
	private String generateActivityLocationRow(Opportunity opportunity) {
		return generateActivityLocationRow(opportunity.location(), opportunity.activityType(), opportunity.attractivity());
	}
	
	private String generateActivityLocationRow(Zone zone) {
		return generateActivityLocationRow(zone.centroidLocation(), UNDEFINED, 0);
	}
	
	private String generateActivityLocationRow(FixedDestination fixed) {
		return generateActivityLocationRow(fixed.location(), fixed.activityType(), 0);
	}
	
	private String generateActivityLocationRow(Location location, ActivityType activity, int attraktivity) {
		return activity.getTypeAsInt() + ";" + locationIdOf(location) + ";" + attraktivity + NEW_LINE;
	}

	

	private String generateHouseholdHeader() {
		return "*" + NEW_LINE
				+ "* Tabelle: Haushalte" + NEW_LINE
				+ "*" + NEW_LINE
				+ "$HOUSEHOLD:NO;RESIDENCEKEY;HHSIZE;INCOME;NRCAR;NRCH" + NEW_LINE;
	}
	
	private String generateHouseholdRow(BaseHousehold household) {
		return 	  householdId(household) + SEP //NO
				+ "(" + HOME.getTypeAsInt() + "," + locationIdOf(household) + ")" + SEP //RESIDENCEKEY
				+ household.getSize() + SEP //HHSIZE
				+ household.monthlyIncomeEur() + SEP //INCOME
				+ household.getNumberOfOwnedCars() + SEP //NRCAR
				+ "0" + NEW_LINE; //NRCH
	}	
	
	private String generatePersonHeader() {
		return "*" + NEW_LINE
				+ "* Tabelle: Personen" + NEW_LINE
				+ "*" + NEW_LINE
				+ "$PERSON:NO;HOUSEHOLDNO;AGE;EMPL;HASTICKET;SEX" + NEW_LINE;
	}
	
	private String generatePersonRow(BasePerson person) {
		return 	 personId(person) + SEP //NO
				+ householdId(person.household()) + SEP //HOUSEHOLDNO
				+ person.age() + SEP //AGE
				+ person.employment().toString().toUpperCase() + SEP //EMPL
				+ (person.hasCommuterTicket() ? 1 : 0) + SEP //HASTICKET
				+ person.gender().toString().toUpperCase() //SEX
				+ NEW_LINE; 
	}
	
	
	
	private String generateLongTermChoiceHeader() {
		return "*" + NEW_LINE
				+ "* Tabelle: Langfristige Entscheidungen" + NEW_LINE
				+ "*" + NEW_LINE
				+ "$LONGTERMCHOICE:PERSONNO;ACTIVITYLOCATIONKEY" + NEW_LINE;
	}
	
	private String generateLongTermChoiceRow(BasePerson person, FixedDestination fixed) {
		return personId(person) + SEP  //PERSONNO
			+ "(" + fixed.activityType().getTypeAsInt() + "," + locationIdOf(fixed.location()) + ")" //ACTIVITYLOCATIONKEY
			+ NEW_LINE;
	}


	
	
	
	private String generateDailyScheduleHeader() {
		return "*" + NEW_LINE
				+ "* Tabelle: Tagespläne" + NEW_LINE
				+ "*" + NEW_LINE
				+ "$SCHEDULE:PERSONNO;NO;DATE" + NEW_LINE;
	}
	
	private String generateDailyScheduleRow(BasePerson person) {
		return personId(person) + SEP  //PERSONNO
			+  personId(person) + SEP  //NO
			+  1					   //DATE (always 'monday' but here monday has 168 hours)
			+  NEW_LINE;
	}
	


}
