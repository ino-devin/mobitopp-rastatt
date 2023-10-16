package edu.kit.ifv.mobitopp.simulation.modechoice;

import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.StandardMode;

import java.util.HashMap;

public class RastattModeResolver implements ModeResolver {

        private final HashMap<String, Mode> mapping = new HashMap<>();
        private final HashMap<Mode, String> reverseMapping = new HashMap<>();

        public RastattModeResolver() {
            mapping.put("Bike", StandardMode.BIKE);
            mapping.put("BikeSharing", StandardMode.BIKESHARING);
            mapping.put("Car", StandardMode.CAR);
            mapping.put("Carsharing_FreeFloating", StandardMode.CARSHARING_FREE);
            mapping.put("Carsharing_Station", StandardMode.CARSHARING_STATION);
            mapping.put("Passenger", StandardMode.PASSENGER);
            mapping.put("Pedestrian", StandardMode.PEDESTRIAN);
            mapping.put("PublicTransport", StandardMode.PUBLICTRANSPORT);
            mapping.put("RidePooling", StandardMode.RIDE_POOLING);
            mapping.put("Taxi", StandardMode.TAXI);

            mapping.keySet().forEach(s -> reverseMapping.put(mapping.get(s), s));
        }





        @Override
        public Mode resolve(String mode) {
            if (mapping.containsKey(mode)) {
                return mapping.get(mode);
            }
            throw new IllegalArgumentException(mode + " cannot be resolved to a mode.");
        }

        @Override
        public String getString(Mode mode) {
            if (reverseMapping.containsKey(mode)) {
                return reverseMapping.get(mode);
            }
            throw new IllegalArgumentException(mode + " has no associated string.");
        }
}
