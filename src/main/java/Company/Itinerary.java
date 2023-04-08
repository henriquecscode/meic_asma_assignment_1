package Company;

import Network.Location.City;
import Network.Location.House;
import Network.Location.Location;
import Network.Location.Port;
import Network.Network;

import java.util.ArrayList;
import java.util.List;

public class Itinerary {
    private final House start;
    private final House end;
    private final List<Location> route;

    public Itinerary(House start, House end) {
        this.start = start;
        this.end = end;
        if (start != null) {
            this.route = calculateRoute();
        } else {
            this.route = new ArrayList<>();
            this.route.add(end);
        }

    }

    public Itinerary(Network network, String itinerary) {
        String[] locations = itinerary.split(",");
        end = (House) network.getLocation(locations[locations.length - 1]);
        if (locations.length > 1) {
            start = (House) network.getLocation(locations[0]);
        } else {
            start = null;
        }
        if (start != null) {
            this.route = calculateRoute();
        } else {
            this.route = new ArrayList<>();
            this.route.add(end);
        }
    }

    private List<Location> calculateRoute() {
        House startHouse = start;
        City startCity = startHouse.getCity();
        Port startPort = startCity.getPort();

        House endHouse = end;
        City endCity = endHouse.getCity();
        Port endPort = endCity.getPort();

        List<Location> route = new ArrayList<>();

        route.add(startHouse);
        if (startHouse.equals(endHouse)) {
            return route;
        }

        route.add(startCity);
        if (startCity.equals(endCity)) {
            route.add(endHouse);
            return route;
        }

        route.add(startPort);
        if (!startPort.equals(endPort)) {
            route.add(endPort);
        }

        route.add(endCity);
        route.add(endHouse);

        return route;
    }

    @Override
    public String toString() {
        return String.join(",", route.stream().map(Location::getName).toArray(String[]::new));
    }

    public List<Location> getLocations() {
        return route;
    }

    public Integer getSize() {
        return route.size() - 1;
    }

    public House getStart() {
        return start;
    }

    public House getEnd() {
        return end;
    }
}
