package edu.kit.ifv.mobitopp.data;

import static java.util.Collections.emptyList;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.Map;

import edu.kit.ifv.mobitopp.data.areatype.AreaType;
import edu.kit.ifv.mobitopp.data.areatype.ZoneAreaType;
import edu.kit.ifv.mobitopp.dataimport.DefaultPower;
import edu.kit.ifv.mobitopp.dataimport.DefaultRegionType;
import edu.kit.ifv.mobitopp.dataimport.RegionType;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.emobility.ChargingDataForZone;
import edu.kit.ifv.mobitopp.simulation.emobility.LimitedChargingDataForZone;

public class ExampleZones {

	private static final Double dummyPoint = new Point2D.Double(0.0d, 0.0d);
	private static final int dummyAccessEdge = -1;
	private static final int parkingPlaces = 2;

	private final Zone someZone;
	private final Zone otherZone;

	public ExampleZones(Zone someZone, Zone otherZone) {
		super();
		this.someZone = someZone;
		this.otherZone = otherZone;
	}

	public static ExampleZones create() {
		Zone someZone = zoneWithId("1", 1);
		Zone otherZone = zoneWithId("2", 2);
		return new ExampleZones(someZone, otherZone);
	}

	public static Zone zoneWithId(final String id, final int oid) {
		ZoneId zoneId = new ZoneId(id, oid);
		String name = "zone " + id;
		AreaType areaType = ZoneAreaType.METROPOLITAN;
		RegionType regionType = new DefaultRegionType(1);
		ZoneClassificationType classification = ZoneClassificationType.studyArea;
		Attractivities attractivities = new Attractivities();
		ChargingDataForZone charging = createChargingData();
		boolean isDestination = true;
		Location centroid = new Location(dummyPoint, dummyAccessEdge, 0.0d);
		ZoneProperties zoneProperties = new ZoneProperties(name, areaType, regionType, classification,
				parkingPlaces, isDestination, centroid, Map.of());
		return new Zone(zoneId, zoneProperties, attractivities, charging);
	}

	public static Zone zoneClassification(final ZoneClassificationType classification) {
		ZoneId zoneId = new ZoneId("1", 1);
		String name = "zone 1";
		AreaType areaType = ZoneAreaType.METROPOLITAN;
		RegionType regionType = new DefaultRegionType(1);
		Attractivities attractivities = new Attractivities();
		ChargingDataForZone charging = createChargingData();
		boolean isDestination = true;
		Location centroid = new Location(dummyPoint, dummyAccessEdge, 0.0d);
		ZoneProperties zoneProperties = new ZoneProperties(name, areaType, regionType, classification,
				parkingPlaces, isDestination, centroid, Map.of());
		return new Zone(zoneId, zoneProperties, attractivities, charging);
	}

	private static LimitedChargingDataForZone createChargingData() {
		return new LimitedChargingDataForZone(emptyList(), DefaultPower.zero);
	}

	public Zone someZone() {
		return someZone;
	}

	public Zone otherZone() {
		return otherZone;
	}

}
