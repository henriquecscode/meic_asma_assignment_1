package Company;

import Network.Location.City;
import Network.Location.House;
import Network.Location.Location;
import Network.Location.Port;

import java.util.ArrayList;
import java.util.List;

public class Request {
    private final House start;
    private final House end;
    private String productName;
    private int quantity;

    private final List<Location> route;

    public Request(House start, House end, String productName, int quantity) {
        this.start = start;
        this.end = end;
        this.productName = productName;
        this.quantity = quantity;
        this.route = this.calculateRoute();
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

    public House getStart() {
        return start;
    }

    public House getEnd() {
        return end;
    }

    public List<Location> getRoute() {
        return route;
    }
}
