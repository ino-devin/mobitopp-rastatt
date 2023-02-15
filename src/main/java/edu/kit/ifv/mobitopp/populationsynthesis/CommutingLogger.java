package edu.kit.ifv.mobitopp.populationsynthesis;

import static edu.kit.ifv.mobitopp.simulation.ActivityType.WORK;

import java.util.List;

import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.populationsynthesis.region.DemandZoneBasedStep;
import edu.kit.ifv.mobitopp.result.Category;
import edu.kit.ifv.mobitopp.result.Results;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;

public class CommutingLogger {
	
	private static final String SEP = ";";
	private final Category resultCategory = new Category("zone-commuting.csv", List.of("from", "to", "level", "distance", "volumeFemale", "volumeMale", "volumeTotal", "populationFrom", "populationTo"));
	private final Results results;
	private final ImpedanceIfc impedance;
	private final List<DemandZone> zones;
	
	public CommutingLogger(SynthesisContext context) {
		results = context.resultWriter();
		impedance = context.impedance();
		zones = context.dataRepository().zoneRepository().getZones();
	}
	
	public void process(DemandZone from) {
		zones.forEach(to -> log(from, to));
	}
	
	private void log(DemandZone from, DemandZone to) {
		String fromId = from.getExternalId();
		String toId = to.getExternalId();
		String level = from.regionalLevel().name() + "-" + to.regionalLevel().name();
		double distance = impedance.getDistance(from.zone().getId(), to.zone().getId());
		long volumeF = countCommuters(from, to, true);
		long volumeM = countCommuters(from, to, false);
		
		long popFrom = countPopulation(from);
		long popTo = countPopulation(to);
		
		this.results.write(resultCategory, fromId + SEP + toId + SEP + level + SEP + distance + SEP + volumeF + SEP + volumeM + SEP + (volumeF + volumeM) + SEP + popFrom + SEP + popTo);
	}
	
	private long countPopulation(DemandZone from) {
		return from.getPopulation()
				   .households()
				   .flatMap(h -> h.persons())
				   .filter(p -> p.hasFixedZoneFor(WORK))
				   .count();
	}
		
	private long countCommuters(DemandZone from, DemandZone to, boolean female) {
		return from.getPopulation()
				   .households()
				   .flatMap(h -> h.persons())
				   .filter(p -> p.isFemale() == female)
				   .filter(p -> p.hasFixedZoneFor(WORK))
				   .filter(p -> to.getExternalId().equals(p.fixedZoneFor(WORK).getId().getExternalId()))
				   .count();
	}
	
	public DemandZoneBasedStep asSyntheisStep() {
		return new DemandZoneBasedStep(this::process);
	}
	
}
