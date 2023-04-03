package Network.Route;

import Network.Location.Location;
import Network.Network;

import java.util.ArrayList;
import java.util.List;

public class Itinerary {
    private final List<Location> locations;

    public Itinerary(List<Location> locations) {
        this.locations = locations;
    }

    public Itinerary(Network network, String locationNames) {
        List<String> locationCoords = List.of(locationNames.split(","));
        locations = new ArrayList<>();

        for (String locationCoord : locationCoords) {
            locations.add(network.getLocation(locationCoord));
        }
    }

    @Override
    public String toString() {
        return String.join(",", locations.stream().map(Location::getName).toArray(String[]::new));
    }

    public List<Location> getLocations() {
        return locations;
    }

    public Integer getSize() {
        return locations.size() - 1;
    }
}
