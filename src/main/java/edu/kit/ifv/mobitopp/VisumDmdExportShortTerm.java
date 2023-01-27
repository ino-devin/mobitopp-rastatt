package edu.kit.ifv.mobitopp;

import static edu.kit.ifv.mobitopp.VisumDmdExportLongTerm.generateVersoinHeader;
import static edu.kit.ifv.mobitopp.VisumDmdIdProvider.locationIdOf;
import static edu.kit.ifv.mobitopp.VisumDmdIdProvider.personId;
import static edu.kit.ifv.mobitopp.WriterFactory.finishWriter;
import static edu.kit.ifv.mobitopp.WriterFactory.getWriter;
import static java.lang.Math.max;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.populationsynthesis.SynthesisContext;
import edu.kit.ifv.mobitopp.routing.Path;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.Car;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.PersonListener;
import edu.kit.ifv.mobitopp.simulation.SimulationContext;
import edu.kit.ifv.mobitopp.simulation.StateChange;
import edu.kit.ifv.mobitopp.simulation.TripData;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.person.FinishedTrip;
import edu.kit.ifv.mobitopp.simulation.person.StartedTrip;
import edu.kit.ifv.mobitopp.simulation.tour.Subtour;
import edu.kit.ifv.mobitopp.simulation.tour.Tour;
import edu.kit.ifv.mobitopp.time.DateFormat;
import edu.kit.ifv.mobitopp.time.Time;

public class VisumDmdExportShortTerm implements PersonListener {
	

	private static final String SEP = ";";
	private static final String NEW_LINE = "\r\n";

	@Override
	public void notifyEndTrip(Person person, FinishedTrip trip) {
		try {
			
			if (trip.previousActivity().activityType().equals(ActivityType.HOME)) {
				tourWriter.write(generateTourRow(person, trip));
			}
			
			List<FinishedTrip> legs = collectLegs(trip);
				
			int i = 1;
			for (FinishedTrip leg : legs) {
				tripsWriter.write(generateTripRow(person, trip));
				
				if (i < legs.size()) {
					activityWriter.write(generateActivityRow(person, -1, leg.endDate(), 0, leg.destination().location()));
				}
				
				i++;
			}

			
		} catch (IOException e) {
			System.err.println("Could not log trip " + String.valueOf(trip) + " of person" + String.valueOf(person));
			e.printStackTrace();
		}
		
	}

	@Override
	public void notifyStartTrip(Person person, StartedTrip trip) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notifyFinishCarTrip(Person person, Car car, FinishedTrip trip, ActivityIfc activity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notifyStartActivity(Person person, ActivityIfc activity) {

		try {
			activityWriter.write(generateActivityRow(person, activity));
		} catch (IOException e) {
			System.err.println("Could not log activity " + String.valueOf(activity) + " of person" + String.valueOf(person));
			e.printStackTrace();
		}
		
	}

	@Override
	public void notifySelectCarRoute(Person person, Car car, TripData trip, Path route) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writeSubtourinfoToFile(Person person, Tour tour, Subtour subtour, Mode tourMode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writeTourinfoToFile(Person person, Tour tour, Zone tourDestination, Mode tourMode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notifyStateChanged(StateChange stateChange) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notifyFinishSimulation() {
		// TODO Auto-generated method stub
		
	}
	
	
	private final Writer tourWriter;
	private final Writer activityWriter;
	private final Writer tripsWriter;
		
	private final Map<Integer, Integer> personTourNo;
	private final Map<Integer, Integer> personTourIndex;
	private final Map<Integer, Integer> personActivityIndex;
	
	public VisumDmdExportShortTerm(SimulationContext context) throws IOException {
		
		File folder = new File(context.configuration().getResultFolder());
		
		this.tourWriter = getWriter(new File(folder, "tours.dmd"));
		this.activityWriter = getWriter(new File(folder, "activities.dmd"));
		this.tripsWriter = getWriter(new File(folder, "trips.dmd"));
		this.personTourNo = new HashMap<>();
		this.personTourIndex = new HashMap<>();
		this.personActivityIndex = new HashMap<>();

	}
	
	public void init(SimulationContext context) {
		try {
			tourWriter.write(generateVersoinHeader());
			tourWriter.write(NEW_LINE);
			tourWriter.write(generateTourHeader());
			activityWriter.write(NEW_LINE);
			activityWriter.write(generateActivityHeader());
			tripsWriter.write(NEW_LINE);
			tripsWriter.write(generateTripHeader());

		} catch (IOException e) {
			System.err.println("Could not init .dmd files");
		}

	}
	
	public void finish() {
		try {
			finishWriter(tourWriter);
			finishWriter(activityWriter);
			finishWriter(tripsWriter);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
				
	}
	
	private String generateTourHeader() {
		return "*" + NEW_LINE
				+ "* Tabelle: Touren" + NEW_LINE
				+ "*" + NEW_LINE
				+ "$TOUR:PERSONNO;SCHEDULENO;NO;MAINDSEGCODE" + NEW_LINE;
	}
	

	private int tourNoCnt = 1;
	
	private String generateTourRow(Person person, FinishedTrip trip) {
		int tourNo = tourNoCnt++;
		personTourNo.put(person.getOid(), tourNo);
		personTourIndex.put(person.getOid(), 1);
				
		return personId(person) + SEP //PERSONNO
			 + personId(person) + SEP //SCHEDULENO
			 + tourNo + SEP //NO
			 + mapModeToCode(trip.mode().mainMode()) //MAINDSEGCODE
			 + NEW_LINE;
		
	}

	private String generateActivityHeader() {
		return "*" + NEW_LINE
				+ "* Tabelle: Aktivitätsausübungen" + NEW_LINE
				+ "*" + NEW_LINE
				+ "$ACTIVITYEXECUTION:PERSONNO;SCHEDULENO;INDEX;STARTTIME;DURATION;ACTIVITYCODE;LOCATIONNO;STARTDAY;STARTTIMEDAY" + NEW_LINE;
	}
	
	private String generateActivityRow(Person person, ActivityIfc activity) {
		return generateActivityRow(person, activity.activityType().getTypeAsInt(), activity.startDate(), activity.duration(), activity.location());
	}
	
	private String generateActivityRow(Person person, int activityCode, Time startDate, int durationMin, Location location) {
		int index = personActivityIndex.getOrDefault(person.getOid(), 1);
		personActivityIndex.put(person.getOid(), index + 1);
		
		return personId(person) + SEP 	//PERSONNO
			 + personId(person) + SEP 	//SCHEDULENO
			 + index + SEP		//INDEX
			 + sumHourFormat(startDate) + SEP //STARTTIME
			 + max(0, durationMin * 60) + "s" + SEP 		  //DURATION
			 + activityCode + SEP //ACTIVITYCODE
			 + locationIdOf(location) + SEP //LOCATIONNO
			 + dayCode(startDate) + SEP 	//STARTDAY
			 + new DateFormat().asTime(startDate) 	//STARTTIMEDAY
			 + NEW_LINE; 
	}

	
	private String generateTripHeader() {
		return "*" + NEW_LINE
				+ "* Tabelle: Trips" + NEW_LINE
				+ "*" + NEW_LINE
				+ "$TRIP:PERSONNO;SCHEDULENO;TOURNO;INDEX;SCHEDDEPTIME;DURATION;DSEGCODE;FROMACTIVITYEXECUTIONINDEX;TOACTIVITYEXECUTIONINDEX;STARTDAY;STARTTIMEDAY" + NEW_LINE;
	}
	
	private String generateTripRow(Person person, FinishedTrip trip) {
//		personTourNo.putIfAbsent(person.getOid(), 1);
//		personTourIndex.putIfAbsent(person.getOid(), 1);
		
		int index = personTourIndex.get(person.getOid());
		personTourIndex.put(person.getOid(), index+1);
		
		int activityIndex = personActivityIndex.get(person.getOid());
		
		Time startDate = trip.startDate();
		
		return personId(person) + SEP 	//PERSONNO
			 + personId(person) + SEP	//SCHEDULENO
			 + personTourNo.get(person.getOid()) + SEP //TOURNO
			 + index + SEP //INDEX
			 + sumHourFormat(startDate) + SEP //SCHEDDEPTIME
			 + max(0, trip.plannedDuration()*60) + "s" + SEP //DURATION
			 + mapModeToCode(trip.mode()) + SEP //DSEGCODE
			 + (activityIndex - 1) + SEP //FROMACTIVITYEXECUTIONINDEX
			 + activityIndex + SEP //TOACTIVITYEXECUTIONINDEX
			 + dayCode(startDate) + SEP 			//STARTDAY
			 + new DateFormat().asTime(startDate) 		//STARTTIMEDAY
			 + NEW_LINE;
	}
	
	private String mapModeToCode(Mode mode) {
		switch (mode.forLogging()) {
			case "0": return "B";
			case "1": return "C_Pkw";
			case "2": return "CP";
			case "3": return "F_Fuss";
			case "4": return "X";
			case "7": return "TX";
			case "11": return "CS_S";
			case "12": return "CS_F";
			case "15": return "E";
			case "17": return "BS";
			case "21": return "M";
			default: return mode.forLogging();
		}
	}
	
	private List<FinishedTrip> collectLegs(FinishedTrip trip) {
		List<FinishedTrip> legs = new ArrayList<>();
		
		trip.forEachFinishedLeg(legs::add);
		
		if (legs.isEmpty()) {
			return List.of(trip);
			
		} else if (legs.size() == 1) {
			return legs;
			
		}

		return legs.stream().flatMap(l -> collectLegs(l).stream()).collect(Collectors.toList());
	}
	
	private String sumHourFormat(Time time) {
		if (time.isBefore(Time.start)) {
			time = Time.start;
		}
	
		return String.format("%02d:%02d:%02d", Math.floorDiv(time.toSeconds(), 60*60), time.getMinute() , time.getSecond());
	}
	
	private String dayCode(Time time) {
		switch (time.weekDay()) {
			case MONDAY:
				return "Mo.";
			case TUESDAY:
				return "Di.";
			case WEDNESDAY:
				return "Mi.";
			case THURSDAY:
				return "Do.";
			case FRIDAY:
				return "Fr.";
			case SATURDAY:
				return "Sa.";
			case SUNDAY:
				return "So.";
			default:
				return "??";
		}
	}
	
}
